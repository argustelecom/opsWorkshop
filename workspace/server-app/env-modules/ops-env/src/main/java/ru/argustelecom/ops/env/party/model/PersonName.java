package ru.argustelecom.ops.env.party.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.google.common.base.Strings;

import lombok.EqualsAndHashCode;

@Embeddable
@Access(AccessType.FIELD)
@EqualsAndHashCode(of = { "prefix", "firstName", "secondName", "lastName", "suffix" })
public class PersonName implements Serializable, Comparable<PersonName>, Cloneable {

	@Column(length = 16)
	private String prefix;

	@Column(length = 64, nullable = false)
	private String firstName;

	@Column(length = 64)
	private String secondName;

	@Column(length = 64, nullable = false)
	private String lastName;

	@Column(length = 16)
	private String suffix;

	/**
	 * Конструктор для JPA
	 */
	protected PersonName() {
	}

	/**
	 * Создает сокращенный вариант имени персоны. Используются только обязательные "имя" и "фамилия"
	 * 
	 * @param firstName
	 *            - имя персоны, не может быть <tt>null<tt>
	 * @param lastName
	 *            - фамилия персоны, не может быть <tt>null<tt>
	 */
	protected PersonName(String firstName, String lastName) {
		checkArgument(!Strings.isNullOrEmpty(firstName), "Person First Name is required");
		checkArgument(!Strings.isNullOrEmpty(lastName), "Person Last Name is required");
		this.firstName = firstName;
		this.lastName = lastName;
	}

	/**
	 * Создает более полный варинат имени персоны. Исполюзуются параметры "имя", "отчество", "фамилия", однако
	 * отсутствуют обращения и регалии: префикс и суффикс имени
	 * 
	 * @param firstName
	 *            - имя персоны, не может быть <tt>null<tt>
	 * @param secondName
	 *            - отчество персоны, может быть <tt>null<tt>
	 * @param lastName
	 *            - фамилия персоны, не может быть <tt>null<tt>
	 */
	protected PersonName(String firstName, String secondName, String lastName) {
		this(firstName, lastName);
		this.secondName = secondName;
	}

	/**
	 * Создает самый полный вариант имени персоны. Кроме обязательных параметров "имя", "отчество", "фамилия"
	 * указываются также обращение и регалии: префикс и суффикс имени. Только для наиболее полных имен имеется
	 * возможность использовать все возможности по формированию различных вариантов имен. Рекомендуется предпочитать
	 * этот вариант инстанцирования имени персоны.
	 * 
	 * @param prefix
	 *            - префикс имени персоны, например, "господин" или "мистер" или "сэр". Может быть <tt>null<tt>
	 * @param firstName
	 *            - имя персоны, не может быть <tt>null<tt>
	 * @param secondName
	 *            - отчество персоны, может быть <tt>null<tt>
	 * @param lastName
	 *            - фамилия персоны, не может быть <tt>null<tt>
	 * @param suffix
	 *            - суффикс имени персоны. Обычно используется для указания регалий, например, "к.т.н." или "профессор"
	 */
	protected PersonName(String prefix, String firstName, String secondName, String lastName, String suffix) {
		this(firstName, lastName);
		this.secondName = secondName;
		this.prefix = prefix;
		this.suffix = suffix;
	}

	/**
	 * Специальный конструктор для создания экземпляра по имеющемуся имени персоны, без выполнения валидации.
	 * Используется для поддержания операции клонирования
	 * 
	 * @param template
	 * 
	 * @see PersonName#clone()
	 */
	protected PersonName(PersonName template) {
		checkArgument(template != null);
		this.prefix = template.prefix();
		this.firstName = template.firstName();
		this.secondName = template.secondName();
		this.lastName = template.lastName();
		this.suffix = template.suffix();
	}

	/**
	 * Создает экземпляр PersonName. см. {@link PersonName#PersonName(String, String)}
	 */
	public static PersonName of(String firstName, String lastName) {
		return new PersonName(firstName, lastName);
	}

	/**
	 * Создает экземпляр PersonName. см. {@link PersonName#PersonName(String, String, String)}
	 */
	public static PersonName of(String firstName, String secondName, String lastName) {
		return new PersonName(firstName, secondName, lastName);
	}

