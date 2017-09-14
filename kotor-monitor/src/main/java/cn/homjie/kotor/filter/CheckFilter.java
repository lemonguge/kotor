package cn.homjie.kotor.filter;

import cn.homjie.kotor.DistributedRemote;
import cn.homjie.kotor.distributed.Description;
import cn.homjie.kotor.distributed.executor.ExecutorFactory;
import cn.homjie.kotor.distributed.executor.ServerExecutor;
import cn.homjie.kotor.enums.RootStatus;
import cn.homjie.kotor.model.TxDescription;
import cn.homjie.kotor.model.TxRootStatus;
import cn.homjie.kotor.util.DistributedUtils;
import cn.homjie.kotor.util.RateLimiter;
import com.alibaba.dubbo.remoting.TimeoutException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import static cn.homjie.kotor.enums.RootStatus.*;

/**
 * @Class CheckFilter
 * @Description 检查过滤器
 * @Author JieHong
 * @Date 2017年3月11日 下午5:02:19
 */
@Component
public class CheckFilter extends AbstractFilter {

	private ConcurrentMap<String, RateLimiter> lockWait = new ConcurrentHashMap<>();

	@Override
	protected boolean permit(RootStatus key) {
		return CHECK_WAIT == key || RETRY_CHECK_WAIT == key;
	}

	@Override
	protected boolean handle(TxRootStatus txRootStatus) {
		String id = txRootStatus.getRoot();
		List<TxDescription> list = txDescriptionDao.selectTotalByRoot(id);
		Description tree = DistributedUtils.tree(list);
		String className = tree.getClassName();
		Object service = DistributedRemote.get(className);
		if (service == null)
			return true;
		ServerExecutor serverExecutor = ExecutorFactory.createServer(tree);
		boolean ok = serverExecutor.check();

		TxRootStatus prevUpdate = new TxRootStatus();
		prevUpdate.setRoot(id);
		if (ok) {
			// only fast change root status
			String waitStatus = txRootStatus.getStatus();
			if (CHECK_WAIT.name().equals(waitStatus))
				prevUpdate.setStatus(CHECK_OK.name());
			else
				prevUpdate.setStatus(RETRY_CHECK_OK.name());
		} else {
			prevUpdate.setTimes(txRootStatus.getTimes() + 1);
		}
		txRootStatusDao.updateByPrimaryKeySelective(prevUpdate);
		if (ok)
			return true;

		TxRootStatus suffUpdate = new TxRootStatus();
		suffUpdate.setRoot(id);
		try {
			String methodName = tree.getMethodName();
			Object[] args = (Object[]) SerializationUtils.deserialize(tree.getParams());
			Object[] params = ArrayUtils.insert(tree.getPoint(), args, tree);

			// avoid call lock waiting
			waitQueue(className + ":" + methodName);
			// reflect invoke service method
			DistributedUtils.invokeMethod(service, methodName, params);

			suffUpdate.setStatus(RETRY_CHECK_WAIT.name());
		} catch (Exception e) {
			Throwable rc = DistributedUtils.getRootCause(e);
			if (rc instanceof TimeoutException) {
				log.info("Get remote service timeout.");
				return true;
			}
			suffUpdate.setStatus(RETRY_FAILED.name());
			suffUpdate.setStackTrace(ExceptionUtils.getStackTrace(e));
		}
		txRootStatusDao.updateByPrimaryKeySelective(suffUpdate);
		return true;
	}

	private void waitQueue(String remote) {
		RateLimiter origin = new RateLimiter(1, 5, TimeUnit.SECONDS);
		RateLimiter limiter = lockWait.putIfAbsent(remote, origin);
		if (limiter == null)
			limiter = origin;
		limiter.acquire();
	}

}
