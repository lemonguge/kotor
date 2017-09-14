package cn.homjie.kotor.model;

/**
 * @Class TxTaskInfoWithBLOBs
 * @Description 事务详细任务信息
 * @Author JieHong
 * @Date 2017年3月11日 下午5:09:19
 */
public class TxTaskInfoWithBLOBs extends TxTaskInfo {

	private static final long serialVersionUID = 6168476725733162403L;

	private byte[] result;

	private String stackTrace;

	public byte[] getResult() {
		return result;
	}

	public void setResult(byte[] result) {
		this.result = result;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace == null ? null : stackTrace.trim();
	}
}