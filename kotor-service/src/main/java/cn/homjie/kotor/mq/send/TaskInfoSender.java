package cn.homjie.kotor.mq.send;

import cn.homjie.kotor.model.TxTaskInfoWithBLOBs;
import cn.homjie.kotor.mq.AbstractSender;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * @Class TaskInfoSender
 * @Description 任务信息发送者
 * @Author JieHong
 * @Date 2017年3月11日 下午5:00:13
 */
public class TaskInfoSender extends AbstractSender<TxTaskInfoWithBLOBs> {

	public TaskInfoSender(RabbitTemplate rabbitTemplate) {
		super(rabbitTemplate);
	}

}
