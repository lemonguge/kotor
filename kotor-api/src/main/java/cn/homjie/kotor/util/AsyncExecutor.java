package cn.homjie.kotor.util;

import cn.homjie.kotor.KotorConfiguration;
import cn.homjie.kotor.distributed.Description;
import cn.homjie.kotor.model.TxDescription;
import cn.homjie.kotor.model.TxTaskInfoWithBLOBs;
import cn.homjie.kotor.service.TransactionService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Class AsyncExecutor
 * @Description 异步发送执行器
 * @Author JieHong
 * @Date 2017年5月19日 下午3:17:16
 */
public class AsyncExecutor {

	private static final AsyncExecutor instance = new AsyncExecutor();

	private ExecutorService executors = Executors.newCachedThreadPool();

	private AsyncExecutor() {
	}

	public static void send(Description description) {
		final TransactionService service = KotorConfiguration.transactionService();
		if (service == null)
			return;
		final TxDescription entity = DistributedUtils.convert(description);
		instance.executors.execute(() -> service.sendDescriptionMessage(entity));
	}

	public static void send(final TxTaskInfoWithBLOBs entity) {
		final TransactionService service = KotorConfiguration.transactionService();
		instance.executors.execute(() -> service.sendTaskInfoMessage(entity));
	}

}
