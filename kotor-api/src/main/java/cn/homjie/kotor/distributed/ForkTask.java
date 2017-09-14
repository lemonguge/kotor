package cn.homjie.kotor.distributed;

import cn.homjie.kotor.distributed.function.Executable;
import cn.homjie.kotor.distributed.function.NulExecutable;
import cn.homjie.kotor.distributed.function.Propagable;

/**
 * @param <T>
 * @Class ForkTask
 * @Description 分支任务
 * @Author JieHong
 * @Date 2017年3月11日 下午5:07:16
 */
public class ForkTask<T> {

	public static final int BUSINESS_EXECUTABLE = 0;
	public static final int BUSINESS_PROPAGABLE = 1;
	private Executable<T> executable;
	private Propagable<T> propagable;
	private Propagate propagate;

	private NulExecutable rollback;

	private int business = -1;

	public Executable<T> getExecutable() {
		return executable;
	}

	public void setExecutable(Executable<T> executable) {
		if (executable == null)
			return;
		this.executable = executable;
		this.business = BUSINESS_EXECUTABLE;
	}

	public Propagable<T> getPropagable() {
		return propagable;
	}

	public void setPropagable(Propagable<T> propagable) {
		if (propagable == null)
			return;
		this.propagable = propagable;
		this.business = BUSINESS_PROPAGABLE;
	}

	public Propagate getPropagate() {
		return propagate;
	}

	public void setPropagate(Propagate propagate) {
		this.propagate = propagate;
	}

	public NulExecutable getRollback() {
		return rollback;
	}

	public void setRollback(NulExecutable rollback) {
		this.rollback = rollback;
	}

	public boolean isBusinessExecutable() {
		return business == BUSINESS_EXECUTABLE;
	}

	public boolean isBusinessPropagable() {
		return business == BUSINESS_PROPAGABLE;
	}

}
