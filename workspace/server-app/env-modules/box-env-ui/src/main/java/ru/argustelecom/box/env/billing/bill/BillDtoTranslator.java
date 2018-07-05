package ru.argustelecom.box.env.billing.bill;

import static java.lang.String.format;
import static ru.argustelecom.box.env.billing.bill.model.BillPeriod.of;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.toLocalDateTime;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.bill.model.Bill;
import ru.argustelecom.box.env.billing.bill.model.BillPeriod;
import ru.argustelecom.box.env.customer.CustomerDtoTranslator;
import ru.argustelecom.box.env.customer.CustomerTypeDtoTranslator;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.system.inf.exception.SystemException;

@DtoTranslator
public class BillDtoTranslator implements DefaultDtoTranslator<BillDto, Bill> {

	@Inject
	private CustomerTypeDtoTranslator customerTypeDtoTr;

	@Inject
	private CustomerDtoTranslator customerDtoTr;

	@Inject
	private BillTypeDtoTranslator billTypeDtoTr;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Override
	public BillDto translate(Bill bill) {
		//@formatter:off
		return BillDto.builder()
					.id(bill.getId())
					.number(bill.getDocumentNumber())
					.type(billTypeDtoTr.translate(bill.getType()))
					.totalAmount(bill.getTotalAmount())
					.billDate(bill.getDocumentDate())
					.paymentCondition(bill.getPaymentCondition())
					.groupId(bill.getGroupId())
					.groupingMethod(bill.getGroupingMethod())
					.customerType(customerTypeDtoTr.translate(bill.getCustomer().getTypeInstance().getType()))
					.customer(customerDtoTr.translate(bill.getCustomer()))
					.provider(businessObjectDtoTr.translate(bill.getProvider()))
					.broker(bill.getBroker() != null ? businessObjectDtoTr.translate(bill.getBroker()) : null)
					.period(createPeriod(bill))
					.group(bill.getGroupingObject())
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