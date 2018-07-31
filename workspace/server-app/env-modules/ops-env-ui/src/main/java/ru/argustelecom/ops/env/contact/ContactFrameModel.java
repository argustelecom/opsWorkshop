package ru.argustelecom.ops.env.contact;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import ru.argustelecom.ops.env.party.model.Party;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class ContactFrameModel implements Serializable {

	private static final long serialVersionUID = -6389169466691695208L;

	@Getter
	private List<Contact> contacts = new ArrayList<>();

	public void preRender(Party party) {
		contacts.clear();
		if (party != null) {
			contacts.addAll(party.getContactInfo().getContacts());
		}
	}

}