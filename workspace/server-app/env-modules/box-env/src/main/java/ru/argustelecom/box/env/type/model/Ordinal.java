package ru.argustelecom.box.env.type.model;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.Lists;

public interface Ordinal {

	Integer getOrdinalNumber();

	void setOrdinalNumber(Integer value);

	/**
	 * Группа, которой принадлежит данный объект
	 * @return
	 */
	List<? extends Ordinal> group();

	Integer initialOrdinalNumber();

	default Integer lastOrdinalNumber() {
		return initialOrdinalNumber() + group().size() - (group().isEmpty() ? 0 : 1);
	}

	default void changeOrdinalNumber(Integer to) {
		List<? extends Ordinal> group = group();

		Integer initial = initialOrdinalNumber();
		checkArgument(to != null && to >= initial && to < group.size() + initial);

		group.forEach(entry -> {
			Integer current = entry.getOrdinalNumber();
			if (current > getOrdinalNumber() && current <= to) {
				entry.setOrdinalNumber(current - 1);
			} else if (getOrdinalNumber() > to && current >= to) {
				entry.setOrdinalNumber(current + 1);
			}
		});

		setOrdinalNumber(to);

		normalize(group);
	}

	static void normalize(List<? extends Ordinal> group) {
		checkRequiredArgument(group, "group");

		AtomicInteger atomicInteger = new AtomicInteger();
		Lists.newArrayList(group).stream().sorted(comparator()).forEach(
				ordinal -> ordinal.setOrdinalNumber(atomicInteger.getAndIncrement() + ordinal.initialOrdinalNumber()));
	}

	static Comparator<Ordinal> comparator() {
		return Comparator.comparingInt(Ordinal::getOrdinalNumber);
	}
}
