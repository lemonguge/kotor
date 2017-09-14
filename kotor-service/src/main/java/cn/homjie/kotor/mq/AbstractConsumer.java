package cn.homjie.kotor.mq;

import cn.homjie.kotor.transaction.KotorTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @param <T>
 * @Class AbstractConsumer
 * @Description 抽象消息消费者
 * @Author JieHong
 * @Date 2017年2月27日 下午2:26:11
 */
public abstract class AbstractConsumer<T> implements MessageListener {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private MessageConverter messageConverter;

	private TransactionTemplate transactionTemplate;

	@Autowired
	private void setTransactionTemplate(KotorTransaction KotorTransaction) {
		this.transactionTemplate = KotorTransaction.transactionTemplate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onMessage(Message message) {
		T object = null;
		try {
			object = (T) messageConverter.fromMessage(message);
		} catch (Exception e) {
			logger.error("消息转换出错");
		}
		if (object == null) {
			logger.info("接收到空报文，不处理");
		} else {
			try {
				final T entity = object;
				// handle message in transaction
				transactionTemplate.execute(new TransactionCallbackWithoutResult() {
					@Override
					protected void doInTransactionWithoutResult(TransactionStatus status) {
						handle(entity);
					}
				});
			} catch (Exception e) {
				logger.error("处理消息异常", e);
			}
		}
	}

	public abstract void handle(T entity);
}
