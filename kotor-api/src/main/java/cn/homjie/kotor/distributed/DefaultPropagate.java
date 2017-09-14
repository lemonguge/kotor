package cn.homjie.kotor.distributed;

import cn.homjie.kotor.util.IdGen;

import java.util.List;

/**
 * @Class Propagate
 * @Description 事务传播
 * @Author JieHong
 * @Date 2017年3月16日 下午1:40:06
 */
public class DefaultPropagate implements Propagate {

	private Description description;
	// 当前子服务信息位置
	private int pointChildren = 0;

	// 当前任务信息
	private ForkTaskInfo<?> present;

	public DefaultPropagate(Description description) {
		this.description = description;
	}

	public void present(ForkTaskInfo<?> present) {
		if (present == null)
			return;
		this.present = present;
	}

	@Override
	public Description acquire() {
		Description child = desc();
		present.setChildDescription(child.getId());
		return child;
	}

	private Description desc() {
		pointChildren++;

		if (description.firstTime()) {
			Description child = addDesc();
			return child;
		} else {
			List<Description> children = description.getChildren();
			int size = children.size();
			int point = pointChildren - 1;

			if (point < size)
				return children.get(point);
			return addDesc();
		}
	}

	private Description addDesc() {
		Description child = new Description(description.getTransaction());
		child.setId(IdGen.uuid());
		child.setRoot(description.getRoot());
		child.setPid(description.getId());
		child.setLevel(description.getLevel() + 1);
		child.setOrder(pointChildren);
		description.getChildren().add(child);
		return child;
	}
}
