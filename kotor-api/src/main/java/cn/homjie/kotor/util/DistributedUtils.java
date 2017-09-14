package cn.homjie.kotor.util;

import cn.homjie.kotor.distributed.Description;
import cn.homjie.kotor.distributed.ForkTaskInfo;
import cn.homjie.kotor.enums.Transaction;
import cn.homjie.kotor.model.TxDescription;
import cn.homjie.kotor.model.TxTaskInfoWithBLOBs;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static cn.homjie.kotor.enums.RootStatus.*;

/**
 * @Class DistributedUtils
 * @Description 分布式工具类
 * @Author JieHong
 * @Date 2017年3月11日 下午5:09:40
 */
public class DistributedUtils {

	/**
	 * @Description 重试状态
	 */
	public static final List<String> RETRY_STATUS_LIST = new ArrayList<>();

	/**
	 * @Description OK状态
	 */
	public static final List<String> OK_STATUS_LIST = new ArrayList<>();

	/**
	 * @Description 失败状态
	 */
	public static final List<String> FAILED_STATUS_LIST = new ArrayList<>();

	static {
		RETRY_STATUS_LIST.add(CHECK_WAIT.name());
		RETRY_STATUS_LIST.add(RETRY_CHECK_WAIT.name());

		OK_STATUS_LIST.add(CHECK_OK.name());
		OK_STATUS_LIST.add(RETRY_CHECK_OK.name());

		FAILED_STATUS_LIST.add(RETRY_FAILED.name());
	}

	/**
	 * @param description
	 * @return
	 * @Title convert
	 * @Description 将服务信息转换为根描述信息
	 * @Author JieHong
	 * @Date 2017年3月11日 下午5:10:19
	 */
	public static TxDescription convert(Description description) {
		if (description == null)
			return null;
		TxDescription entity = new TxDescription();
		entity.setTransaction(description.getTransaction().name());
		entity.setParams(description.getParams());
		entity.setPoint(description.getPoint());
		entity.setClassName(description.getClassName());
		entity.setMethodName(description.getMethodName());

		entity.setId(description.getId());
		entity.setPid(description.getPid());
		entity.setRoot(description.getRoot());
		entity.setLevel(description.getLevel());
		entity.setOrder(description.getOrder());

		entity.setInfosSize(description.getInfos().size());
		entity.setChildSize(description.getChildren().size());

		entity.setTimes(description.getTimes());
		return entity;
	}

	/**
	 * @param list
	 * @return
	 * @Title tree
	 * @Description 将根描述列表转换为Tree
	 * @Author JieHong
	 * @Date 2017年3月11日 下午5:10:54
	 */
	public static Description tree(List<TxDescription> list) {
		if (CollectionUtils.isEmpty(list))
			throw new NullPointerException("数据为空");
		List<TxDescription> toCheck = new ArrayList<>();
		toCheck.addAll(list);
		List<TxDescription> rootList = toCheck.stream().filter(d -> d.getPid() == null).collect(Collectors.toList());
		if (CollectionUtils.isEmpty(rootList))
			throw new NullPointerException("根描述数据为空");
		if (rootList.size() != 1)
			throw new IllegalArgumentException("根描述数据不唯一");
		TxDescription txDescription = rootList.get(0);
		Description root = convert(txDescription);
		toCheck.remove(txDescription);
		tree(root, toCheck);
		if (!toCheck.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			toCheck.forEach(t -> sb.append('|').append(t.getId()));
			throw new IllegalArgumentException("描述数据异常[" + sb.substring(1) + "]");
		}
		return root;
	}

	private static void tree(Description parent, List<TxDescription> posterity) {
		if (CollectionUtils.isEmpty(posterity))
			return;
		List<TxDescription> children = new ArrayList<>();
		for (TxDescription child : posterity) {
			if (parent.getId().equals(child.getPid()))
				children.add(child);
		}
		if (CollectionUtils.isEmpty(children))
			return;
		posterity.removeAll(children);
		Collections.sort(children);
		for (TxDescription child : children) {
			Description nextParent = convert(child);
			parent.getChildren().add(nextParent);
			tree(nextParent, posterity);
		}
	}

