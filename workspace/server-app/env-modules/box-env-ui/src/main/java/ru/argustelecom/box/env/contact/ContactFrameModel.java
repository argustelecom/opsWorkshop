package ru.argustelecom.box.env.contact;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import lombok.Getter;
import ru.argustelecom.box.env.party.model.Party;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class ContactFrameModel implements Serializable {

	private static final long serialVersionUID = -6389169466691695208L;

	@Inject
	private ContactDtoTranslator translator;

	@Getter
	private List<ContactDto> contacts = new ArrayList<>();

	public void preRender(Party party) {
		contacts.clear();
		if (party != null) {
			party.getContactInfo().getContacts().forEach(contact -> contacts.add(translator.translate(contact)));
		}
	}

}