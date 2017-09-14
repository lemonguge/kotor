package cn.homjie.kotor.distributed.function;

import cn.homjie.kotor.distributed.Propagate;

/**
 * @Class NulPropagable
 * @Description 可传播的普通业务
 * @Author JieHong
 * @Date 2017年3月11日 下午5:06:25
 */
@FunctionalInterface
public interface NulPropagable extends Propagable<Void> {

	default Void handle(Propagate propagate) throws Exception {
		handleWithoutResult(propagate);
		return null;
	}

	void handleWithoutResult(Propagate propagate) throws Exception;

}