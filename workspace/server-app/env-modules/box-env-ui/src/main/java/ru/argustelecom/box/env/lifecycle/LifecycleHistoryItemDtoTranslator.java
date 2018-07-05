package ru.argustelecom.box.env.lifecycle;

import ru.argustelecom.box.env.lifecycle.api.history.model.LifecycleHistoryItem;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class LifecycleHistoryItemDtoTranslator {

	public LifecycleHistoryItemDto translate(LifecycleHistoryItem item) {
		//@formatter:off
		return LifecycleHistoryItemDto.builder()
					.transitionTime(item.getTransitionTime())
					.fromState(item.getFromState())
					.toState(item.getToState())
					.initiatorName(item.getInitiator().name())
				.build();
		//@formatter:on
	}

}