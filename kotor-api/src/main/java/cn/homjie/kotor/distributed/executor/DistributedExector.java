package cn.homjie.kotor.distributed.executor;

import cn.homjie.kotor.KotorConfiguration;
import cn.homjie.kotor.distributed.*;
import cn.homjie.kotor.distributed.function.Executable;
import cn.homjie.kotor.distributed.function.Propagable;
import cn.homjie.kotor.exception.DistributedException;
import cn.homjie.kotor.model.TxTaskInfoWithBLOBs;
import cn.homjie.kotor.util.AsyncExecutor;
import cn.homjie.kotor.util.DistributedUtils;
import cn.homjie.kotor.util.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @Class DistributedExector
 * @Description 分布式事务执行器
 * @Author JieHong
 * @Date 2017年3月9日 下午7:19:04
 */
public abstract class DistributedExector implements ClientExecutor, ServerExecutor {

	protected Logger log = LoggerFactory.getLogger(getClass());

	protected volatile boolean complete = false;

	protected Description description;
	protected DefaultPropagate propagate;

	// 已执行的和当前任务
	protected List<ForkTask<?>> tasks = new ArrayList<>();

	// 当前任务信息索引
	private int pointInfos = 0;

	public DistributedExector(Description description) {
		this.description = description;
		this.propagate = new DefaultPropagate(description);
		log.info("Current distributed business [{}]", description.getId());
	}

	@Override
	public <T> TaskResult<T> submit(Executable<T> business, boolean cache) throws DistributedException {
		return submit(business, null, cache);
	}

	@Override
	public <T> TaskResult<T> submit(Propagable<T> business, boolean cache) throws DistributedException {
		return submit(null, business, cache);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private <T> TaskResult<T> submit(Executable<T> executable, Propagable<T> propagable, boolean cache) throws DistributedException {
		// 任务已完成，不处理
		if (complete)
			return TaskResult.nul();

		// two business only one permit
		ForkTask<T> task = new ForkTask<T>();
		task.setExecutable(executable);
		task.setPropagable(propagable);
		task.setPropagate(propagate);
		tasks.add(task);

		ForkTaskInfo<T> info = info();
		propagate.present(info);

		if (description.firstTime())
			return first(task, info);
		else {
			// deserialize result in client
			byte[] rtbytes = info.getRtbytes();
			if (info.isSuccess()) {
				if (rtbytes != null)
					info.setResult((TaskResult) TaskResult.ok(SerializationUtils.deserialize(rtbytes)));
				else
					info.setResult(TaskResult.nul());
			} else {
				info.setResult(TaskResult.ex((Throwable) SerializationUtils.deserialize(rtbytes)));
			}

			return retry(task, info, cache);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> ForkTaskInfo<T> info() {
		pointInfos++;

		if (description.firstTime()) {
			ForkTaskInfo<T> info = addInfo();
			return info;
		} else {
			List<ForkTaskInfo<?>> infos = description.getInfos();
			int size = infos.size();
			int point = pointInfos - 1;

			if (point < size)
				return (ForkTaskInfo<T>) infos.get(point);
			return addInfo();
		}
	}

	private <T> ForkTaskInfo<T> addInfo() {
		ForkTaskInfo<T> info = new ForkTaskInfo<T>();
		info.setDescriptionId(description.getId());
		info.setOrder(pointInfos);
		description.getInfos().add(info);
		return info;
	}

	protected abstract <T> TaskResult<T> first(ForkTask<T> task, ForkTaskInfo<T> info) throws DistributedException;

	protected abstract <T> TaskResult<T> retry(ForkTask<T> task, ForkTaskInfo<T> info, boolean cache) throws DistributedException;

	@Override
	public boolean check() {
		Description tree = description;
		if (!tree.getChildren().isEmpty())
			build(tree);
		return checkAfterBuild(tree);
	}

	private void build(Description parent) {
		List<Description> children = parent.getChildren();
		for (Description child : children) {
			if (!child.getChildren().isEmpty())
				build(child);
		}
		List<ForkTaskInfo<?>> infos = parent.getInfos();
		for (Description description : children) {
			String id = description.getId();
			for (ForkTaskInfo<?> info : infos) {
				if (id.equals(info.getChildDescription())) {
					buildParentByChildren(info, description.getInfos());
					break;
				}
			}
		}
	}

	protected abstract void buildParentByChildren(ForkTaskInfo<?> parent, List<ForkTaskInfo<?>> children);

	protected abstract boolean checkAfterBuild(Description tree);

	protected void sendDescription() {
		AsyncExecutor.send(description);
	}

	protected void sendExTaskInfo(ForkTaskInfo<?> info, Throwable e) {
		if (KotorConfiguration.transactionService() == null)
			return;
		TxTaskInfoWithBLOBs entity = DistributedUtils.convertEx(info, e);
		send(entity);
	}

	protected <T> void sendOkTaskInfo(ForkTaskInfo<T> info, T result) {
		if (KotorConfiguration.transactionService() == null)
			return;
		TxTaskInfoWithBLOBs entity = DistributedUtils.convertOk(info, result);
		send(entity);
	}

	protected void sendOkTaskInfo(ForkTaskInfo<?> info) {
		if (KotorConfiguration.transactionService() == null)
			return;
		TxTaskInfoWithBLOBs entity = DistributedUtils.convertOk(info);
		send(entity);
	}

	private void send(TxTaskInfoWithBLOBs entity) {
		AsyncExecutor.send(entity);
	}

}
