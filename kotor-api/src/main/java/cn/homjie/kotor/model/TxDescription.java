package cn.homjie.kotor.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Class TxDescription
 * @Description 事务描述
 * @Author JieHong
 * @Date 2017年3月11日 下午5:08:37
 */
public class TxDescription implements Serializable, Comparable<TxDescription> {

	private static final long serialVersionUID = 6832571090291572690L;

	// 事务处理
	private String transaction;
	// 方法参数
	private byte[] params;
	// 在方法参数的位置
	private Integer point;
	// 服务类名
	private String className;
	// 方法名
	private String methodName;

	// 当前主键
	private String id;
	// 父级主键
	private String pid;
	// 根主键
	private String root;
	// 级别
	private Integer level;
	// 顺序
	private Integer order;

	// 任务信息个数
	private Integer infosSize;
	// 子服务信息个数
	private Integer childSize;

	// 执行次数
	private Integer times;

	private Date createDate;

	private Date updateDate;

	private Boolean delFlag;

	private List<TxTaskInfoWithBLOBs> infos;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id == null ? null : id.trim();
	}

	public String getTransaction() {
		return transaction;
	}

	public void setTransaction(String transaction) {
		this.transaction = transaction == null ? null : transaction.trim();
	}

	public Integer getPoint() {
		return point;
	}

	public void setPoint(Integer point) {
		this.point = point;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className == null ? null : className.trim();
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName == null ? null : methodName.trim();
	}

	public Integer getInfosSize() {
		return infosSize;
	}

	public void setInfosSize(Integer infosSize) {
		this.infosSize = infosSize;
	}

	public Integer getChildSize() {
		return childSize;
	}

	public void setChildSize(Integer childSize) {
		this.childSize = childSize;
	}

	public Integer getTimes() {
		return times;
	}

	public void setTimes(Integer times) {
		this.times = times;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid == null ? null : pid.trim();
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root == null ? null : root.trim();
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Boolean getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Boolean delFlag) {
		this.delFlag = delFlag;
	}

	public byte[] getParams() {
		return params;
	}

	public void setParams(byte[] params) {
		this.params = params;
	}

	public List<TxTaskInfoWithBLOBs> getInfos() {
		return infos;
	}

	public void setInfos(List<TxTaskInfoWithBLOBs> infos) {
		this.infos = infos;
	}

	@Override
	public int compareTo(TxDescription o) {
		return order - o.order;
	}
}