package ru.argustelecom.box.env.stl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import ru.argustelecom.box.inf.validator.EmailValidator;

import ru.argustelecom.box.inf.modelbase.SingleValueObject;

/**
 * Стандартный системный тип для представления адреса электронной почты. Гарантирует, что этот адрес сформирован
 * корректно в соответствии с требованиями к email адресам.
 * 
 * <p>
 * Этот тип может использоваться как для локальной работы, так и для единообразного представления адреса электроной
 * почты в персистентном хранилище.
 * 
 */
@Embeddable
@Access(AccessType.FIELD)
public class EmailAddress extends SingleValueObject<EmailAddress, String> {

	@Column(name = "email_address", nullable = false)
	private String emailAddress;

	/**
	 * Конструктор для JPA
	 */
	public EmailAddress() {
	}

	/**
	 * Создает экземпляр адреса электронной почты по указанному строковому представлению.
	 * 
	 * @param emailAddress
	 *            - корректное строковое представление адреса электронной почты
	 */
	protected EmailAddress(String emailAddress) {
		checkArgument(!isNullOrEmpty(emailAddress), "Email address cannot be an empty string");
		checkArgument(EmailValidator.validate(emailAddress, true), "Invalid email address: %s", emailAddress);

		this.emailAddress = emailAddress;
	}

	/**
	 * Специальный конструктор для создания экземпляра по имеющемуся адресу почты, без выполнения валидации.
	 * Используется для поддержания операции клонирования
	 * 
	 * @param template
	 * 
	 * @see EmailAddress#clone()
	 */
	private EmailAddress(EmailAddress template) {
		checkArgument(template != null);
		this.emailAddress = template.value();
	}

	/**
	 * Создает экземпляр EmailAddress. см. {@link EmailAddress#EmailAddress(String)}
	 */
	public static EmailAddress create(String emailAddress) {
		return new EmailAddress(emailAddress);
	}

	/**
	 * Возвращает хранимое строковое представление адреса электронной почты, как оно представлено в персистентном
	 * хранилище и внутри этого класса. Всегда <tt>not null</tt>.
	 * 
	 * @return строковое представление адреса электронной почты
	 */
	@Override
	public String value() {
		return emailAddress;
	}

	@Override
	public EmailAddress clone() {
		return new EmailAddress(this);
	}

	private static final long serialVersionUID = 1687161554399408668L;
}
