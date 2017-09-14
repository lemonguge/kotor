package cn.homjie.kotor.mq.send;

import cn.homjie.kotor.model.TxDescription;
import cn.homjie.kotor.mq.AbstractSender;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * @Class DescriptionSender
 * @Description 描述发送者
 * @Author JieHong
 * @Date 2017年3月11日 下午5:00:02
 */
public class DescriptionSender extends AbstractSender<TxDescription> {

	public DescriptionSender(RabbitTemplate rabbitTemplate) {
		super(rabbitTemplate);
	}

}
