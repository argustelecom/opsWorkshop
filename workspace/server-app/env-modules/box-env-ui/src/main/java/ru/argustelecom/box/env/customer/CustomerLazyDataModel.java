package ru.argustelecom.box.env.customer;

import static ru.argustelecom.box.env.customer.CustomerLazyDataModel.CustomerSort;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.criteria.JoinType;

import ru.argustelecom.box.env.EQConvertibleDtoFilterModel;
import ru.argustelecom.box.env.EQConvertibleDtoLazyDataModel;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.CustomerTypeInstance_;
import ru.argustelecom.box.env.party.model.CustomerType_;
import ru.argustelecom.box.env.party.model.Party_;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.model.role.Customer.CustomerQuery;
import ru.argustelecom.box.env.party.model.role.Customer_;

public class CustomerLazyDataModel
		extends EQConvertibleDtoLazyDataModel<Customer, CustomerDto, CustomerQuery, CustomerSort> {

	@Inject
	private CustomerDtoTranslator contractDtoTranslator;

	@Inject
	private CustomerListFilterModel customerListFilterModel;

	@PostConstruct
	private void postConstruct() {
		initPathMap();
	}

	private void initPathMap() {
		addPath(CustomerSort.id, query -> query.root().get(Customer_.id));
		addPath(CustomerSort.name, query -> query.root().join(Customer_.party, JoinType.LEFT).get(Party_.sortName));
		addPath(CustomerSort.type, query -> query.root().join(Customer_.typeInstance, JoinType.LEFT)
				.join(CustomerTypeInstance_.type).get(CustomerType_.name));
	}

	@Override
	protected Class<CustomerSort> getSortableEnum() {
		return CustomerSort.class;
	}

	@Override
	protected DefaultDtoTranslator<CustomerDto, Customer> getDtoTranslator() {
		return contractDtoTranslator;
	}

	@Override
	protected EQConvertibleDtoFilterModel<CustomerQuery> getFilterModel() {
		return customerListFilterModel;
	}

	public enum CustomerSort {
		id, name, type
	}

	private static final long serialVersionUID = 1479491966890562061L;
}