	/**
	 * Создает экземпляр PersonName. см. {@link PersonName#PersonName(String, String, String, String, String)}
	 */
	public static PersonName of(String prefix, String firstName, String secondName, String lastName, String suffix) {
		return new PersonName(prefix, firstName, secondName, lastName, suffix);
	}

	/**
	 * Возвращает префикс имени, например, "господин" или "мистер" или "сэр" и т.д.
	 * 
	 * @return префикс или <tt>null<tt>, если он не определен
	 */
	public String prefix() {
		return prefix;
	}

	/**
	 * Возвращает имя персоны, например, "Василий"
	 * 
	 * @return имя, всегда не <tt>null<tt>
	 */
	public String firstName() {
		return firstName;
	}

	/**
	 * Возвращает отчество персоны, например, "Иванович"
	 * 
	 * @return отчество или <tt>null<tt>, если оно не определено
	 */
	public String secondName() {
		return secondName;
	}

	/**
	 * Возвращает фамилию персоны, например, "Тёркин"
	 * 
	 * @return фамилию, всегда не <tt>null<tt>
	 */
	public String lastName() {
		return lastName;
	}

	/**
	 * Возвращает суффикс имени персоны, обычно используемый для указания регалий, например, "к.т.н." или "профессор"
	 * 
	 * @return суффикс или <tt>null<tt>, если он не определен
	 */
	public String suffix() {
		return suffix;
	}

	/**
	 * Возвращает новый экземпляр имени персоны с указанным префиксом. Вариант использования
	 * 
	 * <code><pre>
	 * PersonName name = PersonName.of("Василий", "Тёркин");
	 * log.debug(name.fullName()); // Василий Теркин
	 * log.debug(name.withPrefix("господин").fullName()); // господин Василий Теркин
	 * </pre></code>
	 * 
	 * @param prefix
	 *            - новый префикс
	 * 
	 * @return
	 */
	public PersonName withPrefix(String prefix) {
		if (Objects.equals(this.prefix, prefix)) {
			return this;
		}

		return new PersonName(prefix, this.firstName, this.secondName, this.lastName, this.suffix);
	}

	/**
	 * Возвращает новый экземпляр имени персоны с указанным суффиксом. Вариант использования
	 * 
	 * <code><pre>
	 * PersonName name = PersonName.of("Василий", "Тёркин");
	 * log.debug(name.fullName()); // Василий Теркин
	 * log.debug(name.withSuffix("к.т.н").fullName()); // Василий Теркин, к.т.н
	 * </pre></code>
	 * 
	 * @param suffix
	 *            - новый суфикс
	 * 
	 * @return
	 */
	public PersonName withSuffix(String suffix) {
		if (Objects.equals(this.suffix, suffix)) {
			return this;
		}

		return new PersonName(this.prefix, this.firstName, this.secondName, this.lastName, suffix);
	}

	/**
	 * Возвращает новый экземпляр имени персоны без префикса. Вариант использования
	 * 
	 * <code><pre>
	 * PersonName name = PersonName.of("господин", "Василий", null, "Тёркин", "к.т.н.");
	 * log.debug(name.fullName()); // господин Василий Теркин, к.т.н.
	 * log.debug(name.withoutPrefix().fullName()); // Василий Теркин, к.т.н.
	 * </pre></code>
	 * 
	 * @return
	 */
	public PersonName withoutPrefix() {
		if (this.prefix == null) {
			return this;
		}

		return new PersonName(null, this.firstName, this.secondName, this.lastName, this.suffix);
	}

	/**
	 * Возвращает новый экземпляр имени персоны без суффикса. Вариант использования
	 * 
	 * <code><pre>
	 * PersonName name = PersonName.of("господин", "Василий", null, "Тёркин", "к.т.н.");
	 * log.debug(name.fullName()); // господин Василий Теркин, к.т.н.
	 * log.debug(name.withoutSuffix().fullName()); // господин Василий Теркин
	 * </pre></code>
	 * 
	 * @return
	 */
	public PersonName withoutSuffix() {
		if (this.suffix == null) {
			return this;
		}

		return new PersonName(this.prefix, this.firstName, this.secondName, this.lastName, null);
	}

