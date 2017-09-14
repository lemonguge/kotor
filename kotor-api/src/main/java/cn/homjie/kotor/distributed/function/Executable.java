package cn.homjie.kotor.distributed.function;

/**
 * @param <T>
 * @Class Executable
 * @Description 普通业务
 * @Author JieHong
 * @Date 2017年3月11日 下午5:06:35
 */
@FunctionalInterface
public interface Executable<T> {

	T handle() throws Exception;

}