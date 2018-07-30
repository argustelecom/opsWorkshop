package ru.argustelecom.box.env.party;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.PartyType;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "customerTypeCreationDm")
@PresentationModel
public class CustomerTypeCreationDialogModel implements Serializable {

	private static final long serialVersionUID = -6080540983649523620L;

	@Inject
	private CustomerTypeRepository customerTypeRepository;

	@Inject
	private PartyTypeRepository partyTypeRepository;

	@Getter
	@Setter
	private CustomerCategory newCategory;

	@Getter
	@Setter
	private PartyType newPartyType;

	@Getter
	@Setter
	private String newName;

	@Getter
	@Setter
	private String newKeyword;

	@Getter
	@Setter
	private String newDescription;

	public void onDialogOpen() {
		RequestContext.getCurrentInstance().execute("PF('customerTypeCreationPanelVar').hide()");
		RequestContext.getCurrentInstance().update("customer_type_creation_form");
		RequestContext.getCurrentInstance().execute("PF('customerTypeCreationDlgVar').show()");
	}

	public CustomerType create() {
		CustomerType newCustomerType = customerTypeRepository.createCustomerType(newName, newCategory, newPartyType,
				newKeyword, newDescription);
		cleanParams();
		return newCustomerType;
	}

	public void cleanParams() {
		newCategory = null;
		newPartyType = null;
		newName = null;
		newKeyword = null;
		newDescription = null;
	}

	public List<PartyType> getAllPartyTypes() {
		return partyTypeRepository.getAllPartyTypes();
	}

}