	/**
	 * Формирует официальное обращение к персоне. Например, "Василий Иванович" (если определено отчество) или "Василий"
	 * (если отчество не определено).
	 * 
	 * <p>
	 * Больше вариантов работы метода приведено в демонстрационном тесте PersonNameTest
	 * 
	 * @return официальное обращение
	 */
	public String officialAppeal() {
		StringBuilder nameBuilder = new StringBuilder();

		checkState(!Strings.isNullOrEmpty(firstName));
		appendCapitalized(nameBuilder, firstName);

		if (!Strings.isNullOrEmpty(secondName)) {
			nameBuilder.append(" ");
			appendCapitalized(nameBuilder, secondName);
		}

		return nameBuilder.toString();
	}

	/**
	 * Формирует сокращенное имя с инициалами. Например, "В.И. Тёркин" (если определено отчество) или "В. Тёркин" (если
	 * отчество не определено)
	 * 
	 * <p>
	 * Больше вариантов работы метода приведено в демонстрационном тесте PersonNameTest
	 * 
	 * @return сокращенное имя с инициалами
	 */
	public String shortInitials() {
		return shortInitials(false);
	}

	/**
	 * Формирует сокращенное имя с инициалами. Если указан флаг lastNameFirst, то фамилия будет поставлена на первое
	 * место. Например "Тёркин В.И." (если определено отчество) или "Тёркин В." (если отчество не определено)
	 * 
	 * <p>
	 * Больше вариантов работы метода приведено в демонстрационном тесте PersonNameTest
	 * 
	 * @param lastNameFirst
	 *            - если <tt>true</tt>, то фамилия будет поставлена на первое место
	 * 
	 * @return сокращенное имя с инициалами
	 */
	public String shortInitials(boolean lastNameFirst) {
		StringBuilder nameBuilder = new StringBuilder();

		if (lastNameFirst) {
			checkState(!Strings.isNullOrEmpty(lastName));
			appendCapitalized(nameBuilder, lastName);
			nameBuilder.append(" ");
		}

		checkState(!Strings.isNullOrEmpty(firstName));
		appendInitial(nameBuilder, firstName);

		if (!Strings.isNullOrEmpty(secondName)) {
			appendInitial(nameBuilder, secondName);
		}

		if (!lastNameFirst) {
			checkState(!Strings.isNullOrEmpty(lastName));
			nameBuilder.append(" ");
			appendCapitalized(nameBuilder, lastName);
		}

		return nameBuilder.toString();
	}

	/**
	 * Формирует сокращенное имя. Например, "Василий Иванович Тёркин" (если определено отчество) или "Василий Тёркин"
	 * (если отчество не определено)
	 * 
	 * <p>
	 * Больше вариантов работы метода приведено в демонстрационном тесте PersonNameTest
	 * 
	 * @return сокращенное имя
	 */
	public String shortName() {
		return shortName(false);
	}

	/**
	 * Формирует сокращенное имя. Если указан флаг lastNameFirst, то фамилия будет поставлена на первое место. Например
	 * "Тёркин Василий Иванович" (если определено отчество) или "Тёркин Василий" (если отчество не определено)
	 * 
	 * <p>
	 * Больше вариантов работы метода приведено в демонстрационном тесте PersonNameTest
	 * 
	 * @param lastNameFirst
	 *            - если <tt>true</tt>, то фамилия будет поставлена на первое место
	 * 
	 * @return сокращенное имя
	 */
	public String shortName(boolean lastNameFirst) {
		StringBuilder nameBuilder = new StringBuilder();

		if (lastNameFirst) {
			checkState(!Strings.isNullOrEmpty(lastName));
			appendCapitalized(nameBuilder, lastName);
			nameBuilder.append(" ");
		}

		nameBuilder.append(officialAppeal());

		if (!lastNameFirst) {
			checkState(!Strings.isNullOrEmpty(lastName));
			nameBuilder.append(" ");
			appendCapitalized(nameBuilder, lastName);
		}

		return nameBuilder.toString();

	}

	/**
	 * Формирует полное имя с инициалами. Например:
	 * <ul>
	 * <li>мистер В.И. Тёркин, к.т.н - указано отчество, префикс и суффикс</li>
	 * <li>мистер В.И. Тёркин - указано отчество, префикс</li>
	 * <li>мистер В. Тёркин - указан только префикс</li>
	 * <li>В.И. Тёркин, к.т.н - указано отчество и суффикс</li>
	 * <li>и т.д.</li>
	 * </ul>
	 * 
	 * <p>
	 * Больше вариантов работы метода приведено в демонстрационном тесте PersonNameTest
	 * 
	 * @return полное имя с инициалами
	 */
	public String fullInitials() {
		return fullInitials(false);
	}

