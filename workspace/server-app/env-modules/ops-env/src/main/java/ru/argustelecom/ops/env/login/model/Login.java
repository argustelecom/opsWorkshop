package ru.argustelecom.ops.env.login.model;

import static com.google.common.base.Preconditions.checkState;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.constraints.Email;

import ru.argustelecom.ops.env.party.model.role.Employee;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryDateFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryLogicalFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;
import ru.argustelecom.system.inf.login.ILogin;

//@formatter:off
@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "login", uniqueConstraints = {
	@UniqueConstraint(name = "uc_login_username", columnNames = { "username" })
})
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "uid")),
	@AttributeOverride(name = "username", column = @Column(length = 50)),
	@AttributeOverride(name = "description", column = @Column(length = 250)),
})
@NamedQueries({
	@NamedQuery(name = Login.FIND_LOGIN_BY_EMPLOYEE, query = "select l from Login l where l.employee = :employee")	
})//@formatter:on
public class Login extends AbstractLogin implements ILogin {

	public static final String FIND_LOGIN_BY_EMPLOYEE = "Login.findLoginByEmployee";

	@ManyToOne
	private Employee employee;

	@Email
	@Column(length = 100)
	private String email;

	@Temporal(TemporalType.TIMESTAMP)
	private Date logonTime;

	@Temporal(TemporalType.DATE)
	private Date expiryDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date lockDate;

	private boolean isSys;

	protected Login() {
	}

	public Login(Long id) {
		super(id);
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public Date getLogonTime() {
		return logonTime;
	}

	public void setLogonTime(Date logonTime) {
		this.logonTime = logonTime;
	}

	public LoginStatus getLoginStatus() {
		if (isExpired() && isLocked())
			return LoginStatus.EXPIRED_N_LOCKED;
		if (isExpired())
			return LoginStatus.EXPIRED;
		if (isLocked())
			return LoginStatus.LOCKED;

		return LoginStatus.OPEN;
	}

	@Override
	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public boolean isExpired() {
		return expiryDate != null && expiryDate.before(new Date());
	}

	@Override
	public Date getLockDate() {
		return lockDate;
	}

	public void setLockDate(Date lockDate) {
		this.lockDate = lockDate;
	}

	public boolean isLocked() {
		return lockDate != null && lockDate.before(new Date());
	}

	@Override
	public Boolean getSys() {
		return isSys;
	}

	public void setSys(boolean isSys) {
		this.isSys = isSys;
	}

	// ***************************************************************************************************************
	// ILogin Support
	// ***************************************************************************************************************

	@Override
	public <T> T getDelegate(Class<T> delegateClass) {
		checkState(delegateClass.isAssignableFrom(this.getClass()));
		return delegateClass.cast(this);
	}

	@Override
	public Long getLoginId() {
		return getId();
	}

	@Override
	public Long getLoginOwnerId() {
		return getEmployee() != null ? getEmployee().getId() : null;
	}

	@Override
	public String getLoginName() {
		return getUsername();
	}

	@Override
	public String getStatus() {
		return getLoginStatus().toString();
	}

	public static class LoginQuery extends AbstractLoginQuery<Login> {

		private EntityQueryEntityFilter<Login, Employee> employee = createEntityFilter(Login_.employee);
		private EntityQueryStringFilter<Login> email = createStringFilter(Login_.email);
		private EntityQueryDateFilter<Login> logonTime = createDateFilter(Login_.logonTime);
		private EntityQueryDateFilter<Login> expiryDate = createDateFilter(Login_.expiryDate);
		private EntityQueryDateFilter<Login> lockDate = createDateFilter(Login_.lockDate);
		private EntityQueryLogicalFilter<Login> sys = createLogicalFilter(Login_.isSys);

		public LoginQuery() {
			super(Login.class);
		}

		public EntityQueryEntityFilter<Login, Employee> employee() {
			return employee;
		}

		public EntityQueryStringFilter<Login> email() {
			return email;
		}

		public EntityQueryDateFilter<Login> logonTime() {
			return logonTime;
		}

		public EntityQueryDateFilter<Login> expiryDate() {
			return expiryDate;
		}

		public EntityQueryDateFilter<Login> lockDate() {
			return lockDate;
		}

		public EntityQueryLogicalFilter<Login> sys() {
			return sys;
		}
	}

	private static final long serialVersionUID = -3642594162798217348L;
}
