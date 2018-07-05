package ru.argustelecom.box.env.party;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;

import lombok.Getter;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.PartyType;
import ru.argustelecom.box.env.type.CurrentType;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "customerTypeAttributesFm")
@PresentationModel
public class CustomerTypeAttributesFrameModel implements Serializable {

	private static final long serialVersionUID = -2583193748075285035L;

	private static final Logger log = Logger.getLogger(CustomerTypeAttributesFrameModel.class);

	private static final String EMPTY_ICON = "fa fa-question";

	@Inject
	private PartyTypeRepository partyTypeRepository;

	@Inject
	private CurrentType currentType;

	@Getter
	private CustomerType customerType;

	public void preRender() {
		refresh();
	}

	public String getCurrentCustomerTypeIconValue() {
		return customerType != null ? customerType.getCategory().getIcon() : EMPTY_ICON;
	}

	public List<PartyType> getAllPartyTypes() {
		return partyTypeRepository.getAllPartyTypes();
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private void refresh() {
		if (currentType.changed(customerType)) {
			customerType = (CustomerType) currentType.getValue();
			log.debugv("postConstruct. customer_type_id={0}", customerType != null ? customerType.getId() : null);
		}
	}

}