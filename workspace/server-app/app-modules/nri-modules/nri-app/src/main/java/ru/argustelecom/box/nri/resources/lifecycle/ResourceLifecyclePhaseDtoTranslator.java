package ru.argustelecom.box.nri.resources.lifecycle;

import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.box.nri.resources.lifecycle.model.ResourceLifecyclePhase;

/**
 * Транслятор фазы ЖЦ
 * Created by s.kolyada on 07.11.2017.
 */
@DtoTranslator
public class ResourceLifecyclePhaseDtoTranslator implements
		DefaultDtoTranslator<ResourceLifecyclePhaseDto, ResourceLifecyclePhase> {

	@Override
	public ResourceLifecyclePhaseDto translate(ResourceLifecyclePhase businessObject) {
		if (businessObject == null)
			return null;

		return ResourceLifecyclePhaseDto.builder()
				.id(businessObject.getId())
				.phaseName(businessObject.getPhaseName())
				.x(businessObject.getX())
				.y(businessObject.getY())
				.build();
	}
}
