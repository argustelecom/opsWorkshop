package ru.argustelecom.box.env.saldo.export;

import javax.inject.Named;

import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "emailsForSuccessEditDM")
@PresentationModel
public class SaldoExportParamEmailsForSuccessEditDialogModel extends SaldoExportParamEmailsEditDialogModel {

	private static final long serialVersionUID = 6688737743860160062L;

	@Override
	public void execute() {
		super.execute();
		param.addEmailForSuccessMsg(newEmails.toArray(new String[newEmails.size()]));
		em.merge(param);
		cancel();
	}

}