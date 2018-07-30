package ru.argustelecom.box.env.party;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;

import ru.argustelecom.box.env.party.model.PartyType;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "customerTypeAttributesFm")
@PresentationModel
public class CustomerTypeAttributesFrameModel implements Serializable {

	private static final long serialVersionUID = -2583193748075285035L;

	private static final Logger log = Logger.getLogger(CustomerTypeAttributesFrameModel.class);

	private static final String EMPTY_ICON = "fa fa-question";

	@Inject
	private PartyTypeRepository partyTypeRepository;

	public List<PartyType> getAllPartyTypes() {
		return partyTypeRepository.getAllPartyTypes();
	}

}