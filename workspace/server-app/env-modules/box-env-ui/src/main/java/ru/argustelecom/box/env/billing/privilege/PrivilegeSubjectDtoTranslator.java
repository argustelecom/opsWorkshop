package ru.argustelecom.box.env.billing.privilege;

import static ru.argustelecom.box.env.billing.privilege.PrivilegeSubjectType.CUSTOMER;
import static ru.argustelecom.box.env.billing.privilege.PrivilegeSubjectType.PERSONAL_ACCOUNT;
import static ru.argustelecom.box.env.billing.privilege.PrivilegeSubjectType.SUBSCRIPTION;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.invoice.model.AbstractInvoice;
import ru.argustelecom.box.env.billing.provision.ProvisionTermsDtoTranslator;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.exception.SystemException;

@DtoTranslator
public class PrivilegeSubjectDtoTranslator implements DefaultDtoTranslator<PrivilegeSubjectDto, BusinessObject> {

	@Inject
	private ProvisionTermsDtoTranslator provisionTermsTr;

	@Override
	public PrivilegeSubjectDto translate(BusinessObject businessObject) {
		businessObject = EntityManagerUtils.initializeAndUnproxy(businessObject);
		if (Subscription.class.isAssignableFrom(businessObject.getClass())) {
			Subscription subscription = (Subscription) businessObject;
			String name = String.format("%s %s", SUBSCRIPTION.getName(), subscription.getObjectName());
			return new PrivilegeSubjectDto(subscription.getId(), SUBSCRIPTION, name,
					provisionTermsTr.translate(subscription.getProvisionTerms()));
		}
		if (PersonalAccount.class.isAssignableFrom(businessObject.getClass())) {
			PersonalAccount personalAccount = (PersonalAccount) businessObject;
			return new PrivilegeSubjectDto(personalAccount.getId(), PERSONAL_ACCOUNT, personalAccount.getObjectName(),
					null);
		}
		if (Customer.class.isAssignableFrom(businessObject.getClass())) {
			Customer customer = (Customer) businessObject;
			String name = String.format("%s %s", CUSTOMER.getName(), customer.getObjectName());
			return new PrivilegeSubjectDto(customer.getId(), CUSTOMER, name, null);
		}
		if (AbstractInvoice.class.isAssignableFrom(businessObject.getClass())) {
			return null;
		}

		throw new SystemException(String.format("Unsupported businessObject '%s'", businessObject));
	}

}
