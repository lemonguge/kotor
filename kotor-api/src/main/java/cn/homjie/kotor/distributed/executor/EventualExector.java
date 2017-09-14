package cn.homjie.kotor.distributed.executor;

import cn.homjie.kotor.distributed.*;
import cn.homjie.kotor.distributed.function.NulExecutable;
import cn.homjie.kotor.enums.TaskStatus;
import cn.homjie.kotor.exception.DistributedException;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.List;

/**
 * @Class EventualExector
 * @Description 最终一致性的事务执行器
 * @Author JieHong
 * @Date 2017年3月9日 下午7:19:27
 */
public class EventualExector extends DistributedExector {

	public EventualExector(Description description) {
		super(description);
	}

	@Override
	protected <T> TaskResult<T> first(ForkTask<T> task, ForkTaskInfo<T> info) throws DistributedException {
		exec(task, info, true);
		return info.getResult();
	}

	@Override
	protected <T> TaskResult<T> retry(ForkTask<T> task, ForkTaskInfo<T> info, boolean cache) throws DistributedException {
		String taskStatus = info.getTaskStatus();
		if (!TaskStatus.SUCCESS.name().equals(taskStatus)) {
			exec(task, info, true);
		} else {
			if (!cache) {
				// if don't want to use cache result, we will get last result
				exec(task, info, false);
				return info.getResult();
			}
			// keep point children index ok
			if (info.getChildDescription() != null)
				propagate.acquire();
		}
		return info.getResult();
	}

	private <T> void exec(ForkTask<T> task, ForkTaskInfo<T> info, boolean send) {
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
			info.setStackTrace(null);

			if (send)
				sendOkTaskInfo(info, result);
		} catch (DistributedException e) {
			log.error("Ignore by exception");

			info.setResult(TaskResult.ex(e.getCause()));
			info.setTaskStatus(TaskStatus.EVENTUAL_IGNORE.name());

			if (send)
				sendExTaskInfo(info, e);
		} catch (Exception e) {
			log.error("Exception Catch [{}]", e.getMessage());

			info.setResult(TaskResult.ex(e));
			info.setTaskStatus(TaskStatus.EVENTUAL_EXCEPTION.name());
			info.setStackTrace(ExceptionUtils.getStackTrace(e));

			if (send)
				sendExTaskInfo(info, e);
		}
	}

	@Override
	public void rollback(NulExecutable rollback) {
	}

	@Override
	public synchronized void complete() {
		if (complete)
			return;
		complete = true;
		sendDescription();
	}

	@Override
	protected void buildParentByChildren(ForkTaskInfo<?> parent, List<ForkTaskInfo<?>> children) {
		if (!TaskStatus.SUCCESS.name().equals(parent.getTaskStatus()))
			return;
		boolean allOk = children.stream().allMatch(info -> TaskStatus.SUCCESS.name().equals(info.getTaskStatus()));
		if (allOk)
			return;
		parent.setTaskStatus(TaskStatus.EVENTUAL_FAILURE.name());
	}

	@Override
	protected boolean checkAfterBuild(Description tree) {
		return tree.getInfos().stream().allMatch(info -> TaskStatus.SUCCESS.name().equals(info.getTaskStatus()));
	}

}
