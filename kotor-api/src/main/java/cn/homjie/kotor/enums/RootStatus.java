package cn.homjie.kotor.enums;

/**
 * @Class RootStatus
 * @Description 根描述状态
 * @Author JieHong
 * @Date 2017年3月11日 下午5:08:01
 */
public enum RootStatus {

	CHECK_WAIT("等待检查"),

	CHECK_OK("检查完成"),

	RETRY_FAILED("重试异常"),

	RETRY_CHECK_WAIT("重试待检查"),

	RETRY_CHECK_OK("重试完成");

	private String memo;

	private RootStatus(String memo) {
		this.memo = memo;
	}

	public String getMemo() {
		return memo;
	}

}
