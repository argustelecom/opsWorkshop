package ru.argustelecom.box.env.billing.bill.dto;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static ru.argustelecom.box.env.billing.bill.model.BillPeriod.of;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.toLocalDateTime;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.bill.model.Bill;
import ru.argustelecom.box.env.billing.bill.model.BillPeriod;
import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.system.inf.exception.SystemException;

@DtoTranslator
public class BillAttributesDtoTranslator implements DefaultDtoTranslator<BillAttributesDto, Bill> {

	@Inject
	private BillTypeDtoTranslator billTypeDtoTr;

	@Inject
	private ReportModelTemplateDtoTranslator reportModelTemplateDtoTranslator;

	@Inject
	private CustomerDtoTranslator customerDtoTranslator;

	@Override
	public BillAttributesDto translate(Bill bill) {
		//@formatter:off
		return BillAttributesDto.builder()
				.id(bill.getId())
				.number(bill.getDocumentNumber())
				.billTypeDto(billTypeDtoTr.translate(initializeAndUnproxy(bill.getType())))
				.billDate(bill.getDocumentDate())
				.paymentCondition(bill.getPaymentCondition())
				.groupingMethod(bill.getGroupingMethod())
				.customerDto(customerDtoTranslator.translate(bill.getCustomer()))
				.period(createPeriod(bill))
				.groupId(bill.getGroupId())
				.sumToPay(bill.getTotalAmount())
				.discount(bill.getDiscountAmount())
				.reportModelTemplateDto(ofNullable(bill.getTemplate())
						.map(reportModelTemplateDtoTranslator::translate).orElse(null))
				.providerId(bill.getProvider().getId())
				.brokerId(bill.getBroker() != null ? bill.getBroker().getId() : null)
				.build();
		//@formatter:on
	}

	private BillPeriod createPeriod(Bill bill) {
		switch (bill.getPeriodType()) {
		case CALENDARIAN:
			return of(bill.getPeriodUnit(), toLocalDateTime(bill.getStartDate()));
		case CUSTOM:
			return of(toLocalDateTime(bill.getStartDate()), toLocalDateTime(bill.getEndDate()));
		default:
			throw new SystemException(format("Unsupported bill period type: '%s'", bill.getPeriodType()));
		}
	}
}
