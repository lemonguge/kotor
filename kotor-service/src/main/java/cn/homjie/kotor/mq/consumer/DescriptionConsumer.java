package cn.homjie.kotor.mq.consumer;

import cn.homjie.kotor.dao.TxDescriptionDao;
import cn.homjie.kotor.dao.TxRootStatusDao;
import cn.homjie.kotor.model.TxDescription;
import cn.homjie.kotor.model.TxRootStatus;
import cn.homjie.kotor.mq.AbstractConsumer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static cn.homjie.kotor.enums.RootStatus.CHECK_WAIT;

/**
 * @Class DescriptionConsumer
 * @Description 描述消费者
 * @Author JieHong
 * @Date 2017年3月11日 下午4:59:40
 */
public class DescriptionConsumer extends AbstractConsumer<TxDescription> {

	public static final int RETRY_LIMIT = 15;

	@Autowired
	private TxDescriptionDao txDescriptionDao;
	@Autowired
	private TxRootStatusDao txRootStatusDao;

	@Override
	public void handle(TxDescription entity) {
		String id = entity.getId();
		Date now = new Date();
		if (firstTime(entity)) {
			entity.setCreateDate(now);
			entity.setUpdateDate(now);
			txDescriptionDao.insertSelective(entity);
			if (entity.getPid() == null) {
				TxRootStatus txRootStatus = new TxRootStatus();
				txRootStatus.setRoot(id);
				txRootStatus.setStatus(CHECK_WAIT.name());
				txRootStatus.setTimes(0);
				txRootStatus.setLimit(RETRY_LIMIT);
				txRootStatusDao.insertSelective(txRootStatus);
			}
		} else {
			entity.setUpdateDate(now);
			txDescriptionDao.updateByPrimaryKeySelective(entity);
		}

	}

	private boolean firstTime(TxDescription entity) {
		return entity.getTimes() == 1;
	}

}
