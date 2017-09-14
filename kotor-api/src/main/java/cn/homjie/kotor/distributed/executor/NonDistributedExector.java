package cn.homjie.kotor.distributed.executor;

import cn.homjie.kotor.distributed.TaskResult;
import cn.homjie.kotor.distributed.function.Executable;
import cn.homjie.kotor.distributed.function.NulExecutable;
import cn.homjie.kotor.distributed.function.Propagable;
import cn.homjie.kotor.enums.ExceptionType;
import cn.homjie.kotor.exception.DistributedException;

/**
 * @Class NonDistributedExector
 * @Description 非分布式执行器
 * @Author JieHong
 * @Date 2017年3月10日 上午10:29:55
 */
public class NonDistributedExector implements ClientExecutor {

	private volatile boolean complete = false;

	@Override
	public <T> TaskResult<T> submit(Executable<T> business, boolean cache) throws DistributedException {
		return submit(business, null);
	}

	@Override
	public <T> TaskResult<T> submit(Propagable<T> business, boolean cache) throws DistributedException {
		return submit(null, business);
	}

	private <T> TaskResult<T> submit(Executable<T> executable, Propagable<T> propagable) throws DistributedException {
		// 任务已完成，不处理
		if (complete)
			return TaskResult.nul();

		// two business only one permit
		try {
			T result = null;
			if (executable != null) {
				result = executable.handle();
			} else {
				result = propagable.handle(null);
			}
			return TaskResult.ok(result);
		} catch (DistributedException e) {
			throw e;
		} catch (Exception e) {
			// 原始异常
			throw new DistributedException(e, ExceptionType.DEFAULT_EXCEPTION);
		}
	}

	@Override
	public void rollback(NulExecutable rollback) {
		// 不做任何事情
	}

	@Override
	public void complete() {
		complete = true;
	}

}
