package cn.homjie.kotor.filter;

import cn.homjie.kotor.enums.RootStatus;
import cn.homjie.kotor.model.TxDescription;
import cn.homjie.kotor.model.TxRootStatus;
import org.springframework.stereotype.Component;

import static cn.homjie.kotor.enums.RootStatus.CHECK_WAIT;
import static cn.homjie.kotor.enums.RootStatus.RETRY_CHECK_WAIT;

/**
 * @Class ReadyFilter
 * @Description 准备过滤器
 * @Author JieHong
 * @Date 2017年3月11日 下午5:02:19
 */
@Component
public class ReadyFilter extends AbstractFilter {

	@Override
	protected boolean permit(RootStatus key) {
		return CHECK_WAIT == key || RETRY_CHECK_WAIT == key;
	}

	@Override
	protected boolean handle(TxRootStatus txRootStatus) {
		String id = txRootStatus.getRoot();
		TxDescription description = txDescriptionDao.selectByPrimaryKey(id);
		if (txRootStatus.getTimes() + 1 != description.getTimes())
			return true;
		Boolean childReady = txDescriptionDao.selectChildReady(id);
		if (childReady == null || !childReady)
			return true;
		Boolean infosReady = txDescriptionDao.selectInfosReady(id);
		if (infosReady == null || !infosReady)
			return true;
		return false;
	}

}
