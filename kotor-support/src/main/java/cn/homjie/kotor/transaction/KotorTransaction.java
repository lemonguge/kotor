package cn.homjie.kotor.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 事务操作
 *
 * @author HomJie
 */
@Component
public class KotorTransaction {

	private TransactionTemplate transactionTemplate;

	@Autowired
	private void setTransactionTemplate(PlatformTransactionManager transactionManager) {
		this.transactionTemplate = new TransactionTemplate(transactionManager);
	}

	public TransactionTemplate transactionTemplate() {
		return transactionTemplate;
	}
}
