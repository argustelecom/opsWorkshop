package ru.argustelecom.box.env.billing.invoice;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class PersonalAccountDto extends ConvertibleDto {
	private Long id;
	private BusinessObjectDto<Customer> customer;
	private Money threshold;

	public PersonalAccountDto(Long id, BusinessObjectDto<Customer> customer, Money threshold) {
		this.id = id;
		this.customer = customer;
		this.threshold = threshold;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return PersonalAccountDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return PersonalAccount.class;
	}
}