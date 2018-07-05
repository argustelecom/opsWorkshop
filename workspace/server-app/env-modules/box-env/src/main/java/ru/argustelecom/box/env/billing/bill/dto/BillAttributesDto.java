package ru.argustelecom.box.env.billing.bill.dto;

import java.util.Date;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.billing.bill.model.Bill;
import ru.argustelecom.box.env.billing.bill.model.BillPeriod;
import ru.argustelecom.box.env.billing.bill.model.GroupingMethod;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.dto2.ConvertibleDto;
import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@EqualsAndHashCode(of = { "id" }, callSuper = false)
public class BillAttributesDto extends ConvertibleDto {

	private Long id;
	@Setter
	private String number;
	private BillTypeDto billTypeDto;
	@Setter
	private Date billDate;
	private PaymentCondition paymentCondition;
	private GroupingMethod groupingMethod;
	private CustomerDto customerDto;
	private Long groupId;
	private Long providerId;
	private Long brokerId;
	private Long personalAccountId;
	private Money sumToPay;
	private Money discount;
	private BillPeriod period;
	@Setter
	private ReportModelTemplateDto reportModelTemplateDto;

	@Builder
	public BillAttributesDto(Long id, String number, BillTypeDto billTypeDto, Date billDate,
			PaymentCondition paymentCondition, GroupingMethod groupingMethod, CustomerDto customerDto, Long groupId,
			Long personalAccountId, Money sumToPay, Money discount, BillPeriod period,
			ReportModelTemplateDto reportModelTemplateDto, Long providerId, Long brokerId) {
		this.id = id;
		this.number = number;
		this.billTypeDto = billTypeDto;
		this.billDate = billDate;
		this.paymentCondition = paymentCondition;
		this.groupingMethod = groupingMethod;
		this.customerDto = customerDto;
		this.groupId = groupId;
		this.personalAccountId = personalAccountId;
		this.sumToPay = sumToPay;
		this.discount = discount;
		this.period = period;
		this.reportModelTemplateDto = reportModelTemplateDto;
		this.providerId = providerId;
		this.brokerId = brokerId;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return BillAttributesDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return Bill.class;
	}

}
