package ru.argustelecom.box.inf.nls;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.jboss.logging.Messages;

import com.google.common.base.Strings;

import ru.argustelecom.box.inf.login.EmployeePrincipal;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.modelbase.NamedObject;
import ru.argustelecom.system.inf.utils.ReflectionUtils;

/**
 * Утилитарный класс для работы локаль-специфичных функций с учетом текущей локали пользователя
 */
public final class LocaleUtils {

	private final static Pattern pattern = Pattern.compile("\\{([a-zA-Z]+):([a-zA-Z._]+)}");

	private LocaleUtils() {
	}

	/**
	 * Возвращает текущую локаль авторизованного пользователя. Если в настоящий момент пользователь не авторизован, то
	 * будет возвращена локаль по умолчанию для системы в целом
	 * 
	 * @return локаль, всегда не null
	 */
	public static Locale getCurrentLocale() {
		Locale result = null;
		EmployeePrincipal authorizedUser = EmployeePrincipal.instance();
		if (authorizedUser != null) {
			result = authorizedUser.getLocale();
		}
		return result != null ? result : Locale.getDefault();
	}

	/**
	 * Возвращает строковое наименование указанной локали на языке этой локали. Т.е. для русской локали выведет название
	 * языка "Русский", для английской локали - "English", и т.д.
	 * 
	 * @param locale
	 *            - локаль
	 * 
	 * @return название языка, описанного указанной локалью, на языке этой локали
	 */
	public static String getDisplayName(Locale locale) {
		String displayName = locale.getDisplayLanguage(locale);
		return StringUtils.capitalize(displayName);
	}

	/**
	 * Возвращает локализованное наименование временной зоны. Для локализации будет использована текущая локаль
	 * пользователя
	 * 
	 * @param timeZone
	 *            - временная зона
	 * 
	 * @return локализованное наименование временной зоны
	 */
	public static String getDisplayName(TimeZone timeZone) {
		return timeZone.getDisplayName(getCurrentLocale());
	}

	/**
	 * Форматирует сообщение с учетом текущей локали. Для задания шаблона форматирования используется нотация
	 * MessageFormat. Если среди указанных аргументов будет обнаружен потомок NamedObject, то у этот аргумент будет
	 * подменен на имя объекта
	 * 
	 * @param pattern
	 *            - шаблон для форматирования в нотации MessageFormat
	 * @param args
	 *            - аргументы форматирования
	 * 
	 * @return форматированное сообщение
	 */
	public static String format(String pattern, Object... args) {
		Object[] arguments = null;
		if (args != null) {
			arguments = new Object[args.length];
			for (int i = 0; i < args.length; i++) {
				Object arg = args[i];
				if (arg instanceof NamedObject) {
					arguments[i] = Strings.nullToEmpty(((NamedObject) arg).getObjectName());
				} else {
					arguments[i] = arg;
				}
			}
		}

		MessageFormat formatter = new MessageFormat(pattern, getCurrentLocale());
		return formatter.format(arguments);
	}

	/**
	 * Возвращает бандл с указанным именем для текущей локали пользователя
	 * 
	 * @param bundleName
	 *            - имя бандла
	 * @param callerClass
	 *            - класс, который рассчитывает получить бандл. Нужен для определения ClassLoader, чтобы искать бандлы
	 *            среди ресурсов этого лоадера. Обязателен для корректного поиска бандла
	 * 
	 * @return бандл локализованных ресурсов
	 */
	public static ResourceBundle getBundle(String bundleName, Class<?> callerClass) {
		return ResourceBundle.getBundle(bundleName, getCurrentLocale(), callerClass.getClassLoader());
	}

	/**
	 * Возвращает бандл с указанным именем для текущей локали пользователя
	 * 
	 * @param bundleName
	 *            - имя бандла
	 * @param classLoader
	 *            - загрузчик класса, в ресурсах которого необходимо выполнять поиск соответствующего бандла. Обязателен
	 *            для корректного поиска бандла
	 * 
	 * @return бандл локализованных ресурсов
	 */
	public static ResourceBundle getBundle(String bundleName, ClassLoader classLoader) {
		return ResourceBundle.getBundle(bundleName, getCurrentLocale(), checkNotNull(classLoader));
	}

	/**
	 * Возвращает локализированое значение из банда по ключу
	 *
	 * @param value
	 *            - значение вида {BundleName:name.of.property_a}, где BundleName - имя бандла, name.of.property_a - имя
	 *            свойства, по которому нужно получить локализованное значение
	 * @param classLoader
	 *            - загрузчик класса, в ресурсах которого необходимо выполнять поиск соответствующего бандла. Обязателен
	 *            для корректного поиска бандла
	 * @return значение, полученное по name.of.property_a из BundleName
	 */
	public static String getLocalizedMessage(String value, ClassLoader classLoader) {
		checkNotNull(value);

		Matcher matcher = pattern.matcher(value);

		if (!matcher.matches()) {
			throw new SystemException(
					String.format("Invalid value: %s. Valid form is {BundleName:name.of.property_a}", value));
		}

		String bundleName = matcher.group(1);
		String property = matcher.group(2);

		ResourceBundle bundle = LocaleUtils.getBundle(bundleName, classLoader);
		checkNotNull(bundle);
		checkState(bundle.containsKey(property));

		return bundle.getString(property);
	}

	public static String getLocalizedMessage(String value, Class<?> callerClass) {
		return getLocalizedMessage(value, callerClass.getClassLoader());
	}

	/**
	 * Возвращает реализацию указанного nls-интерфейса ( {@link Messages#getBundle(Class, Locale)}) для текущей локали
	 * пользователя
	 * 
	 * @param messagesInterface
	 *            nls-интерфейс
	 * 
	 * @return реализацию интерфейса для текущей локали пользователя
	 */
	public static <T> T getMessages(Class<T> messagesInterface) {
		return Messages.getBundle(messagesInterface, getCurrentLocale());
	}

	public static <T extends Throwable> T exception(Class<T> exceptionClass, String messageFmt, Object... args) {
		return ReflectionUtils.newInstance(exceptionClass, format(messageFmt, args));
	}

	public static <T extends Throwable> T exception(Class<T> exceptionClass, Throwable cause, String messageFmt,
			Object... args) {
		T instance = ReflectionUtils.newInstance(exceptionClass, format(messageFmt, args));
		instance.initCause(cause);
		return instance;
	}

	public static boolean containsIgnoreCase(String str1, String str2) {
		String notNullStr1 = Strings.nullToEmpty(str1).toLowerCase(getCurrentLocale());
		String notNullStr2 = Strings.nullToEmpty(str2).toLowerCase(getCurrentLocale());

		return notNullStr1.contains(notNullStr2);
	}

}
