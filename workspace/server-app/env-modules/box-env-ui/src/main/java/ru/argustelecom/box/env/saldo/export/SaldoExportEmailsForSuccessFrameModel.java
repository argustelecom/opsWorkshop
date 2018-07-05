package ru.argustelecom.box.env.saldo.export;

import javax.inject.Named;

import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "emailsForSuccessFM")
@PresentationModel
public class SaldoExportEmailsForSuccessFrameModel extends SaldoExportEmailsFrameModel {

	private static final long serialVersionUID = 6449038846023155253L;

	@Override
	public void removeEmail(String email) {
		param.removeEmailForSuccessMsg(email);
		em.merge(param);
	}

}