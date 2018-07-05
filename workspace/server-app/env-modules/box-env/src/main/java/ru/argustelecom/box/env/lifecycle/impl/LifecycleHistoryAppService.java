package ru.argustelecom.box.env.lifecycle.impl;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.history.model.LifecycleHistoryItem;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class LifecycleHistoryAppService implements Serializable {

	private static final long serialVersionUID = 3975781388654122710L;

	@Inject
	private LifecycleHistoryRepository lifecycleHistoryRp;

	public List<LifecycleHistoryItem> getHistory(LifecycleObject<?> businessObject) {
		return lifecycleHistoryRp.getHistory(businessObject);
	}
}
