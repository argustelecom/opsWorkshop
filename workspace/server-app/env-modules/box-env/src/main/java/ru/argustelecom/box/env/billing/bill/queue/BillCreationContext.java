package ru.argustelecom.box.env.billing.bill.queue;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;
import ru.argustelecom.box.env.billing.bill.model.BillGroup;
import ru.argustelecom.box.env.billing.bill.model.BillPeriodType;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.inf.queue.api.context.Context;
import ru.argustelecom.box.inf.queue.impl.model.QueueEventImpl;

@Getter
public class BillCreationContext extends Context {

	private static final long serialVersionUID = 5151226591272756323L;

	private Long id;

	private String number;

	private BillGroup billGroup;

	private PaymentCondition paymentCondition;

	private BillPeriodType periodType;

	private PeriodUnit periodUnit;

	private Date periodStartDate;

	private Date periodEndDate;

	private Long billTypeId;

	private Long templateId;

	private Date billDate;

	private Date billCreationDate;

	public BillCreationContext(QueueEventImpl event) {
		super(event);
	}

	@Builder
	public BillCreationContext(Long id, String number, BillGroup billGroup, PaymentCondition paymentCondition,
			BillPeriodType periodType, PeriodUnit periodUnit, Date periodStartDate, Date periodEndDate, Long billTypeId,
			Long templateId, Date billDate, Date billCreationDate) {
		super();
		this.id = id;
		this.number = number;
		this.billGroup = billGroup;
		this.paymentCondition = paymentCondition;
		this.periodType = periodType;
		this.periodUnit = periodUnit;
		this.periodStartDate = periodStartDate;
		this.periodEndDate = periodEndDate;
		this.billTypeId = billTypeId;
		this.templateId = templateId;
		this.billDate = billDate;
		this.billCreationDate = billCreationDate;
	}

}