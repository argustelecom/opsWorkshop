package ru.argustelecom.box.nri.resources.lifecycle;

import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.box.nri.resources.lifecycle.model.ResourceLifecycle;

import javax.inject.Inject;

/**
 * Транслятор фаз ЖЦ
 * Created by s.kolyada on 02.11.2017.
 */
@DtoTranslator
public class ResourceLifecycleDtoTranslator implements
		DefaultDtoTranslator<ResourceLifecycleDto, ResourceLifecycle> {

	@Inject
	private ResourceLifecyclePhaseDtoTranslator phaseDtoTranslator;

	@Override
	public ResourceLifecycleDto translate(ResourceLifecycle businessObject) {
		if (businessObject == null)
			return null;
		return ResourceLifecycleDto.builder()
				.id(businessObject.getId())
				.name(businessObject.getName())
				.initialPhase(phaseDtoTranslator.translate(businessObject.getInitialPhase()))
				.build();
	}
}
