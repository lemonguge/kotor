package cn.homjie.kotor.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.support.CorrelationData;

/**
 * @param <T>
 * @Class AbstractSender
 * @Description 消息发送适配器
 * @Author JieHong
 * @Date 2017年2月27日 下午2:26:30
 */
public abstract class AbstractSender<T> {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected RabbitTemplate rabbitTemplate;

	public AbstractSender(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	public void send(T message) {
		try {
			if (!rabbitTemplate.isConfirmListener())
				rabbitTemplate.setConfirmCallback(new ConfirmCallback() {
					@Override
					public void confirm(CorrelationData correlationData, boolean ack, String cause) {
						if (!ack) {
							rabbitTemplate.convertAndSend(message);
						}
					}
				});
			rabbitTemplate.convertAndSend(message);
		} catch (Exception e) {
			logger.error("消息发送失败：" + e);
		}

	}

}
