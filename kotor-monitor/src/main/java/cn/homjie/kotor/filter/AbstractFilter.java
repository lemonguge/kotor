package cn.homjie.kotor.filter;

import cn.homjie.kotor.dao.TxDescriptionDao;
import cn.homjie.kotor.dao.TxRootStatusDao;
import cn.homjie.kotor.dao.TxTaskInfoDao;
import cn.homjie.kotor.enums.RootStatus;
import cn.homjie.kotor.model.TxRootStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Class AbstractFilter
 * @Description 基础分布式过滤处理器
 * @Author JieHong
 * @Date 2017年3月11日 下午5:00:55
 */
public abstract class AbstractFilter implements DistributedFilter {

	protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	protected TxDescriptionDao txDescriptionDao;
	@Autowired
	protected TxRootStatusDao txRootStatusDao;
	@Autowired
	protected TxTaskInfoDao txTaskInfoDao;

	@Override
	public final boolean service(RootStatus key, TxRootStatus txRootStatus) {
		// check this can not deal with the key
		if (!permit(key))
			return false;
		return handle(txRootStatus);
	}

	/**
	 * @param key
	 * @return true = 允许，false = 不允许
	 * @Title permit
	 * @Description 是否允许处理
	 * @Author JieHong
	 * @Date 2017年3月11日 下午5:01:14
	 */
	protected abstract boolean permit(RootStatus key);

	/**
	 * @param txRootStatus
	 * @return true = 不再处理，false = 继续处理
	 * @Title handle
	 * @Description 处理根描述状态
	 * @Author JieHong
	 * @Date 2017年3月11日 下午5:01:51
	 */
	protected abstract boolean handle(TxRootStatus txRootStatus);

}
