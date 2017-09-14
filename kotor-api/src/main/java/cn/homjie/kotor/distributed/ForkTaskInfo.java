package cn.homjie.kotor.distributed;

import java.io.Serializable;

/**
 * @Class ForkTaskInfo
 * @Description 分支任务信息
 * @Author JieHong
 * @Date 2017年3月11日 下午5:07:27
 */
public class ForkTaskInfo<T> implements Serializable, Comparable<ForkTaskInfo<T>> {

	private static final long serialVersionUID = -5228081734361376651L;

	private String id;
	private String descriptionId;
	private String childDescription;

	private int order;

	private TaskResult<T> result;
	// deserialize in client
	private boolean success;
	private byte[] rtbytes;

	private String taskStatus;
	private String stackTrace;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescriptionId() {
		return descriptionId;
	}

	public void setDescriptionId(String descriptionId) {
		this.descriptionId = descriptionId;
	}

	public String getChildDescription() {
		return childDescription;
	}

	public void setChildDescription(String childDescription) {
		this.childDescription = childDescription;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public TaskResult<T> getResult() {
		return result;
	}

	public void setResult(TaskResult<T> result) {
		this.result = result;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public byte[] getRtbytes() {
		return rtbytes;
	}

	public void setRtbytes(byte[] rtbytes) {
		this.rtbytes = rtbytes;
	}

	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	@Override
	public int compareTo(ForkTaskInfo<T> o) {
		return order - o.order;
	}

}
