package cn.homjie.kotor.distributed.function;

/**
 * @Class NulExecutable
 * @Description 普通业务
 * @Author JieHong
 * @Date 2017年3月11日 下午5:06:50
 */
@FunctionalInterface
public interface NulExecutable extends Executable<Void> {

	default Void handle() throws Exception {
		handleWithoutResult();
		return null;
	}

	void handleWithoutResult() throws Exception;

}