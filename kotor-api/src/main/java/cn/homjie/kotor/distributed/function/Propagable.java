package cn.homjie.kotor.distributed.function;

import cn.homjie.kotor.distributed.Propagate;

/**
 * @param <T>
 * @Class Propagable
 * @Description 可传播的业务
 * @Author JieHong
 * @Date 2017年3月11日 下午5:06:01
 */
@FunctionalInterface
public interface Propagable<T> {

	T handle(Propagate propagate) throws Exception;

}