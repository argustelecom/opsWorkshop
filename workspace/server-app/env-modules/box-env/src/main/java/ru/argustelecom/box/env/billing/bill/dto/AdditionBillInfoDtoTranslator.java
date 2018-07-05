package ru.argustelecom.box.env.billing.bill.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.bill.model.AggDataContainer;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class AdditionBillInfoDtoTranslator {

	@Inject
	private AnalyticDtoTranslator analyticDtoTranslator;

	public AdditionBillInfoDto translate(AggDataContainer rawDataContainer) {
		List<AnalyticDto> analytics = new ArrayList<>();
		analytics.addAll(rawDataContainer.getDataHolder().getChargesAggList().stream().filter(ca -> !ca.isRow())
				.map(chargesAggObj -> analyticDtoTranslator.translate(chargesAggObj)).collect(Collectors.toList()));
		analytics.addAll(rawDataContainer.getDataHolder().getIncomesAggList().stream()
				.map(chargesAggObj -> analyticDtoTranslator.translate(chargesAggObj)).collect(Collectors.toList()));
		analytics.addAll(rawDataContainer.getDataHolder().getSummaries().stream()
				.map(chargesAggObj -> analyticDtoTranslator.translate(chargesAggObj)).collect(Collectors.toList()));
		return new AdditionBillInfoDto(analytics);
	}

}