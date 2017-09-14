package cn.homjie.kotor.filter;

import cn.homjie.kotor.enums.RootStatus;
import cn.homjie.kotor.model.TxRootStatus;

/**
 * @Class DistributedFilter
 * @Description 分布式过滤处理器
 * @Author JieHong
 * @Date 2017年3月9日 下午4:28:19
 */
public interface DistributedFilter {

	/**
	 * @param key
	 * @param txRootStatus
	 * @return true = 不再处理，false = 继续处理
	 * @Title service
	 * @Description 处理根描述
	 * @Author JieHong
	 * @Date 2017年3月9日 下午4:21:26
	 */
	public boolean service(RootStatus key, TxRootStatus txRootStatus);

}
