package ru.argustelecom.box.pa.login;

import javax.inject.Named;

import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
@Named("paLoginViewModel")
public class PersonalAreaLoginViewModel extends ru.argustelecom.system.inf.login.LoginViewModel {

	private static final long serialVersionUID = 7406445781763858246L;

	@Override
	protected boolean checkPasswordExpiryGracePeriod() {
		// Не поддерживаем устаревание паролей
		return false;
	}

	@Override
	public void changePassword() {
		// Не поддерживаем смену пароля
		throw new UnsupportedOperationException();
	}

	@Override
	public void startGracePeriodPasswordChanging() {
		// Не поддерживаем устаревание паролей, и как следствие, не поддерживаем период вежливости для смены пароля
		throw new UnsupportedOperationException();
	}

}
