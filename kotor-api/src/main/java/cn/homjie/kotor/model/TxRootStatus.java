package cn.homjie.kotor.model;

import java.io.Serializable;

/**
 * @Class TxRootStatus
 * @Description 事务根描述状态
 * @Author JieHong
 * @Date 2017年3月11日 下午5:08:49
 */
public class TxRootStatus implements Serializable {

	private static final long serialVersionUID = 1441704129559365116L;

	private String root;

	private Integer index;

	private String status;

	private Integer times;

	private Integer limit;

	private String stackTrace;

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root == null ? null : root.trim();
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status == null ? null : status.trim();
	}

	public Integer getTimes() {
		return times;
	}

	public void setTimes(Integer times) {
		this.times = times;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace == null ? null : stackTrace.trim();
	}
}