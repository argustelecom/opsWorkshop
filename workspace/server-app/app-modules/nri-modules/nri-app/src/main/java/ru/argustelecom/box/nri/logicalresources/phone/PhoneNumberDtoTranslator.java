package ru.argustelecom.box.nri.logicalresources.phone;


import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumber;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberPool;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDtoTranslator;

import javax.inject.Inject;

/**
 * Транслятор телефонных номеров
 * Created by b.bazarov on 31.10.2017.
 */
@DtoTranslator
public class PhoneNumberDtoTranslator implements DefaultDtoTranslator<PhoneNumberDto, PhoneNumber> {

	@Inject
	private PhoneNumberPoolDtoTranslator poolTranslator;

	@Inject
	private ResourceInstanceDtoTranslator resTranslator;

	@Override
	public PhoneNumberDto translate(PhoneNumber businessObject) {
		if (businessObject == null) {
			return null;
		}
		return PhoneNumberDto.builder()
				.id(businessObject.getId())
				.name(businessObject.getName())
				.state(businessObject.getState())
				.pool(safeTranslate(businessObject.getPool()))
				.stateChangeDate(businessObject.getStateChangeDate())
				.specification(businessObject.getSpecInstance())
				.resource(resTranslator.translateLazy(businessObject.getResource()))
				.build();
	}

	private PhoneNumberPoolDto safeTranslate(PhoneNumberPool pool) {
		if (pool != null)
			return poolTranslator.translateLazy(pool);
		else
			return null;
	}
}
