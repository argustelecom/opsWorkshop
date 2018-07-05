package ru.argustelecom.box.nri.booking;

import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.box.nri.resources.requirements.ResourceSchemaDtoTranslator;
import ru.argustelecom.box.nri.schema.requirements.ip.model.IpAddressBookingRequirement;

import javax.inject.Inject;

/**
 * Created by s.kolyada on 21.12.2017.
 */
@DtoTranslator
public class IpAddressBookingRequirementDtoTranslator implements DefaultDtoTranslator<IpAddressBookingRequirementDto, IpAddressBookingRequirement> {

	@Inject
	private ResourceSchemaDtoTranslator schemaDtoTranslator;

	@Override
	public IpAddressBookingRequirementDto translate(IpAddressBookingRequirement businessObject) {
		if (businessObject == null) {
			return null;
		}
		return IpAddressBookingRequirementDto.builder()
				.id(businessObject.getId())
				.name(businessObject.getName())
				.shouldBePrivate(businessObject.getShouldBePrivate())
				.shouldHaveBooking(businessObject.getShouldHaveBooking())
				.shouldBeStatic(businessObject.getShouldBeStatic())
				.shouldHaveState(businessObject.getShouldHaveState())
				.shouldHaveTransferType(businessObject.getShouldHaveTransferType())
				.shouldHavePurpose(businessObject.getShouldHavePurpose())
				.schema(schemaDtoTranslator.translate(businessObject.getSchema()))
				.build();
	}
}
