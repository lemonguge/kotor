package cn.homjie.kotor.distributed;

/**
 * 事务传播
 *
 * @author HomJie
 */
public interface Propagate {

	/**
	 * @return 获取传播描述
	 */
	Description acquire();

}
