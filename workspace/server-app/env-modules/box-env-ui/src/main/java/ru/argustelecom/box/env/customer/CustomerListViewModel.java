package ru.argustelecom.box.env.customer;

import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import lombok.Getter;
import ru.argustelecom.box.env.contact.ContactType;
import ru.argustelecom.box.env.contact.ContactTypeRepository;
import ru.argustelecom.box.env.party.CustomerTypeRepository;
import ru.argustelecom.box.env.party.PartyTypeRepository;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@PresentationModel
public class CustomerListViewModel extends ViewModel {

	private static final long serialVersionUID = -8491798733734428741L;

	@Inject
	private CustomerTypeRepository customerTypeRp;

	@Inject
	private PartyTypeRepository partyTypeRp;

	@Inject
	private CustomerListViewState customerListVs;

	@Inject
	@Getter
	private CustomerLazyDataModel lazyDm;

	@Inject
	private ContactTypeRepository contactTypeRp;

	@Inject
	private CustomerTypeDtoTranslator customerTypeDtoTr;

	private List<CustomerTypeDto> customerTypes;
	private List<ContactType> contactTypes;

	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
		customerListVs.setCustomerPropsFilter(customerTypeRp.createCustomerTypePropertyFilters());
		customerListVs.setPartyPropsFilter(partyTypeRp.createPartyTypePropertyFilters());
	}

	public List<CustomerTypeDto> getCustomerTypes() {
		if (customerTypes == null) {
			List<CustomerType> allCustomerTypes = customerTypeRp.getAllCustomerTypes();
			customerTypes = allCustomerTypes.stream().map(customerTypeDtoTr::translate).collect(toList());
		}
		return customerTypes;
	}

	public List<ContactType> getContactTypes() {
		if (contactTypes == null) {
			contactTypes = contactTypeRp.allContactTypes();
			contactTypes.forEach(EntityManagerUtils::initializeAndUnproxy);
		}
		return contactTypes;
	}
}