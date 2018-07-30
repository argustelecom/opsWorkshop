package ru.argustelecom.ops.env.contact;

import java.io.Serializable;

import javax.inject.Named;

import org.primefaces.context.RequestContext;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.ops.env.party.model.Party;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "contactCreationDM")
@PresentationModel
public class ContactCreationDialogModel implements Serializable {

	private static final long serialVersionUID = -4696173242900741747L;

	@Getter
	@Setter
	private Party party;

	public void open() {
		RequestContext.getCurrentInstance().update("contact_creation_form-contact_creation_dlg");
		RequestContext.getCurrentInstance().execute("PF('contactCreationDlg').show()");
	}

}