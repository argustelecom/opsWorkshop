package ru.argustelecom.box.env.personnel;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.jboss.logging.Logger;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.login.model.Login;
import ru.argustelecom.box.env.login.nls.LoginMessagesBundle;
import ru.argustelecom.box.env.overall.nls.OverallMessagesBundle;
import ru.argustelecom.box.env.party.CurrentPartyRole;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.page.LanguageBean;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.chrono.TZ;
import ru.argustelecom.system.inf.login.ILoginService;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "employeeLoginFM")
@PresentationModel
public class EmployeeLoginFrameModel implements Serializable {

	private static final long serialVersionUID = 2287312932764901956L;

	private static final Logger log = Logger.getLogger(EmployeeLoginFrameModel.class);

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequence;

	@Inject
	private ILoginService loginService;

	@Inject
	private CurrentPartyRole currentPartyRole;

	@Inject
	private LanguageBean languageBean;
	private Employee employee;
	private Login login;

	private String newUsername;
	private String newEmail;
	private String newPassword;
	private String newConfirmPassword;
	private String newDescription;

	private LoginMessagesBundle loginMb;

	@Getter
	private List<LocaleDto> supportedLocales;

	private LocaleDto loginLocale;

	@PostConstruct
	protected void postConstruct() {
		loginMb = LocaleUtils.getMessages(LoginMessagesBundle.class);
		supportedLocales = languageBean.getSupportedLocales().stream()
				.map(l -> new LocaleDto(languageBean.getDisplayName(l), l))
				.sorted(Comparator.comparing(LocaleDto::getName)).collect(Collectors.toList());
	}

	public void preRender() {
		refresh();
	}

	protected void refresh() {
		checkNotNull(currentPartyRole.getValue(), "currentPartyRole required");
		if (currentPartyRole.changed(employee)) {
			employee = (Employee) currentPartyRole.getValue();
			log.debugv("postConstruct. employee_id={0}", employee.getId());
		}
	}

	public Login getLogin() {
		if (login == null) {
			try {
				login = em.createNamedQuery(Login.FIND_LOGIN_BY_EMPLOYEE, Login.class)
						.setParameter("employee", employee).getSingleResult();
			} catch (NoResultException ex) {
				return null;
			}
		}
		return login;
	}

	public LocaleDto getLoginLocale() {
		if (loginLocale == null && getLogin() != null) {
			loginLocale = supportedLocales.stream().filter(l -> Objects.equals(l.getLocale(), getLogin().getLocale()))
					.findFirst().orElse(null);
		}
		return loginLocale;
	}

	public void setLoginLocale(LocaleDto loginLocale) {
		if (getLogin() != null) {
			checkNotNull(loginLocale);
			getLogin().setLocale(loginLocale.getLocale());
			this.loginLocale = loginLocale;
		}
	}

	public void createLogin() {
		if (!newPassword.equals(newConfirmPassword)) {
			Notification.error(loginMb.cannotCreateLogin(), loginMb.incorrectPasswordConfirmation());
			return;
		}
		Login newLogin = new Login(idSequence.nextValue(Login.class));
		newLogin.setEmployee(employee);
		newLogin.setUsername(newUsername);
		newLogin.setEmail(newEmail);
		newLogin.setDescription(newDescription);
		newLogin.setLocale(Locale.forLanguageTag("ru-RU"));
		newLogin.setTimeZone(TZ.getServerZoneId().toString());
		loginService.createLogin(newLogin, newPassword);
		Notification.info(loginMb.loginCreated(),
				loginMb.loginSuccessfullyCreated(newLogin.getLoginName(), employee.getObjectName()));
	}

	public void togglePasswordLock() {
		if (login.isLocked()) {
			loginService.unlockLogin(login);
		} else {
			loginService.lockLogin(login);
		}
	}

	public void changePassword() {
		loginService.changePassword(login, newPassword);
	}

	public void expireLogin() {
		loginService.expireLogin(login);
	}

	public void sendLoginInfoToEmail() {
		OverallMessagesBundle overallMessages = LocaleUtils.getMessages(OverallMessagesBundle.class);
		Notification.info(overallMessages.warning(), overallMessages.notImplemented());
	}

	public void cleanLoginCreationParams() {
		newUsername = null;
		newEmail = null;
		newPassword = null;
		newConfirmPassword = null;
	}

	public void cleanChangePasswordParams() {
		newPassword = null;
		newConfirmPassword = null;
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public Employee getEmployee() {
		return employee;
	}

	public String getNewUsername() {
		return newUsername;
	}

	public void setNewUsername(String newUsername) {
		this.newUsername = newUsername;
	}

	public String getNewEmail() {
		return newEmail;
	}

	public void setNewEmail(String newEmail) {
		this.newEmail = newEmail;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getNewConfirmPassword() {
		return newConfirmPassword;
	}

	public void setNewConfirmPassword(String newConfirmPassword) {
		this.newConfirmPassword = newConfirmPassword;
	}

	public String getNewDescription() {
		return newDescription;
	}

	public void setNewDescription(String newDescription) {
		this.newDescription = newDescription;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@EqualsAndHashCode(of = { "locale" })
	public static class LocaleDto {
		private String name;
		private Locale locale;

		@Override
		public String toString() {
			return name;
		}
	}
}