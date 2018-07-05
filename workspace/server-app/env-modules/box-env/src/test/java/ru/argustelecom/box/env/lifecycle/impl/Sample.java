package ru.argustelecom.box.env.lifecycle.impl;

import lombok.EqualsAndHashCode;
import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;

@EqualsAndHashCode(of = "id")
public class Sample implements LifecycleObject<SampleState> {

	private Long id;
	private SampleState state;

	public Sample(Long id) {
		this.id = id;
		this.state = SampleState.DRAFT;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public SampleState getState() {
		return state;
	}

	@Override
	public void setState(SampleState state) {
		this.state = state;
	}

}
