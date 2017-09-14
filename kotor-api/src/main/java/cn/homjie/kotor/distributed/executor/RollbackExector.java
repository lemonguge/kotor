package cn.homjie.kotor.distributed.executor;

import cn.homjie.kotor.distributed.*;
import cn.homjie.kotor.distributed.function.NulExecutable;
import cn.homjie.kotor.enums.ExceptionType;
import cn.homjie.kotor.enums.TaskStatus;
import cn.homjie.kotor.exception.DistributedException;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.List;

/**
 * @Class RollbackExector
 * @Description 异常回滚的事务执行器
 * @Author JieHong
 * @Date 2017年3月9日 下午7:19:49
 */
public class RollbackExector extends DistributedExector {

	public RollbackExector(Description description) {
		super(description);
	}

	@Override
	protected <T> TaskResult<T> first(ForkTask<T> task, ForkTaskInfo<T> info) throws DistributedException {

		Throwable ex = null;
		try {
			T result = null;
			if (task.isBusinessExecutable()) {
				result = task.getExecutable().handle();
			} else {
				Propagate propagate = task.getPropagate();
				result = task.getPropagable().handle(propagate);
			}
			info.setResult(TaskResult.ok(result));
			info.setTaskStatus(TaskStatus.SUCCESS.name());
			// 执行成功，立即返回
			return info.getResult();
		} catch (DistributedException e) {
			// 回滚成功的几率更大，减少判断
			if (e.isRollbackSuccess())
				info.setTaskStatus(TaskStatus.ROLLBACK_SUCCESS.name());
			else if (e.isRollbackFailure())
				info.setTaskStatus(TaskStatus.ROLLBACK_FAILURE.name());

			ex = e;
		} catch (Exception e) {
			// 原始异常
			info.setTaskStatus(TaskStatus.EXCEPTION.name());
			info.setStackTrace(ExceptionUtils.getStackTrace(e));
			ex = e;
		}

		sendDescription();
		sendExTaskInfo(info, ex);

		// 回滚是否出现过异常
		boolean rollbackException = false;
		for (ForkTask<?> forktask : tasks) {
			if (task == forktask)
				break;
			NulExecutable rollback = forktask.getRollback();
			if (rollback != null) {
				try {
					rollback.handle();
					info.setTaskStatus(TaskStatus.ROLLBACK_SUCCESS.name());

					sendOkTaskInfo(info);
				} catch (Exception e) {
					rollbackException = true;
					info.setTaskStatus(TaskStatus.ROLLBACK_EXCEPTION.name());
					info.setStackTrace(ExceptionUtils.getStackTrace(e));

					sendExTaskInfo(info, e);
				}
			} else {
				info.setTaskStatus(TaskStatus.ROLLBACK_NOTFIND.name());

				sendOkTaskInfo(info);
			}
		}

		if (ex instanceof DistributedException) {
			if (rollbackException)
				// 回滚失败，获取原始异常
				ex = ex.getCause();
			else
				throw (DistributedException) ex;
		}
		if (rollbackException)
			throw new DistributedException(ex, ExceptionType.ROLLBACK_FAILURE);
		else
			throw new DistributedException(ex, ExceptionType.ROLLBACK_SUCCESS);
	}

	@Override
	protected <T> TaskResult<T> retry(ForkTask<T> task, ForkTaskInfo<T> info, boolean cache) throws DistributedException {
		String taskStatus = info.getTaskStatus();
		// 未执行的任务忽略处理
		if (taskStatus == null)
			return TaskResult.nul();
		if (TaskStatus.ROLLBACK_FAILURE.name().equals(taskStatus)) {
			try {
				if (task.isBusinessExecutable()) {
					task.getExecutable().handle();
				} else {
					Propagate propagate = task.getPropagate();
					task.getPropagable().handle(propagate);
				}
			} catch (Exception e) {
				log.error("[ROLLBACK_FAILURE]处理业务异常", e);
			}
			return TaskResult.nul();
		} else if (TaskStatus.ROLLBACK_EXCEPTION.name().equals(taskStatus)) {
			try {
				task.getRollback().handle();
				info.setTaskStatus(TaskStatus.ROLLBACK_SUCCESS.name());
				info.setStackTrace(null);

				sendOkTaskInfo(info);
			} catch (Exception e) {
				info.setTaskStatus(TaskStatus.ROLLBACK_EXCEPTION.name());
				info.setStackTrace(ExceptionUtils.getStackTrace(e));

				sendExTaskInfo(info, e);
			}

			return TaskResult.nul();
		} else {
			// keep point children index ok
			if (info.getChildDescription() != null)
				propagate.acquire();
			// default use cache
			return info.getResult();
		}
	}

	@Override
	public void rollback(NulExecutable rollback) {
		tasks.get(tasks.size() - 1).setRollback(rollback);
	}

	@Override
	public void complete() {
		complete = true;
	}

	@Override
	protected void buildParentByChildren(ForkTaskInfo<?> parent, List<ForkTaskInfo<?>> children) {
		if (TaskStatus.ROLLBACK_FAILURE.name().equals(parent.getTaskStatus())) {
			boolean anyEx = children.stream().anyMatch(info -> {
				String taskStatus = info.getTaskStatus();
				return TaskStatus.ROLLBACK_EXCEPTION.name().equals(taskStatus) || TaskStatus.ROLLBACK_FAILURE.name().equals(taskStatus);
			});
			if (!anyEx) {
				parent.setStackTrace(TaskStatus.ROLLBACK_SUCCESS.name());
			}
		}
	}

	@Override
	protected boolean checkAfterBuild(Description tree) {
		return tree.getInfos().stream().allMatch(info -> {
			String taskStatus = info.getTaskStatus();
			return TaskStatus.ROLLBACK_SUCCESS.name().equals(taskStatus) || TaskStatus.ROLLBACK_NOTFIND.name().equals(taskStatus);
		});
	}

}
