package ru.argustelecom.box.env.task;

import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.inject.Inject;

import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOption;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class TelephonyOptionDtoTranslator implements DefaultDtoTranslator<TelephonyOptionDto, TelephonyOption> {

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	public TelephonyOptionDto translate(TelephonyOption option) {
		return new TelephonyOptionDto(option.getId(), option.getObjectName(),
				businessObjectDtoTr.translate(option.getTariff()));
	}

	public List<TelephonyOptionDto> translate(List<TelephonyOption> options) {
		return options.stream().map(this::translate).collect(toList());
	}

}