package ru.argustelecom.box.env.billing.bill.dto;

import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.bill.model.ChargesAgg;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class ChargesDtoTranslator {

	@Inject
	private ChargesAggDtoTranslator chargesAggDtoTr;

	public ChargesDto translate(List<ChargesAgg> chargesAggList) {
		List<ChargesAggDto> chargesAggDtoList = chargesAggList.stream().filter(ChargesAgg::isRow)
				.map(chargesAggDtoTr::translate).collect(toList());
		return new ChargesDto(chargesAggDtoList);
	}

}