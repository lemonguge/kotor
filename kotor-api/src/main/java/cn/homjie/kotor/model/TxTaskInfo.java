package cn.homjie.kotor.model;

import java.io.Serializable;

/**
 * @Class TxTaskInfo
 * @Description 事务基础任务信息
 * @Author JieHong
 * @Date 2017年3月11日 下午5:09:05
 */
public class TxTaskInfo implements Serializable, Comparable<TxTaskInfo> {

	private static final long serialVersionUID = 7057049783628400880L;

	private String id;

	private Integer order;

	private Boolean success;

	private String taskStatus;

	private String descriptionId;

	private String childDescription;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id == null ? null : id.trim();
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus == null ? null : taskStatus.trim();
	}

	public String getDescriptionId() {
		return descriptionId;
	}

	public void setDescriptionId(String descriptionId) {
		this.descriptionId = descriptionId == null ? null : descriptionId.trim();
	}

	public String getChildDescription() {
		return childDescription;
	}

	public void setChildDescription(String childDescription) {
		this.childDescription = childDescription == null ? null : childDescription.trim();
	}

	@Override
	public int compareTo(TxTaskInfo o) {
		return order - o.order;
	}
}