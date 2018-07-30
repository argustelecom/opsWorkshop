package ru.argustelecom.ops.env.stl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.LinkedList;
import java.util.Locale;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import ru.argustelecom.ops.inf.modelbase.SingleValueObject;
import ru.argustelecom.ops.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

/**
 * Стандартный системный тип для представления любого номера телефона. Может быть создан по номерам с любым
 * форматированием в одном из двух принципиальных режимов: с явным или неявным указанием country calling code (смотри
 * конструкторы и фабричные методы).
 *
 * <p>
 * Этот тип может использоваться как для локальной работы, так и для единообразного представления номера телефона в
 * персистентном хранилище.
 *
 */
@Embeddable
@Access(AccessType.FIELD)
public class PhoneNumber extends SingleValueObject<PhoneNumber, String> {

	@Column(name = "phone_number", nullable = false)
	private String phoneNumber;

	/**
	 * Конструктор для JPA
	 */
	protected PhoneNumber() {
	}

	/**
	 * Создает экземпляр PhoneNumber по номеру телефона, указанному в одном из международных форматов:
	 * <ul>
	 * <li>E164: +78123333660</li>
	 * <li>INTERNATIONAL: +7 812 333-36-60</li>
	 * <li>RFC3966: tel:+7-812-333-36-60</li>
	 * </ul>
	 *
	 * <p>
	 * Объективно, не имеет значения, как будет указан номер телефона. Форматирование здесь абсолютно не важно, т.е.
	 * номер будет успешно разобран при указании следующих вариантов:
	 * <ul>
	 * <li>+7(812)3333660</li>
	 * <li>+7 812 333 36 60</li>
	 * <li>+7(812)333-36-60</li>
	 * <li>+7(812)333-3660</li>
	 * <li>и т.д.</li>
	 * </ul>
	 *
	 * <p>
	 * Все эти варианты форматирования будут успешно разобраны и нормализованы в формат E164. Единственным значимым и
	 * обязательным параметром номера для текущего варианта создания является country calling code, например +7 для
	 * России или +1 для США.
	 *
	 * @param internationalFormattedNumber
	 *            - номер телефона, содержащий country calling code
	 */
	protected PhoneNumber(String internationalFormattedNumber) {
		checkArgument(!isNullOrEmpty(internationalFormattedNumber), "Phone number cannot be an empty string");

		delegate = parse(internationalFormattedNumber);
		phoneNumber = util().format(delegate, com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.E164);
	}

	/**
	 * Создает экземпляр PhoneNumber по номеру телефона, указанному в национальном формате:
	 * <ul>
	 * <li>8(812)3333660</li>
	 * <li>8(812)333-36-60</li>
	 * <li>8(812)333-3660</li>
	 * <li>8 812 333 36 60</li>
	 * <li>и т.д.</li>
	 * </ul>
	 *
	 * <p>
	 * Т.к. в в общем случае для национального номера телефона невозможно определить country calling code, то необходимо
	 * указать дополнительный параметр - код региона по умолчанию, например, RU для России или US для США.
	 *
	 * <p>
	 * Если номер телефона указан в международном формате или содержит country calling code, то код региона будет
	 * проигнорирован.
	 *
	 * @param nationalFormattedNumber
	 *            - номер телефона в национальном формате
	 * @param regionCode
	 *            - код региона
	 */
	protected PhoneNumber(String nationalFormattedNumber, String regionCode) {
		checkArgument(!isNullOrEmpty(nationalFormattedNumber), "Phone number cannot be an empty string");
		checkArgument(!isNullOrEmpty(regionCode), "Region code cannot be an empty string");

		delegate = parse(nationalFormattedNumber, regionCode);
		phoneNumber = util().format(delegate, com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.E164);
	}

	/**
	 * Создает экземпляр PhoneNumber по номеру телефона, указанному в национальном формате.
	 *
	 * <p>
	 * Т.к. в этом варианте невозможно определить country calling code, то необходимо указать дополнительный параметр -
	 * локаль, которая содержит код страны (Locale.getCountry()).
	 *
	 * <p>
	 * Если номер телефона указан в международном формате или содержит country calling code, то локаль с кодом страны
	 * будет проигнорирована.
	 *
	 * @param nationalFormattedNumber
	 *            - номер телефона в национальном формате
	 * @param locale
	 *            - локаль, содержащая код страны
	 */
	protected PhoneNumber(String nationalFormattedNumber, Locale locale) {
		checkArgument(!isNullOrEmpty(nationalFormattedNumber), "Phone number cannot be an empty string");
		checkArgument(locale != null, "The locale cannot be empty");
		checkArgument(!isNullOrEmpty(locale.getCountry()), "The locale must contain the country code");

		delegate = parse(nationalFormattedNumber, locale.getCountry());
		phoneNumber = util().format(delegate, com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.E164);
	}

