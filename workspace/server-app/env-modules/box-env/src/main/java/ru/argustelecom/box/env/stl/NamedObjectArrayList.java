package ru.argustelecom.box.env.stl;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.Collection;

import ru.argustelecom.system.inf.modelbase.NamedObject;

public class NamedObjectArrayList<T> extends ArrayList<T> {

	private static final String DELIMITER = ", ";

	public NamedObjectArrayList(int initialCapacity) {
		super(initialCapacity);
	}

	public NamedObjectArrayList() {
	}

	public NamedObjectArrayList(Collection<? extends T> c) {
		super(c);
	}

	@Override
	public String toString() {
		return this.stream().map(this::mapper).collect(joining(DELIMITER));
	}

	private String mapper(T value) {
		if (value instanceof NamedObject) {
			return ((NamedObject) value).getObjectName();
		}
		return value.toString();
	}

	private static final long serialVersionUID = -3130647427702711049L;
}
