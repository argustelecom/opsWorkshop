package ru.argustelecom.box.env.party;

import java.io.Serializable;

import javax.inject.Named;

import org.jboss.logging.Logger;

import lombok.Getter;
import ru.argustelecom.box.env.party.model.PartyType;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "partyTypeAttributesFm")
@PresentationModel
public class PartyTypeAttributesFrameModel implements Serializable {

	private static final long serialVersionUID = -2583193748075285035L;

	private static final Logger log = Logger.getLogger(PartyTypeAttributesFrameModel.class);

	private static final String EMPTY_ICON = "fa fa-question";

	@Getter
	private PartyType partyType;

	public String getCurrentPartyTypeIconValue() {
		return partyType != null ? partyType.getCategory().getIcon() : EMPTY_ICON;
	}

}