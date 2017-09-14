package cn.homjie.kotor.distributed;

import cn.homjie.kotor.exception.DistributedException;

import java.io.Serializable;

import static cn.homjie.kotor.enums.ExceptionType.EVENTUAL_EXCEPTION;

/**
 * 任务结果
 *
 * @param <T>
 * @author HomJie
 */
public class TaskResult<T> implements Serializable {

	private static final long serialVersionUID = -3144849695440150966L;

	private T result;

	private DistributedException ex;

	private boolean ok;

	private TaskResult() {
	}

	public static <R> TaskResult<R> ok(R result) {
		TaskResult<R> tr = new TaskResult<R>();
		tr.result = result;
		tr.ok = true;
		return tr;
	}

	public static <R> TaskResult<R> ex(Throwable ex) {
		TaskResult<R> tr = new TaskResult<R>();
		tr.ex = new DistributedException(ex, EVENTUAL_EXCEPTION);
		tr.ok = false;
		return tr;
	}

	public static <R> TaskResult<R> nul() {
		TaskResult<R> tr = new TaskResult<R>();
		tr.ok = true;
		return tr;
	}

	public boolean isOk() {
		return ok;
	}

	/**
	 * @return 任务结果是否可用
	 */
	public T get() {
		if (ok)
			return result;
		else
			throw ex;
	}

	/**
	 * @return 获取异常
	 */
	public Throwable cause() {
		if (ok)
			return null;
		return ex.getCause();
	}

}
