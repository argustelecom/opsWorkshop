package ru.argustelecom.box.pa;

import static com.google.common.base.Preconditions.checkState;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.login.PersonalAreaLoginRepository;
import ru.argustelecom.box.env.login.model.PersonalAreaLogin;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@PresentationModel
@Named("personalAreaSettingsVM")
public class PersonalAreaSettingsViewModel extends ViewModel {

	@Inject
	private PersonalAreaLoginRepository loginRepo;

	private PersonalAreaLogin login;

	private String newLogin;
	private String newPassword;
	private String currentPassword;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		refresh();
	}

	private void refresh() {
		if (login == null) {
			login = loginRepo.currentLogin();
			checkState(login != null);
		}
	}

	public void changeCredentials() {
		loginRepo.changeCredentials(login, currentPassword, newPassword, newLogin);

		newLogin = null;
		newPassword = null;
		currentPassword = null;

		Notification.info("", "Параметры учетной записи изменены");
	}

	public String getNewLogin() {
		return newLogin;
	}

	public void setNewLogin(String newLogin) {
		this.newLogin = newLogin;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}

	public PersonalAreaLogin getLogin() {
		return login;
	}

	private static final long serialVersionUID = -4437793304534527629L;

}
