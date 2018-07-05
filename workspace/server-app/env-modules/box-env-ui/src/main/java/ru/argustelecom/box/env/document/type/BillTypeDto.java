package ru.argustelecom.box.env.document.type;

import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.billing.bill.model.BillPeriodType;
import ru.argustelecom.box.env.billing.bill.model.BillType;
import ru.argustelecom.box.env.billing.bill.model.GroupingMethod;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.customer.CustomerTypeDto;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class BillTypeDto extends DocumentTypeDto {

	private CustomerTypeDto customerTypeDto;
	private BillPeriodType billPeriodType;
	private PeriodUnit billingPeriodUnit;
	private PaymentCondition paymentCondition;
	private GroupingMethod groupingMethod;
	private SummaryBillAnalyticTypeDto billAnalyticTypeDto;
	private String description;
	private List<BillAnalyticTypeDto> billAnalyticTypeDtos;
	private List<SummaryBillAnalyticTypeDto> summaryBillAnalyticTypeDtos;
	private List<BusinessObjectDto<PartyRole>> providers;

	@Builder
	public BillTypeDto(Long id, String name, List<ReportModelTemplateDto> reportTemplates,
			CustomerTypeDto customerTypeDto, BillPeriodType billPeriodType, PeriodUnit billingPeriodUnit,
			PaymentCondition paymentCondition, GroupingMethod groupingMethod,
			SummaryBillAnalyticTypeDto billAnalyticTypeDto, String description,
			List<BillAnalyticTypeDto> billAnalyticTypeDtos,
			List<SummaryBillAnalyticTypeDto> summaryBillAnalyticTypeDtos,
			List<BusinessObjectDto<PartyRole>> providers) {
		super(id, name, reportTemplates);
		this.customerTypeDto = customerTypeDto;
		this.billPeriodType = billPeriodType;
		this.billingPeriodUnit = billingPeriodUnit;
		this.paymentCondition = paymentCondition;
		this.groupingMethod = groupingMethod;
		this.billAnalyticTypeDto = billAnalyticTypeDto;
		this.description = description;
		this.billAnalyticTypeDtos = billAnalyticTypeDtos;
		this.summaryBillAnalyticTypeDtos = summaryBillAnalyticTypeDtos;
		this.providers = providers;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return BillTypeDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return BillType.class;
	}
}
