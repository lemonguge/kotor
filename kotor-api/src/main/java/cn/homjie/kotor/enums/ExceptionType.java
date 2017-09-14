package cn.homjie.kotor.enums;

/**
 * @Class ExceptionType
 * @Description 分布式异常类型
 * @Author JieHong
 * @Date 2017年3月11日 下午5:07:51
 */
public enum ExceptionType {

	DEFAULT_EXCEPTION,

	EVENTUAL_EXCEPTION,

	ROLLBACK_SUCCESS,

	ROLLBACK_FAILURE;
}
