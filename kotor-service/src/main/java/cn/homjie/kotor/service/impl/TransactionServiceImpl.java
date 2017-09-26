package cn.homjie.kotor.service.impl;

import cn.homjie.kotor.model.TxDescription;
import cn.homjie.kotor.model.TxTaskInfoWithBLOBs;
import cn.homjie.kotor.mq.send.DescriptionSender;
import cn.homjie.kotor.mq.send.TaskInfoSender;
import cn.homjie.kotor.service.TransactionService;

import javax.annotation.Resource;

/**
 * @Class TransactionServiceImpl
 * @Description 事务服务
 * @Author JieHong
 * @Date 2017年3月11日 下午5:00:35
 */
public class TransactionServiceImpl implements TransactionService {

	@Resource
	private DescriptionSender descriptionSender;
	@Resource
	private TaskInfoSender taskInfoSender;

	@Override
	public void sendDescriptionMessage(TxDescription entity) {
		descriptionSender.send(entity);
	}

	@Override
	public void sendTaskInfoMessage(TxTaskInfoWithBLOBs entity) {
		taskInfoSender.send(entity);
	}

}
