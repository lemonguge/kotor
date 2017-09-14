package cn.homjie.kotor.annotation;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class DistributedMarkHandler {

	private List<Class<? extends Annotation>> markAnnotations = new ArrayList<>();

	public DistributedMarkHandler() {
		markAnnotations.add(DistributedMark.class);
	}

	public void register(Class<? extends Annotation> mark) {
		if (mark == null)
			return;
		markAnnotations.add(mark);
	}

	public boolean findMark(Class<?> clazz) {
		Annotation[] annotations = clazz.getDeclaredAnnotations();
		for (Class<? extends Annotation> markAnnotation : markAnnotations) {
			for (Annotation annotation : annotations) {
				if (markAnnotation.isInstance(annotation)) {
					return true;
				}
			}
		}
		return false;
	}

}
