package ru.argustelecom.ops.inf.component.dashboard;

import static com.google.common.base.Preconditions.checkState;

import java.util.List;

public final class DashboardUtils {

	private DashboardUtils() {
	}

	public static <T> boolean movePrev(List<T> collection, T element) {
		int index = collection.indexOf(element);
		checkState(index >= 0);
		if (index != 0) {
			collection.remove(index);
			collection.add(--index, element);
			return true;
		}
		return false;
	}

	public static <T> boolean moveNext(List<T> collection, T element) {
		int index = collection.indexOf(element);
		checkState(index >= 0);
		if (index != collection.size() - 1) {
			collection.remove(index);
			collection.add(++index, element);
			return true;
		}
		return false;
	}

}
