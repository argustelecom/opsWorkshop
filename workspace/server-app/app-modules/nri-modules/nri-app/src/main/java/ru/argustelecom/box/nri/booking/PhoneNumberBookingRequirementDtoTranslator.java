package ru.argustelecom.box.nri.booking;

import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.box.nri.resources.requirements.ResourceSchemaDtoTranslator;
import ru.argustelecom.box.nri.schema.requirements.phone.model.PhoneNumberBookingRequirement;

import javax.inject.Inject;


/**
 * Транслятор для требований к тн
 */
@DtoTranslator
public class PhoneNumberBookingRequirementDtoTranslator implements DefaultDtoTranslator<PhoneNumberBookingRequirementDto, PhoneNumberBookingRequirement> {
	@Inject
	private ResourceSchemaDtoTranslator schemaDtoTranslator;

	@Override
	public PhoneNumberBookingRequirementDto translate(PhoneNumberBookingRequirement businessObject) {
		if (businessObject == null) {
			return null;
		}
		return PhoneNumberBookingRequirementDto.builder()
				.id(businessObject.getId())
				.name(businessObject.getName())
				.schema(schemaDtoTranslator.translate(businessObject.getSchema()))
				.build();
	}
}
