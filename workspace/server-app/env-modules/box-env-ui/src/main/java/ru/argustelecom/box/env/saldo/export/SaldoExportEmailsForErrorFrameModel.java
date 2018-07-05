package ru.argustelecom.box.env.saldo.export;

import javax.inject.Named;

import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "emailsForErrorFM")
@PresentationModel
public class SaldoExportEmailsForErrorFrameModel extends SaldoExportEmailsFrameModel {

	private static final long serialVersionUID = 3271561032200722357L;

	@Override
	public void removeEmail(String email) {
		param.removeEmailForErrorMsg(email);
		em.merge(param);
	}

}