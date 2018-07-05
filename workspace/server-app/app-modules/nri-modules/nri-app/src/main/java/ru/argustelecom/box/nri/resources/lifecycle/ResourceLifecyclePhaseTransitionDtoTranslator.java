package ru.argustelecom.box.nri.resources.lifecycle;

import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.box.nri.resources.lifecycle.model.ResourceLifecyclePhaseTransition;

/**
 * Транслятор переходов фаз ЖЦ
 * Created by s.kolyada on 07.11.2017.
 */
@DtoTranslator
public class ResourceLifecyclePhaseTransitionDtoTranslator implements
		DefaultDtoTranslator<ResourceLifecyclePhaseTransitionDto, ResourceLifecyclePhaseTransition> {

	@Override
	public ResourceLifecyclePhaseTransitionDto translate(ResourceLifecyclePhaseTransition businessObject) {
		if (businessObject == null)
			return null;
		return ResourceLifecyclePhaseTransitionDto.builder()
				.id(businessObject.getId())
				.comment(businessObject.getComment())
				.build();
	}
}
