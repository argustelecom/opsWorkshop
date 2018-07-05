package ru.argustelecom.box.env.document.type;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.bill.model.BillType;
import ru.argustelecom.box.env.customer.CustomerTypeDtoTranslator;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.dto.DefaultDtoConverterUtils;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class BillTypeDtoTranslator implements DefaultDtoTranslator<BillTypeDto, BillType> {

	@Inject
	private CustomerTypeDtoTranslator customerTypeDtoTranslator;

	@Inject
	private BillAnalyticTypeDtoTranslator billAnalyticTypeDtoTranslator;

	@Inject
	private SummaryBillAnalyticTypeDtoTranslator summaryBillAnalyticTypeDtoTranslator;

	@Inject
	private ReportModelTemplateDtoTranslator reportModelTemplateDtoTranslator;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Override
	public BillTypeDto translate(BillType billType) {
		//@formatter:off
		return BillTypeDto.builder()
				.id(billType.getId())
				.name(billType.getObjectName())
				.customerTypeDto(billType.getCustomerType() != null
						? customerTypeDtoTranslator.translate(billType.getCustomerType()) : null)
				.billingPeriodUnit(billType.getPeriodUnit())
				.billPeriodType(billType.getPeriodType())
				.groupingMethod(billType.getGroupingMethod())
				.billAnalyticTypeDto(billType.getSummaryToPay() != null
						? summaryBillAnalyticTypeDtoTranslator.translate(billType.getSummaryToPay()) : null)
				.paymentCondition(billType.getPaymentCondition())
				.description(billType.getDescription())
				.billAnalyticTypeDtos(DefaultDtoConverterUtils.translate(billAnalyticTypeDtoTranslator,
						billType.getBillAnalyticTypes()))
				.summaryBillAnalyticTypeDtos(DefaultDtoConverterUtils.translate(summaryBillAnalyticTypeDtoTranslator,
						billType.getSummaryBillAnalyticTypes()))
				.reportTemplates(DefaultDtoConverterUtils.translate(reportModelTemplateDtoTranslator,
						billType.getTemplates()))
				.providers(businessObjectDtoTr.translate(billType.getProviders()))
				.build();
		//@formatter:on
	}
}
