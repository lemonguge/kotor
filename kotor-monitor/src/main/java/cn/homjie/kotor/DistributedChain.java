package cn.homjie.kotor;

import cn.homjie.kotor.dao.TxRootStatusDao;
import cn.homjie.kotor.enums.RootStatus;
import cn.homjie.kotor.filter.CheckFilter;
import cn.homjie.kotor.filter.DistributedFilter;
import cn.homjie.kotor.filter.IgnoreFilter;
import cn.homjie.kotor.filter.ReadyFilter;
import cn.homjie.kotor.model.TxRootStatus;
import cn.homjie.kotor.transaction.KotorTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @Class DistributedChain
 * @Description 分布式过滤处理链
 * @Author JieHong
 * @Date 2017年3月11日 下午5:04:27
 */
@Component
public class DistributedChain {

	private Logger log = LoggerFactory.getLogger(getClass());

	private TransactionTemplate transactionTemplate;

	@Autowired
	private TxRootStatusDao txRootStatusDao;

	@Autowired
	private IgnoreFilter ignoreFilter;
	@Autowired
	private ReadyFilter readyFilter;
	@Autowired
	private CheckFilter checkFilter;

	private List<DistributedFilter> filters = new ArrayList<>();

	@PostConstruct
	private void doStart() {
		// Construct the filter chain
		filters.add(ignoreFilter);
		filters.add(readyFilter);
		filters.add(checkFilter);
		log.info("DistributedChain Construct Success");
	}

	@Autowired
	private void setTransactionTemplate(KotorTransaction KotorTransaction) {
		this.transactionTemplate = KotorTransaction.transactionTemplate();
	}

	public void filter(String root) {
		TxRootStatus txRootStatus = txRootStatusDao.selectByPrimaryKey(root);
		if (txRootStatus == null)
			return;
		String status = txRootStatus.getStatus();
		if (status == null)
			return;
		RootStatus key = RootStatus.valueOf(status);

		transactionTemplate.execute(new TransactionAction(key, txRootStatus));

	}

	private class TransactionAction extends TransactionCallbackWithoutResult {
		RootStatus key;
		TxRootStatus txRootStatus;

		TransactionAction(RootStatus key, TxRootStatus txRootStatus) {
			this.key = key;
			this.txRootStatus = txRootStatus;
		}

		@Override
		protected void doInTransactionWithoutResult(TransactionStatus status) {
			for (DistributedFilter filter : filters) {
				if (filter.service(key, txRootStatus))
					break;
			}
		}

	}
}
