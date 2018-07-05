package ru.argustelecom.box.env.map.page.detailinfo.header;

import ru.argustelecom.system.inf.page.ObservablePresentationState;
import ru.argustelecom.system.inf.page.PresentationState;

@PresentationState
public class CurrentDetailInfoModifier extends ObservablePresentationState<ObjectDetailInfoModifier> {
	private static final long serialVersionUID = -2155786119189365894L;

	private ObjectDetailInfoModifier value;

	@Override
	public ObjectDetailInfoModifier getValue() {
		return value;
	}

	@Override
	protected void doSetValue(ObjectDetailInfoModifier value) {
		this.value = value;
	}

	public void showRoot() {
		setValue(null);
	}
}
