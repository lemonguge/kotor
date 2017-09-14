package cn.homjie.kotor;

import cn.homjie.kotor.dao.TxRootStatusDao;
import cn.homjie.kotor.model.TxRootStatus;
import cn.homjie.kotor.util.DistributedUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.*;

/**
 * @Class DistributedManager
 * @Description 分布式管理器
 * @Author JieHong
 * @Date 2017年3月11日 下午5:04:52
 */
@Component
public class DistributedManager {

	public static final String LOCK_PREX = "transaction:root:";
	// consumers
	private static final int CONCURRENT = 15;
	// root message will store here
	private final BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
	private final Executor executor = Executors.newFixedThreadPool(CONCURRENT + 1);
	private Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	private RedissonClient redisson;
	@Autowired
	private TxRootStatusDao txRootStatusDao;

	@Autowired
	private DistributedChain distributedChain;

	public void doStart() {
		executor.execute(new ProducerTask());
		for (int i = 0; i < CONCURRENT; i++) {
			executor.execute(new ConsumerTask());
		}
	}

	private class ProducerTask implements Runnable {
		@Override
		public void run() {
			log.info("Producer[{}] Run", Thread.currentThread().getName());
			List<String> condition = DistributedUtils.RETRY_STATUS_LIST;
			while (true) {
				try {
					if (queue.size() == 0) {
						List<TxRootStatus> list = txRootStatusDao.selectBaseByStatusList(condition);
						if (!CollectionUtils.isEmpty(list))
							for (TxRootStatus trs : list)
								queue.put(trs.getRoot());
					}
					// check every one minute
					TimeUnit.SECONDS.sleep(60);
				} catch (Exception e) {
					log.error("生产中断", e);
				}
			}
		}
	}

	private class ConsumerTask implements Runnable {
		@Override
		public void run() {
			log.info("Consumer[{}] Run", Thread.currentThread().getName());
			while (true) {
				try {
					// waiting available
					String root = queue.take();
					RLock lock = redisson.getLock(LOCK_PREX + root);
					// max lock 5 minute
					if (lock.tryLock(0, 5, TimeUnit.MINUTES))
						try {
							distributedChain.filter(root);
						} catch (Exception e) {
							log.error("消费[" + root + "]异常", e);
						} finally {
							lock.unlock();
						}
				} catch (Exception e) {
					log.error("消费中断", e);
				}
			}
		}
	}

}
