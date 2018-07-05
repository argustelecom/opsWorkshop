package ru.argustelecom.box.env.billing.bill;

import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.toList;
import static ru.argustelecom.box.env.billing.bill.model.AnalyticTypeError.SUMMARY_HAS_INVALID_DATA;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.bill.model.BillAnalyticType;
import ru.argustelecom.box.env.billing.bill.model.BillType;
import ru.argustelecom.box.env.billing.bill.model.Summary;
import ru.argustelecom.box.env.billing.bill.model.SummaryBillAnalyticType;
import ru.argustelecom.box.inf.modelbase.BusinessDirectory;
import ru.argustelecom.box.inf.service.DomainService;

/**
 * Сервис для расчёта {@linkplain ru.argustelecom.box.env.billing.bill.model.Summary итогов} по сырым данным счёта.
 */
@DomainService
public class SummaryService implements Serializable {

	@Inject
	private BillAnalyticTypeRepository analyticTypeRp;

	public List<Summary> initSummaries(BillType billType, List<AggData> aggDataList) {
		List<SummaryBillAnalyticType> allSummaryTypes = billType.getSummaryBillAnalyticTypes();
		if (!allSummaryTypes.contains(billType.getSummaryToPay())) {
			allSummaryTypes.add(billType.getSummaryToPay());
		}
		Map<SummaryBillAnalyticType, List<AggData>> summaryTypeAggDataMap = groupingDataBySummaryType(allSummaryTypes,
				aggDataList);
		return calculateSummaries(summaryTypeAggDataMap);
	}

	private List<Summary> calculateSummaries(Map<SummaryBillAnalyticType, List<AggData>> summaryTypeAggDataMap) {
		List<Summary> summaries = new ArrayList<>();

		summaryTypeAggDataMap.forEach((key, value) -> {
			//@formatter:off
			BigDecimal sum = value.stream().map(AggData::getMathSum).reduce(BigDecimal::add).orElse(ZERO);
			Optional<AggData> invalidOptional = value.stream().filter(aggData -> !aggData.isValid()).findAny();
			summaries.add(Summary.builder()
									.analyticTypeId(key.getId())
									.keyword(key.getKeyword())
									.sum(sum)
									.invertible(key.getInvertible())
									.error(invalidOptional.isPresent() ? SUMMARY_HAS_INVALID_DATA : null)
								.build());
			//@formatter:on
		});

		summaries.sort(AggData.aggDataComparator());

		return summaries;
	}

	/**
	 * Группирует агрегированные сырые данные по типу {@linkplain SummaryBillAnalyticType итоговой аналитики}. Если для
	 * итоговой аналитики нет исходных данных, то в карту будет положен пустой список.
	 * 
	 * @param summaryTypes
	 *            список итоговых типов аналитик.
	 * @param dataList
	 *            список сырых данных в агрегированном виде.
	 */
	private Map<SummaryBillAnalyticType, List<AggData>> groupingDataBySummaryType(
			List<SummaryBillAnalyticType> summaryTypes, List<AggData> dataList) {

		Map<SummaryBillAnalyticType, List<AggData>> summaryTypeAggDataMap = new HashMap<>();
		// Получаем типы аналитик, являющиеся строками, т.к. они должны быть всегда
		List<BillAnalyticType> billRows = analyticTypeRp.findRowBillAnalyticTypes();

		summaryTypes.forEach(summaryType -> {
			//@formatter:off
			List<Long> typeIds = Stream.concat(summaryType.getBillAnalyticTypes().stream(), billRows.stream()).map(BusinessDirectory::getId).collect(toList());
			List<AggData> aggDataForSummaryType = dataList.stream().filter(ad -> typeIds.contains(ad.getAnalyticTypeId())).collect(toList());
			summaryTypeAggDataMap.put(summaryType, aggDataForSummaryType);
			//@formatter:on
		});
		return summaryTypeAggDataMap;
	}

	private static final long serialVersionUID = -2628640076550444850L;

}