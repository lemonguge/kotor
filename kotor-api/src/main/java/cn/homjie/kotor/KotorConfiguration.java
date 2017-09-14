package cn.homjie.kotor;

import cn.homjie.kotor.annotation.DistributedMarkHandler;
import cn.homjie.kotor.service.TransactionService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;

public class KotorConfiguration {

	private static final KotorConfiguration instance = new KotorConfiguration();
	private static Logger log = LoggerFactory.getLogger(KotorConfiguration.class);
	private TransactionService transactionService;

	private String projectPackagePrefix;

	private DistributedMarkHandler markHandler = new DistributedMarkHandler();

	private KotorConfiguration() {
	}

	/**
	 * @param transactionService
	 * @Title register
	 * @Description 注册事务服务
	 * @Author JieHong
	 * @Date 2017年3月11日 下午5:12:25
	 */
	public static void register(TransactionService transactionService) {
		if (transactionService == null)
			return;
		instance.transactionService = transactionService;
		log.info("transaction service register ok");
	}

	public static TransactionService transactionService() {
		return instance.transactionService;
	}

	public static void addMark(Class<? extends Annotation> mark) {
		instance.markHandler.register(mark);
	}

	public static DistributedMarkHandler markHandler() {
		return instance.markHandler;
	}

	/**
	 * @param projectPackagePrefix
	 * @Title projectPackagePrefix
	 * @Description 项目包前缀
	 * @Author JieHong
	 * @Date 2017年4月12日 上午9:44:16
	 */
	public static void projectPackagePrefix(String projectPackagePrefix) {
		instance.projectPackagePrefix = projectPackagePrefix;
	}

	public static boolean isBelongProject(String className) {
		if (StringUtils.isBlank(instance.projectPackagePrefix))
			return true;
		if (StringUtils.isBlank(className))
			return false;

		return className.startsWith(instance.projectPackagePrefix);
	}
}
