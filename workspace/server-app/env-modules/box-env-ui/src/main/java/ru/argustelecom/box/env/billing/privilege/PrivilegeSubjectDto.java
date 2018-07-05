package ru.argustelecom.box.env.billing.privilege;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.provision.ProvisionTermsDto;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class PrivilegeSubjectDto extends ConvertibleDto {

	private Long id;
	private PrivilegeSubjectType type;
	private String name;
	private ProvisionTermsDto provisionTerms;


	@Override
	public Class<? extends Identifiable> getEntityClass() {
		switch (type) {
		case SUBSCRIPTION:
			return Subscription.class;
		case PERSONAL_ACCOUNT:
			return PersonalAccount.class;
		case CUSTOMER:
			return Customer.class;
		default:
			throw new SystemException(String.format("Unsupported subject type '%s'", type));
		}
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return PrivilegeSubjectDtoTranslator.class;
	}
}
