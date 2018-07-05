package ru.argustelecom.box.env.billing.bill;

import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.billing.bill.model.BillPeriodType;
import ru.argustelecom.box.env.billing.bill.model.BillType;
import ru.argustelecom.box.env.billing.bill.model.GroupingMethod;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.customer.CustomerTypeDto;
import ru.argustelecom.box.env.document.type.ReportModelTemplateDto;
import ru.argustelecom.box.env.document.type.DocumentTypeDto;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BillCreationTypeDto extends DocumentTypeDto {

	private BillPeriodType billPeriodType;
	private PeriodUnit periodUnit;
	private CustomerTypeDto customerType;
	private GroupingMethod groupingMethod;
	private PaymentCondition paymentCondition;
	private String description;
	private List<BusinessObjectDto<PartyRole>> providers;

	@Builder
	public BillCreationTypeDto(Long id, String name, List<ReportModelTemplateDto> reportTemplates,
			BillPeriodType billPeriodType, PeriodUnit periodUnit, CustomerTypeDto customerType,
			GroupingMethod groupingMethod, PaymentCondition paymentCondition, String description,
			List<BusinessObjectDto<PartyRole>> providers) {
		super(id, name, reportTemplates);
		this.billPeriodType = billPeriodType;
		this.periodUnit = periodUnit;
		this.customerType = customerType;
		this.groupingMethod = groupingMethod;
		this.paymentCondition = paymentCondition;
		this.description = description;
		this.providers = providers;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return BillCreationTypeDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return BillType.class;
	}

}