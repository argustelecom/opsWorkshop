package ru.argustelecom.box.env.login.model;

import static com.google.common.base.Preconditions.checkArgument;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.inf.login.PasswordEncrypt;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;

import com.google.common.base.Strings;

//@formatter:off
@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "pa_login", uniqueConstraints = {
	@UniqueConstraint(name = "uc_pa_login_username", columnNames = { "username" }) 
})
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "uid"))	
})//@formatter:on
public class PersonalAreaLogin extends AbstractLogin {

	@ManyToOne
	private Customer customer;

	@Column(name = "password")
	private String encryptedPassword;

	@Transient
	private PasswordEncrypt passwordEncrypt;

	protected PersonalAreaLogin() {
		super();
	}

	public PersonalAreaLogin(Long id) {
		super(id);
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getPassword() {
		checkPasswordEncrypt();
		return passwordEncrypt != null ? passwordEncrypt.getDecryptedValue() : null;
	}

	public void setPassword(String password) {
		checkArgument(!Strings.isNullOrEmpty(password));
		this.passwordEncrypt = new PasswordEncrypt(false, password);
		this.encryptedPassword = this.passwordEncrypt.getEncryptedValue();
	}

	private void checkPasswordEncrypt() {
		if (passwordEncrypt == null && !Strings.isNullOrEmpty(encryptedPassword)) {
			passwordEncrypt = new PasswordEncrypt(true, encryptedPassword);
		}
	}

	public static class PersonalAreaLoginQuery extends AbstractLoginQuery<PersonalAreaLogin> {

		private EntityQueryEntityFilter<PersonalAreaLogin, Customer> customer = createEntityFilter(PersonalAreaLogin_.customer);

		public PersonalAreaLoginQuery() {
			super(PersonalAreaLogin.class);
		}

		public EntityQueryEntityFilter<PersonalAreaLogin, Customer> customer() {
			return customer;
		}

	}

	private static final long serialVersionUID = 8694929101561830650L;

}
