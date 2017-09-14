package cn.homjie.kotor.exception;

import cn.homjie.kotor.enums.ExceptionType;

import static cn.homjie.kotor.enums.ExceptionType.*;

/**
 * @Class DistributedException
 * @Description 分布式异常
 * @Author JieHong
 * @Date 2017年3月11日 下午5:08:27
 */
public class DistributedException extends RuntimeException {

	private static final long serialVersionUID = 3026319152089291579L;

	private ExceptionType type;

	public DistributedException(Throwable cause, ExceptionType type) {
		super(cause);
		this.type = type;
	}

	public boolean isDefaultException() {
		return DEFAULT_EXCEPTION == type;
	}

	public boolean isEventualException() {
		return EVENTUAL_EXCEPTION == type;
	}

	public boolean isRollbackSuccess() {
		return ROLLBACK_SUCCESS == type;
	}

	public boolean isRollbackFailure() {
		return ROLLBACK_FAILURE == type;
	}

}
