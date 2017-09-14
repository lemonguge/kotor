package cn.homjie.kotor.distributed.executor;

import cn.homjie.kotor.distributed.TaskResult;
import cn.homjie.kotor.distributed.function.Executable;
import cn.homjie.kotor.distributed.function.NulExecutable;
import cn.homjie.kotor.distributed.function.Propagable;
import cn.homjie.kotor.exception.DistributedException;

/**
 * @Class ClientExecutor
 * @Description 客户端事务执行器
 * @Author JieHong
 * @Date 2017年3月9日 下午7:18:18
 */
public interface ClientExecutor {

	<T> TaskResult<T> submit(Executable<T> business, boolean cache) throws DistributedException;

	<T> TaskResult<T> submit(Propagable<T> business, boolean cache) throws DistributedException;

	void rollback(NulExecutable rollback);

	void complete();

}
