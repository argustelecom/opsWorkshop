package ru.argustelecom.box.env.billing.bill;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.bill.model.BillType;
import ru.argustelecom.box.env.customer.CustomerTypeDto;
import ru.argustelecom.box.env.customer.CustomerTypeDtoTranslator;
import ru.argustelecom.box.env.document.type.ReportModelTemplateDtoTranslator;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.dto.DefaultDtoConverterUtils;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class BillCreationTypeDtoTranslator implements DefaultDtoTranslator<BillCreationTypeDto, BillType> {

	@Inject
	private ReportModelTemplateDtoTranslator reportModelTemplateDtoTr;

	@Inject
	private CustomerTypeDtoTranslator customerTypeDtoTr;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Override
	public BillCreationTypeDto translate(BillType billType) {
		CustomerTypeDto customerType = billType.getCustomerType() != null
				? customerTypeDtoTr.translate(billType.getCustomerType()) : null;

		//@formatter:off
		return BillCreationTypeDto.builder()
				.id(billType.getId())
				.name(billType.getObjectName())
				.reportTemplates(DefaultDtoConverterUtils.translate(reportModelTemplateDtoTr, billType.getTemplates()))
				.billPeriodType(billType.getPeriodType())
				.periodUnit(billType.getPeriodUnit())
				.customerType(customerType)
				.groupingMethod(billType.getGroupingMethod())
				.paymentCondition(billType.getPaymentCondition())
				.description(billType.getDescription())
				.providers(businessObjectDtoTr.translate(billType.getProviders()))
				.build();
		//@formatter:on
	}

}