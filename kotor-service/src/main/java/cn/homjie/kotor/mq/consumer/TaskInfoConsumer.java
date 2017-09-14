package cn.homjie.kotor.mq.consumer;

import cn.homjie.kotor.dao.TxRootStatusDao;
import cn.homjie.kotor.dao.TxTaskInfoDao;
import cn.homjie.kotor.model.TxTaskInfoWithBLOBs;
import cn.homjie.kotor.mq.AbstractConsumer;
import cn.homjie.kotor.util.IdGen;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Class TaskInfoConsumer
 * @Description 任务信息消费者
 * @Author JieHong
 * @Date 2017年3月11日 下午4:59:51
 */
public class TaskInfoConsumer extends AbstractConsumer<TxTaskInfoWithBLOBs> {

	@Autowired
	protected TxRootStatusDao txRootStatusDao;
	@Autowired
	private TxTaskInfoDao txTaskInfoDao;

	@Override
	public void handle(TxTaskInfoWithBLOBs entity) {
		String id = entity.getId();
		if (id == null) {
			entity.setId(IdGen.uuid());
			txTaskInfoDao.insertSelective(entity);
		} else {
			// retry success will clear stackTrace
			txTaskInfoDao.updateByPrimaryKeyWithBLOBs(entity);
		}
	}

}
