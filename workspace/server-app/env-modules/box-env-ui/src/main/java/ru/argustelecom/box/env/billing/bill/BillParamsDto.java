package ru.argustelecom.box.env.billing.bill;

import java.util.Date;

import com.google.common.collect.Lists;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.billing.bill.model.BillPeriod;
import ru.argustelecom.box.env.billing.bill.model.GroupingMethod;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.customer.CustomerDto;
import ru.argustelecom.box.env.customer.CustomerTypeDto;
import ru.argustelecom.box.env.document.type.ReportModelTemplateDto;
import ru.argustelecom.box.env.dto.BusinessObjectDto;

@Getter
@Setter
@NoArgsConstructor
public class BillParamsDto {

	private BillContext currentContext;
	private BillCreationTypeDto billType;
	private String billNumber;
	private CustomerTypeDto customerType;
	private CustomerDto customer;
	private PaymentCondition paymentCondition;
	private BusinessObjectDto<?> groupingObject;
	private GroupingMethod groupingMethod;
	private BillPeriod billPeriod;
	private Date billDate;
	private ReportModelTemplateDto template;
	private boolean templateRendered = true;

	@Builder
	public BillParamsDto(BillContext currentContext, BillCreationTypeDto billType, String billNumber,
			CustomerTypeDto customerType, CustomerDto customer, PaymentCondition paymentCondition,
			BusinessObjectDto<?> groupingObject, GroupingMethod groupingMethod, BillPeriod billPeriod, Date billDate,
			ReportModelTemplateDto template) {
		super();
		this.currentContext = currentContext;
		this.billType = billType;
		this.billNumber = billNumber;
		this.customerType = customerType;
		this.customer = customer;
		this.paymentCondition = paymentCondition;
		this.groupingObject = groupingObject;
		this.groupingMethod = groupingMethod;
		this.billPeriod = billPeriod;
		this.billDate = billDate;
		this.template = template;
	}

	public void setBillType(BillCreationTypeDto billType) {
		if (currentContext.equals(BillContext.LIST) || currentContext.equals(BillContext.MASS)) {
			customerType = null;
			customer = null;
			paymentCondition = null;
			groupingObject = null;
			groupingMethod = null;
			template = null;
		}

		if (billType != null) {
			customerType = billType.getCustomerType();
			groupingMethod = billType.getGroupingMethod();
			paymentCondition = billType.getPaymentCondition();
		}
		this.billType = billType;
	}

	public enum BillContext {
		CUSTOMER, PERSONAL_ACCOUNT, CONTRACT, LIST, MASS;

		public static boolean isFromCard(BillContext context) {
			return Lists.newArrayList(CUSTOMER, PERSONAL_ACCOUNT, CONTRACT).contains(context);
		}
	}

}