	@SuppressWarnings("rawtypes")
	private static Description convert(TxDescription txDescription) {
		Transaction transaction = Transaction.valueOf(txDescription.getTransaction());
		Description description = new Description(transaction);
		description.setParams(txDescription.getParams());
		description.setPoint(txDescription.getPoint());
		description.setClassName(txDescription.getClassName());
		description.setMethodName(txDescription.getMethodName());
		description.setId(txDescription.getId());
		description.setPid(txDescription.getPid());
		description.setRoot(txDescription.getRoot());
		description.setLevel(txDescription.getLevel());
		description.setOrder(txDescription.getOrder());
		description.setTimes(txDescription.getTimes());
		List<TxTaskInfoWithBLOBs> infos = txDescription.getInfos();
		if (!CollectionUtils.isEmpty(infos)) {
			Collections.sort(infos);
			for (TxTaskInfoWithBLOBs txTaskInfo : infos) {
				ForkTaskInfo<?> info = new ForkTaskInfo();

				info.setId(txTaskInfo.getId());
				info.setDescriptionId(txTaskInfo.getDescriptionId());
				info.setChildDescription(txTaskInfo.getChildDescription());
				info.setOrder(txTaskInfo.getOrder());
				info.setTaskStatus(txTaskInfo.getTaskStatus());
				info.setStackTrace(txTaskInfo.getStackTrace());
				// deserialize result in client
				info.setSuccess(txTaskInfo.getSuccess());
				info.setRtbytes(txTaskInfo.getResult());

				description.getInfos().add(info);
			}
		}
		return description;
	}

	/**
	 * @param info
	 * @return
	 * @Title convertOk
	 * @Description 将分支任务信息转换为事务详细任务信息
	 * @Author JieHong
	 * @Date 2017年3月11日 下午5:11:25
	 */
	public static TxTaskInfoWithBLOBs convertOk(ForkTaskInfo<?> info) {
		if (info == null)
			return null;
		TxTaskInfoWithBLOBs entity = convert(info);
		entity.setSuccess(true);
		return entity;
	}

	/**
	 * @param info
	 * @param result 正常结果
	 * @return
	 * @Title convertOk
	 * @Description 将分支任务信息转换为事务详细任务信息
	 * @Author JieHong
	 * @Date 2017年3月11日 下午5:11:49
	 */
	public static <T> TxTaskInfoWithBLOBs convertOk(ForkTaskInfo<T> info, T result) {
		if (info == null)
			return null;
		TxTaskInfoWithBLOBs entity = convert(info);
		entity.setResult(SerializationUtils.serialize(result));
		entity.setSuccess(true);
		return entity;
	}

	/**
	 * @param info
	 * @param e    异常
	 * @return
	 * @Title convertEx
	 * @Description 将分支任务信息转换为事务详细任务信息
	 * @Author JieHong
	 * @Date 2017年3月11日 下午5:11:57
	 */
	public static TxTaskInfoWithBLOBs convertEx(ForkTaskInfo<?> info, Throwable e) {
		if (info == null)
			return null;
		TxTaskInfoWithBLOBs entity = convert(info);
		entity.setResult(SerializationUtils.serialize(e));
		entity.setSuccess(false);
		return entity;
	}

	private static TxTaskInfoWithBLOBs convert(ForkTaskInfo<?> info) {
		TxTaskInfoWithBLOBs entity = new TxTaskInfoWithBLOBs();
		entity.setId(info.getId());
		entity.setDescriptionId(info.getDescriptionId());
		entity.setChildDescription(info.getChildDescription());
		entity.setOrder(info.getOrder());
		entity.setTaskStatus(info.getTaskStatus());
		entity.setStackTrace(info.getStackTrace());
		return entity;
	}

	public static Object invokeMethod(final Object object, final String methodName, Object... args)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		args = ArrayUtils.nullToEmpty(args);
		final Class<?>[] paramTypes = ClassUtils.toClass(args);
		Method method = findMethod(object.getClass(), methodName, paramTypes);
		ReflectionUtils.makeAccessible(method);
		return method.invoke(object, args);
	}

	public static Method findMethod(Class<?> clazz, String name, Class<?>... paramTypes) {
		Assert.notNull(clazz, "Class must not be null");
		Assert.notNull(name, "Method name must not be null");
		Class<?> searchType = clazz;
		while (searchType != null) {
			Method[] methods = (searchType.isInterface() ? searchType.getMethods() : searchType.getDeclaredMethods());
			for (Method method : methods) {
				if (name.equals(method.getName()) && ClassUtils.isAssignable(paramTypes, method.getParameterTypes())) {
					return method;
				}
			}
			searchType = searchType.getSuperclass();
		}
		return null;
	}

	public static Throwable getRootCause(Throwable throwable) {
		if (throwable.getCause() == null)
			return throwable;
		return ExceptionUtils.getRootCause(throwable);
	}

}
