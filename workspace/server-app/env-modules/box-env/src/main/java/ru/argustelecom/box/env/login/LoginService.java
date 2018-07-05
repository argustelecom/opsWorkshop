package ru.argustelecom.box.env.login;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Priority;
import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptor;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;

import ru.argustelecom.box.env.login.model.Login;
import ru.argustelecom.box.env.login.model.Login.LoginQuery;
import ru.argustelecom.box.env.login.nls.LoginMessagesBundle;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.inf.login.EmployeePrincipal;
import ru.argustelecom.box.inf.login.PasswordHash;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;
import ru.argustelecom.system.inf.exception.BusinessExceptionWithoutRollback;
import ru.argustelecom.system.inf.login.ILogin;
import ru.argustelecom.system.inf.login.ILoginService;

@Stateless
@Alternative
@LocalBean
@Local(ILoginService.class)
@Priority(Interceptor.Priority.APPLICATION)
public class LoginService implements ILoginService {

	@PersistenceContext
	private EntityManager em;

	@Override
	public ILogin find(Serializable loginId) {
		return em.find(Login.class, loginId);
	}

	@Override
	public ILogin find(String displayName) throws BusinessExceptionWithoutRollback {
		LoginQuery query = new LoginQuery();
		query.and(query.username().equal(displayName));

		Login result = null;
		try {
			result = query.getSingleResult(em);
		} catch (NoResultException | NonUniqueResultException e) {
			LoginMessagesBundle messages = LocaleUtils.getMessages(LoginMessagesBundle.class);
			throw new BusinessExceptionWithoutRollback(messages.loginNotFoundOrNotUnique(displayName), e);
		}

		return result;
	}

	@Override
	public ILogin currentLogin() {
		EmployeePrincipal loggedUser = EmployeePrincipal.instance();
		return find(loggedUser.getUid());
	}

	@Override
	public void lockLogin(ILogin ilogin) {
		Login login = ilogin.getDelegate(Login.class);

		if (!login.isLocked()) {
			login.setLockDate(new Date());
		}
	}

	@Override
	public void unlockLogin(ILogin ilogin) {
		Login login = ilogin.getDelegate(Login.class);

		if (login.isLocked()) {
			login.setLockDate(null);
		}
	}

	@Override
	public void expireLogin(ILogin ilogin) {
		expireLogin(ilogin, new Date());
	}

	public void expireLogin(ILogin ilogin, Date expiryDate) {
		Login login = ilogin.getDelegate(Login.class);

		if (!login.isExpired()) {
			login.setExpiryDate(expiryDate);
		}
	}

	@Override
	public void deleteLogin(ILogin ilogin) {
		Login login = ilogin.getDelegate(Login.class);
		em.remove(login);
	}

	@Override
	public void createLogin(ILogin ilogin, String password) {
		Login login = ilogin.getDelegate(Login.class);

		em.persist(login);
		changePassword(login, password);
	}

	@Override
	public Set<ConstraintViolation<?>> validatePassword(String arg0, Locale arg1) {
		return Collections.emptySet();
	}

	@Override
	@NamedNativeQuery(name = CHANGE_PASS_QUERY_NAME, query = "UPDATE system.login SET password = :pass, salt = :salt WHERE uid = :id")
	public void changePassword(ILogin ilogin, String password) {
		Login login = ilogin.getDelegate(Login.class);

		// TODO Сейчас не учитывается период вежливости. Нужно предусмотреть определение статуса в период вежливости
		if (login.isExpired()) {
			// TODO Потом будет специальная настройка, с какой периодичностью должны устаревать все пароли
			login.setExpiryDate(null);
		}
		em.flush();

		// TODO Определить настройку алгоритма хеширования
		PasswordHash hash = new PasswordHash(password, PasswordHash.Algorithm.SHA512);

		Query query = em.createNamedQuery(CHANGE_PASS_QUERY_NAME);
		query.setParameter("pass", hash.getHash());
		query.setParameter("salt", PasswordHash.saltToString(hash.getSalt()));
		query.setParameter("id", login.getId());
		query.executeUpdate();
	}

	private static final String CHANGE_PASS_QUERY_NAME = "LoginService.changePassword";

	@Override
	public void changeTimezone(ILogin ilogin, String timeZone) {
		Login login = ilogin.getDelegate(Login.class);
		login.setTimeZone(timeZone);
	}

	@Override
	public void changeLocale(ILogin ilogin, Locale locale) {
		Login login = ilogin.getDelegate(Login.class);
		login.setLocale(locale);
	}

	public Employee getCurrentEmployee() {
		EmployeePrincipal principal = checkNotNull(EmployeePrincipal.instance());
		return em.find(Employee.class, principal.getEmployeeId());
	}

	@Override
	@SuppressWarnings("unchecked")
	@NamedNativeQuery(name = GET_TIMEZONES_NAMES_QUERY_NAME, query = "SELECT tzname FROM system.named_time_zone")
	public List<String> getTimeZonesNames() {
		return em.createNamedQuery(GET_TIMEZONES_NAMES_QUERY_NAME).getResultList();
	}

	private static final String GET_TIMEZONES_NAMES_QUERY_NAME = "LoginService.getTimeZonesNames";

	private static final long serialVersionUID = 428436160950328170L;
}
