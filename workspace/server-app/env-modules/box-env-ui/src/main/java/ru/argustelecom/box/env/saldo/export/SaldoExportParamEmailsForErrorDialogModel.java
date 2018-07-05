package ru.argustelecom.box.env.saldo.export;

import javax.inject.Named;

import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "emailsForErrorEditDM")
@PresentationModel
public class SaldoExportParamEmailsForErrorDialogModel extends SaldoExportParamEmailsEditDialogModel {

	private static final long serialVersionUID = -5886908965005663778L;

	@Override
	public void execute() {
		super.execute();
		param.addEmailForErrorMsg(newEmails.toArray(new String[newEmails.size()]));
		em.merge(param);
		cancel();
	}

}