	/**
	 * Формирует полное имя с инициалами. Если указан флаг lastNameFirst, то фамилия будет поставлена на первое место.
	 * Например:
	 * <ul>
	 * <li>мистер Тёркин В.И., к.т.н - указано отчество, префикс и суффикс</li>
	 * <li>мистер Тёркин В.И. - указано отчество, префикс</li>
	 * <li>мистер Тёркин В. - указан только префикс</li>
	 * <li>Тёркин В.И., к.т.н - указано отчество и суффикс</li>
	 * <li>и т.д.</li>
	 * </ul>
	 * 
	 * <p>
	 * Больше вариантов работы метода приведено в демонстрационном тесте PersonNameTest
	 * 
	 * @param lastNameFirst
	 *            - если <tt>true</tt>, то фамилия будет поставлена на первое место
	 * 
	 * @return полное имя с инициалами
	 */
	public String fullInitials(boolean lastNameFirst) {
		StringBuilder nameBuilder = new StringBuilder();

		appendPrefix(nameBuilder);
		nameBuilder.append(shortInitials(lastNameFirst));
		appendSuffix(nameBuilder);

		return nameBuilder.toString();
	}

	/**
	 * Формирует полное имя. Например:
	 * <ul>
	 * <li>мистер Василий Иванович Тёркин, к.т.н - указано отчество, префикс и суффикс</li>
	 * <li>мистер Василий Иванович Тёркин - указано отчество, префикс</li>
	 * <li>мистер Василий Тёркин - указан только префикс</li>
	 * <li>Василий Иванович Тёркин, к.т.н - указано отчество и суффикс</li>
	 * <li>и т.д.</li>
	 * </ul>
	 * 
	 * <p>
	 * Больше вариантов работы метода приведено в демонстрационном тесте PersonNameTest
	 * 
	 * @return полное имя с инициалами
	 */
	public String fullName() {
		return fullName(false);
	}

	/**
	 * Формирует полное имя. Если указан флаг lastNameFirst, то фамилия будет поставлена на первое место. Например:
	 * <ul>
	 * <li>мистер Тёркин Василий Иванович, к.т.н - указано отчество, префикс и суффикс</li>
	 * <li>мистер Тёркин Василий Иванович - указано отчество, префикс</li>
	 * <li>мистер Тёркин Василий - указан только префикс</li>
	 * <li>Тёркин Василий Иванович, к.т.н - указано отчество и суффикс</li>
	 * <li>и т.д.</li>
	 * </ul>
	 * 
	 * <p>
	 * Больше вариантов работы метода приведено в демонстрационном тесте PersonNameTest
	 * 
	 * @param lastNameFirst
	 *            - если <tt>true</tt>, то фамилия будет поставлена на первое место
	 * 
	 * @return полное имя с инициалами
	 */
	public String fullName(boolean lastNameFirst) {
		StringBuilder nameBuilder = new StringBuilder();

		appendPrefix(nameBuilder);
		nameBuilder.append(shortName(lastNameFirst));
		appendSuffix(nameBuilder);

		return nameBuilder.toString();
	}

	private StringBuilder appendCapitalized(StringBuilder buider, String name) {
		buider.append(Character.toUpperCase(name.charAt(0)));
		if (name.length() > 1) {
			buider.append(name.substring(1).toLowerCase());
		}
		return buider;
	}

	private StringBuilder appendInitial(StringBuilder buider, String name) {
		buider.append(Character.toUpperCase(name.charAt(0))).append('.');
		return buider;
	}

	private StringBuilder appendPrefix(StringBuilder builder) {
		if (!Strings.isNullOrEmpty(prefix)) {
			builder.append(prefix).append(" ");
		}
		return builder;
	}

	private StringBuilder appendSuffix(StringBuilder builder) {
		if (!Strings.isNullOrEmpty(suffix)) {
			builder.append(", ").append(suffix);
		}
		return builder;
	}

	@Override
	public int compareTo(PersonName that) {
		if (Objects.equals(this, that)) {
			return 0;
		}
		return this.fullName().compareToIgnoreCase(that.fullName());
	}

	@Override
	public String toString() {
		return fullName();
	}

	@Override
	public PersonName clone() {
		return new PersonName(this);
	}

	private static final long serialVersionUID = -4387342894008173404L;
}
