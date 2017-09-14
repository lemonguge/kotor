package cn.homjie.kotor.service;

import cn.homjie.kotor.model.TxDescription;
import cn.homjie.kotor.model.TxTaskInfoWithBLOBs;

/**
 * @Class TransactionService
 * @Description 事务服务
 * @Author JieHong
 * @Date 2017年3月11日 下午5:09:31
 */
public interface TransactionService {

	void sendDescriptionMessage(TxDescription entity);

	void sendTaskInfoMessage(TxTaskInfoWithBLOBs entity);

}
