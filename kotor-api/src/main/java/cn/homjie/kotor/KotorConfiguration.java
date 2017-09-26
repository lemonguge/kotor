package cn.homjie.kotor;

import cn.homjie.kotor.annotation.DistributedMarkHandler;
import cn.homjie.kotor.service.TransactionService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.lang.annotation.Annotation;

public class KotorConfiguration {

	private static final KotorConfiguration INSTANCE = new KotorConfiguration();
	private static Logger log = LoggerFactory.getLogger(KotorConfiguration.class);

	private TransactionService transactionService;

	private String projectPackagePrefix;

	private DistributedMarkHandler markHandler = new DistributedMarkHandler();

	private KotorConfiguration() {
	}

	/**
	 * @Title register
	 * @Description 注册事务服务
	 * @Author JieHong
	 * @Date 2017年3月11日 下午5:12:25
	 */
	public static void register() {
		if (INSTANCE.transactionService != null)
			return;
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("consumer-beans.xml");
		context.start();
		TransactionService transactionService = context.getBean(TransactionService.class);
		if (transactionService == null)
			throw new RuntimeException("transaction service register failure");
		INSTANCE.transactionService = transactionService;
		log.info("transaction service register ok");
	}

	public static TransactionService transactionService() {
		return INSTANCE.transactionService;
	}

	public static void addMark(Class<? extends Annotation> mark) {
		INSTANCE.markHandler.register(mark);
	}

	public static DistributedMarkHandler markHandler() {
		return INSTANCE.markHandler;
	}

	/**
	 * @param projectPackagePrefix
	 * @Title projectPackagePrefix
	 * @Description 项目包前缀
	 * @Author JieHong
	 * @Date 2017年4月12日 上午9:44:16
	 */
	public static void projectPackagePrefix(String projectPackagePrefix) {
		INSTANCE.projectPackagePrefix = projectPackagePrefix;
	}

	public static boolean isBelongProject(String className) {
		if (StringUtils.isBlank(INSTANCE.projectPackagePrefix))
			return true;
		if (StringUtils.isBlank(className))
			return false;

		return className.startsWith(INSTANCE.projectPackagePrefix);
	}
}
