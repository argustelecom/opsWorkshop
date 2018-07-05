package ru.argustelecom.box.env.customer;

import static ru.argustelecom.box.env.contact.ContactCategory.CUSTOM;
import static ru.argustelecom.box.env.contact.ContactCategory.EMAIL;
import static ru.argustelecom.box.env.contact.ContactCategory.PHONE;
import static ru.argustelecom.box.env.contact.ContactCategory.SKYPE;
import static ru.argustelecom.box.env.customer.CustomerListViewState.CustomerFilter.CONTACT_TYPE;
import static ru.argustelecom.box.env.customer.CustomerListViewState.CustomerFilter.CONTACT_VALUE;
import static ru.argustelecom.box.env.customer.CustomerListViewState.CustomerFilter.CONTRACT_ID;
import static ru.argustelecom.box.env.customer.CustomerListViewState.CustomerFilter.FIRST_NAME;
import static ru.argustelecom.box.env.customer.CustomerListViewState.CustomerFilter.LAST_NAME;
import static ru.argustelecom.box.env.customer.CustomerListViewState.CustomerFilter.ORGANIZATION_NAME;
import static ru.argustelecom.box.env.customer.CustomerListViewState.CustomerFilter.PERSONAL_ACCOUNT;
import static ru.argustelecom.box.env.customer.CustomerListViewState.CustomerFilter.SECOND_NAME;
import static ru.argustelecom.box.env.customer.CustomerListViewState.CustomerFilter.TYPE;
import static ru.argustelecom.box.env.party.model.role.Customer.CustomerQuery;

import java.util.Map;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.BaseEQConvertibleDtoFilterModel;
import ru.argustelecom.box.env.contact.ContactType;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.model.role.Individual.IndividualQuery;
import ru.argustelecom.box.env.party.model.role.Organization.OrganizationQuery;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class CustomerListFilterModel extends BaseEQConvertibleDtoFilterModel<CustomerQuery> {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private CustomerListViewState customerListVs;

	@Override
	@SuppressWarnings({ "unchecked", "ConstantConditions" })
	public void buildPredicates(CustomerQuery customerQuery) {
		Map<String, Object> filterMap = customerListVs.getFilterMap();
		for (Map.Entry<String, Object> filterEntry : filterMap.entrySet()) {
			if (filterEntry != null) {
				switch (filterEntry.getKey()) {
				case TYPE:
					addPredicate(customerQuery.customerType()
							.equal(((CustomerTypeDto) filterEntry.getValue()).getIdentifiable(em)));
					break;
				case FIRST_NAME:
					if (customerListVs.isIndividual())
						addPredicate(((IndividualQuery) customerQuery).byFirstName((String) filterEntry.getValue()));
					break;
				case SECOND_NAME:
					if (customerListVs.isIndividual())
						addPredicate(((IndividualQuery) customerQuery).bySecondName((String) filterEntry.getValue()));
					break;
				case LAST_NAME:
					if (customerListVs.isIndividual())
						addPredicate(((IndividualQuery) customerQuery).byLastName((String) filterEntry.getValue()));
					break;
				case ORGANIZATION_NAME:
					if (!customerListVs.isIndividual())
						addPredicate(((OrganizationQuery) customerQuery).byName((String) filterEntry.getValue()));
					break;
				case CONTACT_TYPE:
					if (!filterMap.containsKey(CONTACT_VALUE))
						break;
					ContactType contactType = (ContactType) filterEntry.getValue();
					addPredicate(customerQuery.byContactType(contactType));
					if (contactType.getCategory().equals(PHONE))
						addPredicate(customerQuery.byContact(contactType, (String) filterMap.get(CONTACT_VALUE)));
					if (contactType.getCategory().equals(EMAIL))
						addPredicate(customerQuery.byContact(contactType, (String) filterMap.get(CONTACT_VALUE)));
					if (contactType.getCategory().equals(SKYPE))
						addPredicate(customerQuery.byContact(contactType, (String) filterMap.get(CONTACT_VALUE)));
					if (contactType.getCategory().equals(CUSTOM))
						addPredicate(customerQuery.byContact(contactType, (String) filterMap.get(CONTACT_VALUE)));
					break;
				case CONTRACT_ID:
					addPredicate(customerQuery.byContract((String) filterMap.get(CONTRACT_ID)));
					break;
				case PERSONAL_ACCOUNT:
					addPredicate(customerQuery.byPersonalAccount((String) filterMap.get(PERSONAL_ACCOUNT)));
					break;
				default:
					break;
				}
			}
		}

		if (customerListVs.getCustomerPropsFilter() != null) {
			addPredicate(customerListVs.getCustomerPropsFilter().toJpaPredicate(customerQuery.customerProperties()));
		}

		if (customerListVs.getPartyPropsFilter() != null) {
			addPredicate(customerListVs.getPartyPropsFilter().toJpaPredicate(customerQuery.partyProperties()));
		}
	}

	@Override
	public Supplier<CustomerQuery> entityQuerySupplier() {
		return () -> {
			boolean hasTypeFilter = customerListVs.getFilterMap().containsKey(TYPE);
			if (customerListVs.getFilterMap().isEmpty() || !hasTypeFilter) {
				return new CustomerQuery<>(Customer.class);
			} else {
				return customerListVs.isIndividual() ? new IndividualQuery() : new OrganizationQuery();
			}
		};
	}

	private static final long serialVersionUID = 139149040276659056L;
}