	/**
	 * Создает экземпляр PhoneNumber по номеру телефона, указанному в национальном формате.
	 *
	 * <p>
	 * Т.к. в этом варианте невозможно определить country calling code, то необходимо его указать явно.
	 *
	 * <p>
	 * Если номер телефона указан в международном формате или содержит country calling code, то явно указанный в
	 * отдельном параметре country calling code будет проигнорирован.
	 *
	 * @param nationalFormattedNumber
	 *            - номер телефона в национальном формате
	 * @param countryCallingCode
	 *            - код страны, например, 7 для России или 1 для США
	 */
	protected PhoneNumber(String nationalFormattedNumber, int countryCallingCode) {
		checkArgument(!isNullOrEmpty(nationalFormattedNumber), "Phone number cannot be an empty string");
		checkArgument(countryCallingCode > 0, "The country calling code must be greater than zero");

		String regionCode = util().getRegionCodeForCountryCode(countryCallingCode);
		delegate = parse(nationalFormattedNumber, regionCode);
		phoneNumber = util().format(delegate, com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.E164);
	}

	/**
	 * Специальный конструктор для создания экземпляра по имеющемуся делегату, например, при парсинге номеров из тектса
	 * или для клонирования
	 *
	 * @param delegate
	 *
	 * @see PhoneNumber#extractAll(CharSequence, String)
	 */
	private PhoneNumber(com.google.i18n.phonenumbers.Phonenumber.PhoneNumber delegate) {
		checkArgument(delegate != null);

		this.delegate = delegate;
		this.phoneNumber = util().format(delegate, com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.E164);
	}

	/**
	 * Создает экземпляр PhoneNumber. см. {@link PhoneNumber#PhoneNumber(String)}
	 */
	public static PhoneNumber create(String internationalFormattedNumber) {
		return new PhoneNumber(internationalFormattedNumber);
	}

	/**
	 * Создает экземпляр PhoneNumber. см. {@link PhoneNumber#PhoneNumber(String, String)}
	 */
	public static PhoneNumber create(String nationalFormattedNumber, String regionCode) {
		return new PhoneNumber(nationalFormattedNumber, regionCode);
	}

	/**
	 * Создает экземпляр PhoneNumber. см. {@link PhoneNumber#PhoneNumber(String, Locale)}
	 */
	public static PhoneNumber create(String nationalFormattedNumber, Locale locale) {
		return new PhoneNumber(nationalFormattedNumber, locale);
	}

	/**
	 * Создает экземпляр PhoneNumber. см. {@link PhoneNumber#PhoneNumber(String, int)}
	 */
	public static PhoneNumber create(String nationalFormattedNumber, int countryCallingCode) {
		return new PhoneNumber(nationalFormattedNumber, countryCallingCode);
	}

	/**
	 * Извлекает из произвольного текста все номера телефонов, указанных в произвольном варианте форматирования,
	 * нормализует их до стандартного представления в формате E164, а также запоминает исходный текст и стартовую и
	 * конечную позицию номера в тексте.
	 *
	 * <p>
	 * Предназначен для поддержки функций автоматического преобразования номеров телефонов в тексте, например,
	 * комментариев, в формат ссылки tel:+78123333660 для выполнения звонка кликом мыши.
	 *
	 * @param text
	 *            - произвольный текст, в котором нужно найти все возможные номера телефонов
	 *
	 * @param expectingRegionCode
	 *            - ожидаемый код региона, в пределах которого будет валиден найденный номер телефона. Используется
	 *            только в том случае, если в самом номере телефона не указан country calling code
	 *
	 * @return итерируемую коллекцию {@link PhoneNumberEntry вхождений} номеров телефонов
	 */
	public static Iterable<PhoneNumberEntry> extractAll(CharSequence text, String expectingRegionCode) {
		LinkedList<PhoneNumberEntry> result = new LinkedList<>();

		util().findNumbers(text, expectingRegionCode).forEach(match -> {
			//@formatter:off
			result.add(new PhoneNumberEntry(
				new PhoneNumber(match.number()),
				match.rawString(),
				match.start(),
				match.end()
			));
			//@formatter:on
		});

		return result;
	}

