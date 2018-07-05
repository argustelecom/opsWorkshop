package ru.argustelecom.box.env.login;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.logging.Logger;

import com.google.common.base.Strings;

import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.login.model.PersonalAreaLogin;
import ru.argustelecom.box.env.login.model.PersonalAreaLogin.PersonalAreaLoginQuery;
import ru.argustelecom.box.env.login.nls.LoginMessagesBundle;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.chrono.TZ;
import ru.argustelecom.system.inf.exception.BusinessException;
import ru.argustelecom.system.inf.login.ArgusPrincipal;

@Repository
public class PersonalAreaLoginRepository {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequence;

	public PersonalAreaLogin findLogin(String displayName) {
		PersonalAreaLoginQuery query = new PersonalAreaLoginQuery();
		query.and(query.username().equal(displayName));
		return query.getSingleResult(em, false);
	}

	public PersonalAreaLogin findLogin(Customer customer) {
		PersonalAreaLoginQuery query = new PersonalAreaLoginQuery();
		query.and(query.customer().equal(customer));
		return query.getSingleResult(em, false);
	}

	public PersonalAreaLogin currentLogin() {
		ArgusPrincipal principal = ArgusPrincipal.instance();
		if (principal != null) {
			PersonalAreaLogin login = em.find(PersonalAreaLogin.class, principal.getLoginId());
			if (login == null) {
				log.warnv("Не удалось определить залогиненого пользователя личного кабинета. CurrentLogin:{0}",
						principal.getLoginName());
			}
			return login;
		}
		return null;
	}

	public PersonalAreaLogin createLogin(Customer customer, String username, String password) {
		checkArgument(customer != null);
		checkArgument(!Strings.isNullOrEmpty(username));
		checkArgument(!Strings.isNullOrEmpty(password));

		if (findLogin(username) != null) {
			LoginMessagesBundle messages = LocaleUtils.getMessages(LoginMessagesBundle.class);
			throw new BusinessException(messages.loginAlreadyExists(username));
		}

		Long loginId = idSequence.nextValue(PersonalAreaLogin.class);
		PersonalAreaLogin login = new PersonalAreaLogin(loginId);
		login.setUsername(username);
		login.setPassword(password);
		login.setCustomer(customer);
		login.setLocale(Locale.getDefault());
		login.setTimeZone(TZ.getServerTimeZone().getID());

		em.persist(login);
		return login;
	}

	public void removeLogin(PersonalAreaLogin login) {
		em.remove(login);
	}

	public void changeCredentials(PersonalAreaLogin login, String currentPassword, String newPassword,
			String newLogin) {

		String loginPassword = login.getPassword();
		if (!Objects.equals(loginPassword, currentPassword)) {
			LoginMessagesBundle messages = LocaleUtils.getMessages(LoginMessagesBundle.class);
			throw new BusinessException(messages.confirmationDoesNotMatchPassword());
		}

		changeLogin(login, newLogin);
		changePassword(login, newPassword);
	}

	public void changeLogin(PersonalAreaLogin login, String newLogin) {
		if (newLogin != null && !Objects.equals(login.getUsername(), newLogin)) {
			if (findLogin(newLogin) != null) {
				LoginMessagesBundle messages = LocaleUtils.getMessages(LoginMessagesBundle.class);
				throw new BusinessException(messages.loginAlreadyExists(newLogin));
			}
			login.setUsername(newLogin);
		}
	}

	public void changePassword(PersonalAreaLogin login, String newPassword) {
		if (newPassword != null) {
			login.setPassword(newPassword);
		}
	}

	private static final Logger log = Logger.getLogger(PersonalAreaLoginRepository.class);
}
