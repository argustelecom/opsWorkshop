package ru.argustelecom.box.env.document.type;

import static ru.argustelecom.box.env.billing.bill.model.BillPeriodType.CALENDARIAN;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.billing.bill.model.BillPeriodType;
import ru.argustelecom.box.env.billing.bill.model.GroupingMethod;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.stl.period.PeriodUnit;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "name")
public class BillTypeCreationDto {
	private String name;
	private String description;
	private List<BusinessObjectDto<PartyRole>> providers;
	private BusinessObjectDto<CustomerType> customerType;
	private BillPeriodType periodType = CALENDARIAN;
	private PeriodUnit periodUnit;
	private PaymentCondition paymentCondition;
	private GroupingMethod groupingMethod;
	private SummaryBillAnalyticTypeDto summaryBillAnalyticType;
}
