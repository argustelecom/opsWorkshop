package ru.argustelecom.box.env.document.type;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.google.common.collect.Lists;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.billing.bill.BillAnalyticTypeAppService;
import ru.argustelecom.box.env.billing.bill.BillTypeAppService;
import ru.argustelecom.box.env.billing.bill.model.AbstractBillAnalyticType;
import ru.argustelecom.box.env.billing.bill.model.BillAnalyticType;
import ru.argustelecom.box.env.billing.bill.model.BillPeriodType;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "billAnalyticTypeFm")
@PresentationModel
public class BillAnalyticTypeFrameModel implements Serializable {

	public static final String SUMMARY = "SUMMARY";

	@PersistenceContext
	private EntityManager em;

	@Inject
	private BillTypeAppService billTypeAppService;

	@Inject
	private BillAnalyticTypeAppService billAnalyticTypeAs;

	@Inject
	private BillAnalyticTypeDtoTranslator billAnalyticTypeDtoTranslator;

	@Inject
	private SummaryBillAnalyticTypeDtoTranslator summaryBillAnalyticTypeDtoTranslator;

	@Getter
	@Setter
	private BillTypeDto billTypeDto;

	private Map<String, List<AnalyticTypeRow>> rows;

	public void preRender(BillTypeDto billTypeDto) {
		this.billTypeDto = billTypeDto;
		this.rows = null;
	}

	public Map<String, List<AnalyticTypeRow>> getRows() {
		if (this.rows != null || this.billTypeDto == null) {
			return rows;
		}

		//@formatter:off
		rows = getBillAnalyticTypeDtoList(billAnalyticTypeAs.findAllBillAnalyticTypes(), billAnalyticTypeDtoTranslator)
				.stream()
				.collect(Collectors.groupingBy(dto -> dto.getAnalyticCategory().name(), LinkedHashMap::new,
						Collectors.mapping(getSupplierFunction(billTypeDto.getBillAnalyticTypeDtos()), Collectors.toList())));

		List<AnalyticTypeRow> summaryBillAnalyticRow
				= getBillAnalyticTypeDtoList(billAnalyticTypeAs.findAllSummaryBillAnalyticType(), summaryBillAnalyticTypeDtoTranslator)
				.stream()
				.map(getSupplierFunction(billTypeDto.getSummaryBillAnalyticTypeDtos()))
				.collect(Collectors.toList());

		if (!summaryBillAnalyticRow.isEmpty()) {
			rows.put(SUMMARY, summaryBillAnalyticRow);
		}
		//@formatter:on

		return rows;
	}

	public Callback<Map<String, List<AnalyticTypeRow>>> getBillAnalyticTypeCallback() {
		return map -> {
			List<BillAnalyticTypeDto> billAnalyticTypeDtos = billTypeDto.getBillAnalyticTypeDtos();
			List<SummaryBillAnalyticTypeDto> summaryBillAnalyticTypeDtos = billTypeDto.getSummaryBillAnalyticTypeDtos();
			List<Long> ids = Lists.newArrayList();
			billAnalyticTypeDtos.clear();
			summaryBillAnalyticTypeDtos.clear();
			map.forEach((s, analyticTypeRows) -> {
				if (!s.equals(SUMMARY)) {
					initLists(billAnalyticTypeDtos, ids, analyticTypeRows, BillAnalyticTypeDto.class);
				} else {
					initLists(summaryBillAnalyticTypeDtos, ids, analyticTypeRows, SummaryBillAnalyticTypeDto.class);
				}
			});
			billTypeAppService.save(billTypeDto.getId(), ids);
		};
	}

	private Function<AbstractBillAnalyticTypeDto, AnalyticTypeRow> getSupplierFunction(
			List<? extends AbstractBillAnalyticTypeDto> billAnalyticTypeDtos) {
		return t -> new AnalyticTypeRow(billAnalyticTypeDtos.contains(t), t);
	}

	private <R extends AbstractBillAnalyticTypeDto, I extends AbstractBillAnalyticType, T extends DefaultDtoTranslator<R, I>> List<R> getBillAnalyticTypeDtoList(
			List<I> analyticTypes, T translator) {

		return translator.translate(analyticTypes.stream().filter(analyticType -> {
			if (billTypeDto.getBillPeriodType().equals(BillPeriodType.CUSTOM)) {
				if (!analyticType.getAvailableForCustomPeriod()) {
					return false;
				}
			}

			if (analyticType instanceof BillAnalyticType) {
				if (((BillAnalyticType) analyticType).getIsRow()) {
					return false;
				}
			}
			return true;
		}).collect(Collectors.toList()));
	}

	private <T extends AbstractBillAnalyticTypeDto> void initLists(List<T> analyticTypeDtos, List<Long> ids,
			List<AnalyticTypeRow> analyticTypeRows, Class<T> castClass) {
		List<T> selectedAnalyticTypeDtos = analyticTypeRows.stream().filter(AnalyticTypeRow::getSelected)
				.map(analyticTypeRow -> castClass.cast(analyticTypeRow.getAnalyticTypeDto()))
				.collect(Collectors.toList());
		analyticTypeDtos.addAll(selectedAnalyticTypeDtos);
		ids.addAll(
				selectedAnalyticTypeDtos.stream().map(AbstractBillAnalyticTypeDto::getId).collect(Collectors.toList()));
	}

	@Getter
	@Setter
	@AllArgsConstructor
	public static class AnalyticTypeRow {
		private Boolean selected;
		private AbstractBillAnalyticTypeDto analyticTypeDto;
	}

	private static final long serialVersionUID = 6915176953875637216L;
}
