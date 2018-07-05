package ru.argustelecom.box.env.billing.provision;

import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;

import ru.argustelecom.box.env.billing.provision.ProvisionTermsDto.ProvisionTermsType;
import ru.argustelecom.box.env.billing.provision.model.AbstractProvisionTerms;
import ru.argustelecom.box.env.billing.provision.model.RecurrentTerms;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.stl.period.PeriodType;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class ProvisionTermsDtoTranslator implements DefaultDtoTranslator<ProvisionTermsDto, AbstractProvisionTerms> {

	public ProvisionTermsDto translate(AbstractProvisionTerms provisionTerms) {
		//@formatter:off
		
		return ProvisionTermsDto.builder()
			.id(provisionTerms.getId())
			.name(provisionTerms.getObjectName())
			.type(defineType(provisionTerms))
			.periodType(definePeriodType(provisionTerms))
			.description(provisionTerms.getDescription())
			.reserveFunds(provisionTerms instanceof RecurrentTerms && ((RecurrentTerms) provisionTerms).isReserveFunds())
		.build();
		
		//@formatter:on
	}

	private ProvisionTermsType defineType(AbstractProvisionTerms provisionTerms) {
		return provisionTerms.isRecurrent() ? ProvisionTermsType.RECURRENT : ProvisionTermsType.NON_RECURRENT;
	}

	private PeriodType definePeriodType(AbstractProvisionTerms provisionTerms) {
		return provisionTerms.isRecurrent() ? ((RecurrentTerms) initializeAndUnproxy(provisionTerms)).getPeriodType()
				: null;
	}
}