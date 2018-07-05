package ru.argustelecom.box.env.lifecycle.impl;

import java.io.Serializable;
import java.util.Arrays;

import ru.argustelecom.box.env.lifecycle.api.LifecycleState;

public enum SampleState implements LifecycleState<SampleState> {

	DRAFT, WHAITING, ACTIVE, DEACTIVATING, CLOSED;

	@Override
	public Iterable<SampleState> getStates() {
		return Arrays.asList(values());
	}

	@Override
	public Serializable getKey() {
		return this;
	}

	@Override
	public String getName() {
		return name();
	}

	@Override
	public String getEventQualifier() {
		return name();
	}
}
