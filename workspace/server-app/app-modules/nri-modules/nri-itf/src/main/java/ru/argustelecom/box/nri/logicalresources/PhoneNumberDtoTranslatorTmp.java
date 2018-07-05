package ru.argustelecom.box.nri.logicalresources;


import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberPoolDto;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberPoolDtoTranslator;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumber;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberPool;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDtoTranslator;

import javax.inject.Inject;

/**
 * Транслятор телефонных номеров
 * Created by b.bazarov on 31.10.2017.
 */
@DtoTranslator
public class PhoneNumberDtoTranslatorTmp implements DefaultDtoTranslator<PhoneNumberDtoTmp, PhoneNumber> {

	@Inject
	private PhoneNumberPoolDtoTranslator poolTranslator;

	@Inject
	private ResourceInstanceDtoTranslator resTranslator;

	@Override
	public PhoneNumberDtoTmp translate(PhoneNumber businessObject) {
		if (businessObject == null) {
			return null;
		}
		return PhoneNumberDtoTmp.builder()
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
