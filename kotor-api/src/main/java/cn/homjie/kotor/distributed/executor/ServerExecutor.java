package cn.homjie.kotor.distributed.executor;

/**
 * @Class ServerExecutor
 * @Description 服务端事务执行器
 * @Author JieHong
 * @Date 2017年3月9日 下午7:18:44
 */
public interface ServerExecutor {

	/**
	 * @return true = Ok，false = Ex
	 * @Title check
	 * @Description 状态检查
	 * @Author JieHong
	 * @Date 2017年3月9日 下午8:10:11
	 */
	public boolean check();

}
