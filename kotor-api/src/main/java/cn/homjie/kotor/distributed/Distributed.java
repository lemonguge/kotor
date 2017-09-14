package cn.homjie.kotor.distributed;

import cn.homjie.kotor.KotorConfiguration;
import cn.homjie.kotor.distributed.executor.ClientExecutor;
import cn.homjie.kotor.distributed.executor.ExecutorFactory;
import cn.homjie.kotor.distributed.function.Executable;
import cn.homjie.kotor.distributed.function.NulExecutable;
import cn.homjie.kotor.distributed.function.NulPropagable;
import cn.homjie.kotor.distributed.function.Propagable;
import cn.homjie.kotor.exception.DistributedException;
import cn.homjie.kotor.util.IdGen;
import cn.homjie.kotor.util.SerializationUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Class Distributed
 * @Description 分布式任务
 * @Author JieHong
 * @Date 2017年3月11日 下午5:07:06
 */
public class Distributed {

	private Logger log = LoggerFactory.getLogger(Distributed.class);

	private Description description;
	// 事务执行器
	private ClientExecutor clientExecutor;

	public Distributed(Object... params) {
		try {
			obtainDescriptionFromParams(params);
		} catch (Exception e) {
			log.error("obtain description from params throw exception", e);
		}
		clientExecutor = ExecutorFactory.createClient(description);
	}

	private void obtainDescriptionFromParams(Object... params) {
		if (params == null)
			return;

		int length = params.length;
		int point = -1;
		for (int i = 0; i < length; i++) {
			Object object = params[i];
			if (object instanceof Description) {
				description = (Description) object;
				point = i;
				break;
			}
		}

		if (description == null)
			return;
		// 执行次数 + 1
		description.incTimes();

		if (!description.firstTime())
			return;

		// 首次运行初始化

		if (description.getPid() == null) {
			// 根节点的初始化
			String root = IdGen.uuid();
			description.setId(root);
			description.setRoot(root);

			// 快速序列化为入参时状态
			description.setPoint(point);
			description.setParams(SerializationUtils.serialize(ArrayUtils.remove(params, point)));
		}

		// 获取运行类和执行方法
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		for (StackTraceElement element : stack) {
			String className = element.getClassName();
			if (!KotorConfiguration.isBelongProject(className))
				continue;
			try {
				Class<?> clazz = Class.forName(className);
				if (KotorConfiguration.markHandler().findMark(clazz)) {
					String methodName = element.getMethodName();
					description.setClassName(className);
					description.setMethodName(methodName);
					break;
				}
			} catch (ClassNotFoundException e) {
				log.error("没有找到类[" + className + "]", e);
			}
		}

	}

	public boolean firstTime() {
		return description == null ? true : description.firstTime();
	}

	public void execute(NulExecutable business) throws DistributedException {
		// default use cache
		execute(business, true);
	}

	public void execute(NulPropagable business) throws DistributedException {
		// default use cache
		execute(business, true);
	}

	public void execute(NulExecutable business, boolean cache) throws DistributedException {
		submit((Executable<Void>) business, cache);
	}

	public void execute(NulPropagable business, boolean cache) throws DistributedException {
		submit((Propagable<Void>) business, cache);
	}

	public <T> TaskResult<T> submit(Executable<T> business) throws DistributedException {
		// default use cache
		return submit(business, true);
	}

	public <T> TaskResult<T> submit(Propagable<T> business) throws DistributedException {
		// default use cache
		return submit(business, true);
	}

	public <T> TaskResult<T> submit(Executable<T> business, boolean cache) throws DistributedException {
		if (business == null)
			throw new NullPointerException("Executable business is null");
		return clientExecutor.submit(business, cache);
	}

	public <T> TaskResult<T> submit(Propagable<T> business, boolean cache) throws DistributedException {
		if (business == null)
			throw new NullPointerException("Propagable business is null");
		return clientExecutor.submit(business, cache);
	}

	public void rollback(NulExecutable rollback) {
		clientExecutor.rollback(rollback);
	}

	public void complete() {
		clientExecutor.complete();
	}

}
