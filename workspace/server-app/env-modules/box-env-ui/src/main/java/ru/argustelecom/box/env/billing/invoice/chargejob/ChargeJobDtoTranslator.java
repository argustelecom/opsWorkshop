package ru.argustelecom.box.env.billing.invoice.chargejob;

import ru.argustelecom.box.env.billing.invoice.model.ChargeJob;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class ChargeJobDtoTranslator implements DefaultDtoTranslator<ChargeJobDto, ChargeJob> {

	@Override
	public ChargeJobDto translate(ChargeJob chargeJob) {
		return ChargeJobDto.builder()
				.id(chargeJob.getId())
				.mediationId(chargeJob.getMediationId())
				.creationDate(chargeJob.getCreationDate())
				.dataType(chargeJob.getDataType())
				.state(chargeJob.getState())
				.filter(chargeJob.getFilter())
				.build();
	}
}