	/**
	 * Возвращает хранимое значение текущего номера телефона, как оно представлено в персистентном хранилище и внутри
	 * этого класса. Всегда <tt>not null</tt>. Совпадает с форматом E164.
	 *
	 * @return значение текущего номера телефона
	 */
	@Override
	public String value() {
		return phoneNumber;
	}

	@Override
	public PhoneNumber clone() {
		return new PhoneNumber(this.delegate());
	}

	/**
	 * Представляет текущий номер телефона в формате E164, например +78123333660
	 *
	 * @return текущий номер в формате E164
	 */
	public String formatE164() {
		return util().format(delegate(), com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.E164);
	}

	/**
	 * Представляет текущий номер телефона в международном формате, например +7 812 333-36-60
	 *
	 * @return текущий номер в международном формате
	 */
	public String formatInternational() {
		return util().format(delegate(), com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
	}

	/**
	 * Представляет текущий номер телефона в национальном формате, например 8 (812) 333-36-60
	 *
	 * @return текущий номер в национальном формате
	 */
	public String formatNational() {
		return util().format(delegate(), com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
	}

	/**
	 * Представляет текущий номер телефона в формате RCF3966, например tel:+7-812-333-36-60
	 *
	 * @return текущий номер в формате RCF3966
	 */
	public String formatRCF3966() {
		return util().format(delegate(), com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.RFC3966);
	}

	/**
	 * Возвращает код страны (country calling code) текущего номера телефона. Например 7 для России
	 *
	 * @return код страны
	 */
	public int countryCode() {
		return delegate().getCountryCode();
	}

	/**
	 * Возвращает код региона текущего номера телефона. Например RU для России
	 *
	 * @return код региона
	 */
	public String regionCode() {
		return util().getRegionCodeForNumber(delegate());
	}

	/**
	 * Возвращает тип номера телефона, например, "мобильный" или "телефон фиксированной связи", или "бесплатная линия" и
	 * т.д.
	 *
	 * @return тип номер телефона
	 *
	 * @see PhoneNumberType
	 */
	public PhoneNumberType numberType() {
		if (numberType == null) {
			numberType = PhoneNumberType.identify(util().getNumberType(delegate()));
		}
		return numberType;
	}

	@Transient
	private transient PhoneNumberType numberType;

	/**
	 * Возвращает true, если текущий номер телефона принадлежит категории "фиксированной связи"
	 *
	 * @return
	 */
	public boolean isFixedLine() {
		return numberType() == PhoneNumberType.FIXED_LINE || numberType() == PhoneNumberType.FIXED_LINE_OR_MOBILE;
	}

	/**
	 * Возвращает true, если текущий номер телефона принадлежит категории "мобильной связи"
	 *
	 * @return
	 */
	public boolean isMobile() {
		return numberType() == PhoneNumberType.MOBILE || numberType() == PhoneNumberType.FIXED_LINE_OR_MOBILE;
	}

	/**
	 * Возвращает true, если текущий номер телефона принадлежит категории "бесплатная/горячая линия"
	 *
	 * @return
	 */
	public boolean isTollFree() {
		return numberType() == PhoneNumberType.TOLL_FREE;
	}

	/**
	 * Возвращает true, если текущий номер телефона принадлежит категории "премиальных номеров"
	 *
	 * @return
	 */
	public boolean isPremiumRate() {
		return numberType() == PhoneNumberType.PREMIUM_RATE;
	}

	/**
	 * Возвращает true, если текущий номер телефона "VoIP"
	 *
	 * @return
	 */
	public boolean isVoIP() {
		return numberType() == PhoneNumberType.VOIP;
	}

	@Transient
	private transient com.google.i18n.phonenumbers.Phonenumber.PhoneNumber delegate;

	private com.google.i18n.phonenumbers.Phonenumber.PhoneNumber delegate() {
		if (delegate == null) {
			delegate = parse(phoneNumber);
		}
		return delegate;
	}

	private static com.google.i18n.phonenumbers.PhoneNumberUtil util() {
		return com.google.i18n.phonenumbers.PhoneNumberUtil.getInstance();
	}

	private static com.google.i18n.phonenumbers.Phonenumber.PhoneNumber parse(String numberToParse) {
		return parse(numberToParse, null);
	}

	private static com.google.i18n.phonenumbers.Phonenumber.PhoneNumber parse(String numberToParse, String regionCode) {
		try {
			return util().parse(numberToParse, regionCode);
		} catch (com.google.i18n.phonenumbers.NumberParseException e) {
			throw new SystemException(LocaleUtils.format("Unable to parse phone number {0}", numberToParse), e);
		}
	}

	private static final long serialVersionUID = -2964693768527715523L;
}
