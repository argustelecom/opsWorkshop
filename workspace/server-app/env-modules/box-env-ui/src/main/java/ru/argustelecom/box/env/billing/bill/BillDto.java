package ru.argustelecom.box.env.billing.bill;

import java.util.Date;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.env.billing.bill.model.Bill;
import ru.argustelecom.box.env.billing.bill.model.BillPeriod;
import ru.argustelecom.box.env.billing.bill.model.GroupingMethod;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.customer.CustomerDto;
import ru.argustelecom.box.env.customer.CustomerTypeDto;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.system.inf.modelbase.Identifiable;

/**
 * DTO для отображения результатов фильтрации в списках счётов. Но в целом можно позиционировать как общий DTO для
 * счёта, т.к. содержит в себе всю основную информацию, за исключением расчётов аналитик.
 */
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = { "id" }, callSuper = false)
public class BillDto extends ConvertibleDto {

	private Long id;
	private String number;
	private BillTypeDto type;
	private Money totalAmount;
	private Date billDate;
	private PaymentCondition paymentCondition;
	private GroupingMethod groupingMethod;
	private Long groupId;
	private Identifiable group;
	private CustomerTypeDto customerType;
	private CustomerDto customer;
	private BusinessObjectDto<PartyRole> provider;
	private BusinessObjectDto<Owner> broker;
	private BillPeriod period;

	@Builder
	public BillDto(Long id, String number, BillTypeDto type, Money totalAmount, Date billDate, Long groupId,
			PaymentCondition paymentCondition, GroupingMethod groupingMethod, CustomerTypeDto customerType,
			CustomerDto customer, BillPeriod period, BusinessObjectDto<PartyRole> provider,
			BusinessObjectDto<Owner> broker, Identifiable group) {
		this.id = id;
		this.number = number;
		this.totalAmount = totalAmount;
		this.type = type;
		this.billDate = billDate;
		this.paymentCondition = paymentCondition;
		this.groupId = groupId;
		this.groupingMethod = groupingMethod;
		this.customerType = customerType;
		this.customer = customer;
		this.period = period;
		this.provider = provider;
		this.broker = broker;
		this.group = group;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return BillDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return Bill.class;
	}

}