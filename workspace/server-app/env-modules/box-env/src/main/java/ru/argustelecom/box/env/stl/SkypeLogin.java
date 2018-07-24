package ru.argustelecom.box.env.stl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.regex.Pattern;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import ru.argustelecom.box.inf.modelbase.SingleValueObject;

/**
 * Стандартный системный тип для представления логина пользователя в скайпе. Гарантирует, что этот логин сформирован
 * корректно в соответствии с требованиями мессенджера скайпа к логинам пользователей.
 *
 * <p>
 * Этот тип может использоваться как для локальной работы, так и для единообразного представления логина пользователя в
 * мессенджере Скайп в персистентном хранилище.
 *
 */
@Embeddable
@Access(AccessType.FIELD)
public class SkypeLogin extends SingleValueObject<SkypeLogin, String> {

	/**
	 * Регулярное выражения для проверки корректности формата логина пользователя в мессенджере Скайп
	 */
	public static final String REGEXP = "[a-zA-Z][a-zA-Z0-9_\\-\\,\\.]{5,31}";

	@Column(name = "skype_login", nullable = false)
	private String skypeLogin;

	/**
	 * Конструктор для JPA
	 */
	protected SkypeLogin() {
	}

	/**
	 * Создает экземпляр логина пользователя в скайпе по указанному строковому представлению. Строковое представление
	 * логина пользователя в скайпе должно удовлетворять следующему формату: содержать только латинске символы, цифры,
	 * знаки подчеркивания, дефисы и точки; начинаться с буквы, иметь длину от шести до тридцати двух символов.
	 *
	 * <p>
	 * При создании логина пользователя в скайп выполняется валидация по указанным выше правилам
	 *
	 * @param skypeLogin
	 *            - корректное строковое представление логина пользователя в skype
	 */
	protected SkypeLogin(String skypeLogin) {
		checkArgument(!isNullOrEmpty(skypeLogin), "Skype login cannot be an empty string");
		checkArgument(PATTERN.matcher(skypeLogin).matches(),
				"Skype login can only contain Latin characters, digits, hyphens and underscores [%s]", skypeLogin);

		this.skypeLogin = skypeLogin;
	}

	/**
	 * Специальный конструктор для создания экземпляра по имеющемуся логину скайпа, без выполнения валидации.
	 * Используется для поддержания операции клонирования
	 *
	 * @param template
	 *
	 * @see SkypeLogin#clone()
	 */
	private SkypeLogin(SkypeLogin template) {
		checkArgument(template != null);
		this.skypeLogin = template.value();
	}

	/**
	 * Создает экземпляр SkypeLogin. см. {@link SkypeLogin#SkypeLogin(String)}
	 */
	public static SkypeLogin create(String skypeLogin) {
		return new SkypeLogin(skypeLogin);
	}

	/**
	 * Возвращает хранимое строковое представление логина пользователя в скайпе, как оно представлено в персистентном
	 * хранилище и внутри этого класса. Всегда <tt>not null</tt>.
	 *
	 * @return строковое представление логина пользователя в скайпе
	 */
	@Override
	public String value() {
		return skypeLogin;
	}

	@Override
	public SkypeLogin clone() {
		return new SkypeLogin(this);
	}

	private static final Pattern PATTERN = Pattern.compile(REGEXP);
	private static final long serialVersionUID = -4833054711425747979L;
}
