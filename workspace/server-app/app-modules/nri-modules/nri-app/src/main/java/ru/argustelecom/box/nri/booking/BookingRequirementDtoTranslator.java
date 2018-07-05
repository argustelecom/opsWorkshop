package ru.argustelecom.box.nri.booking;

import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.box.nri.resources.requirements.ResourceSchemaDtoTranslator;
import ru.argustelecom.box.nri.schema.requirements.model.ResourceRequirement;

import javax.inject.Inject;

/**
 * Транслятор ДТО для требования к бронированию
 * Created by s.kolyada on 21.12.2017.
 */
@DtoTranslator
public class BookingRequirementDtoTranslator implements DefaultDtoTranslator<BookingRequirementDto, ResourceRequirement> {

	/**
	 * Транслятор схемы
	 */
	@Inject
	private ResourceSchemaDtoTranslator schemaDtoTranslator;

	@Override
	public BookingRequirementDto translate(ResourceRequirement businessObject) {
		if (businessObject == null) {
			return null;
		}

		return  new BookingRequirementDto(businessObject.getId(), businessObject.getName(), businessObject.getType(),
				schemaDtoTranslator.translate(businessObject.getSchema()));
	}
}
