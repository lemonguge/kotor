package cn.homjie.kotor;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Class DistributedRemote
 * @Description 分布式远程服务
 * @Author JieHong
 * @Date 2017年3月11日 下午5:05:04
 */
public class DistributedRemote {

	private static final String INGNORE_PREX = "com.alibaba.dubbo";
	private static Logger log = LoggerFactory.getLogger(DistributedRemote.class);
	private static DistributedRemote instance = new DistributedRemote();
	// interface - service
	private Map<String, Object> rpc = new HashMap<>();

	// implement - interface
	private Map<String, String> cache = new HashMap<>();

	private DistributedRemote() {
	}

	public static void register(Object service) {
		if (service == null)
			return;
		String interfaceName = extractInterface(service);
		if (interfaceName == null)
			return;
		instance.rpc.put(interfaceName, service);
		log.info("register [" + interfaceName + "] success");
	}

	private static String extractInterface(Object service) {
		List<Class<?>> interfaces = ClassUtils.getAllInterfaces(service.getClass());
		for (Class<?> clazz : interfaces) {
			String interfaceName = clazz.getName();
			if (!interfaceName.startsWith(INGNORE_PREX)) {
				return interfaceName;
			}
		}
		return null;
	}

	public static void register(String className, Object service) {
		if (service == null || className == null)
			return;
		instance.rpc.put(className, service);
		instance.cache.put(className, className);
		log.info("register [" + className + "] success");
	}

	public static Object get(String className) {
		if (StringUtils.isBlank(className))
			return null;
		String rpcKey = instance.cache.get(className);
		if (rpcKey == null) {
			String[] array = className.split("\\.");
			int length = array.length;
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < length; i++) {
				// ignore ".impl.xxxImpl"
				if (i == length - 2)
					break;
				sb.append(array[i]).append('.');
			}
			String simpleName = array[length - 1];
			// sub "Impl"
			rpcKey = sb.append(simpleName.substring(0, simpleName.length() - 4)).toString();
			instance.cache.put(className, rpcKey);
		}
		if (rpcKey != null)
			return instance.rpc.get(rpcKey);
		return null;
	}
}
