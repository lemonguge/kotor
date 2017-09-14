package cn.homjie.kotor.distributed;

import cn.homjie.kotor.enums.Transaction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Class Description
 * @Description 服务信息
 * @Author JieHong
 * @Date 2017年1月11日 下午4:05:10
 */
public class Description implements Serializable, Comparable<Description> {

	private static final long serialVersionUID = -3941038773069693258L;

	// 事务处理
	private Transaction transaction;
	// 方法参数
	private byte[] params;
	// 在方法参数的位置
	private int point;
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
	private int level = 1;
	// 顺序
	private int order;

	// 执行次数
	private int times = 0;

	// 所有任务信息
	private List<ForkTaskInfo<?>> infos = new ArrayList<>();
	// 所有子服务信息
	private List<Description> children = new ArrayList<>();

	@SuppressWarnings("unused")
	private Description() {
		// default constructor for dubbo
	}

	public Description(Transaction transaction) {
		if (transaction == null)
			throw new NullPointerException("事务执行不能为空");
		this.transaction = transaction;
	}

	public boolean firstTime() {
		return times == 1;
	}

	void incTimes() {
		times++;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public byte[] getParams() {
		return params;
	}

	public void setParams(byte[] params) {
		this.params = params;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	public List<ForkTaskInfo<?>> getInfos() {
		return infos;
	}

	public void setInfos(List<ForkTaskInfo<?>> infos) {
		this.infos = infos;
	}

	public List<Description> getChildren() {
		return children;
	}

	public void setChildren(List<Description> children) {
		this.children = children;
	}

	@Override
	public int compareTo(Description o) {
		return order - o.order;
	}

}
