package cn.homjie.kotor.distributed.executor;

import cn.homjie.kotor.distributed.Description;
import cn.homjie.kotor.enums.Transaction;

/**
 * @Class ExecutorFactory
 * @Description 执行器工厂
 * @Author JieHong
 * @Date 2017年3月11日 下午5:05:44
 */
public class ExecutorFactory {

	public static ClientExecutor createClient(Description description) {
		if (description == null)
			return new NonDistributedExector();

		Transaction transaction = description.getTransaction();
		ClientExecutor executor = null;
		switch (transaction) {
			case ROLLBACK:
				executor = new RollbackExector(description);
				break;
			case EVENTUAL:
				executor = new EventualExector(description);
				break;
			default:
				executor = new NonDistributedExector();
				break;
		}
		return executor;
	}

	public static ServerExecutor createServer(Description tree) {
		if (tree == null)
			throw new NullPointerException("根描述数据为空");

		Transaction transaction = tree.getTransaction();
		ServerExecutor executor = null;
		switch (transaction) {
			case ROLLBACK:
				executor = new RollbackExector(tree);
				break;
			case EVENTUAL:
				executor = new EventualExector(tree);
				break;
			default:
				throw new NullPointerException("没有匹配的事务执行器");
		}
		return executor;
	}

}
