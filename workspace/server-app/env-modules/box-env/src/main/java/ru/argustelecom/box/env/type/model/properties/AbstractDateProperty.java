package ru.argustelecom.box.env.type.model.properties;

import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import ru.argustelecom.box.env.datetime.model.DateIntervalValue;
import ru.argustelecom.box.env.type.model.TypeProperty;
import ru.argustelecom.box.env.type.model.TypePropertyHolder;
import ru.argustelecom.box.env.type.nls.TypeMessagesBundle;
import ru.argustelecom.box.inf.modelbase.MetadataUnit;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.chrono.DateUtils;
import ru.argustelecom.system.inf.chrono.TZ;
import ru.argustelecom.system.inf.exception.BusinessException;

@Entity
@Access(AccessType.FIELD)
public abstract class AbstractDateProperty<T> extends TypeProperty<T> {

	private static final long serialVersionUID = 1338056000986135171L;

	@Column(name = "date_pattern", length = 32)
	private String pattern;

	@Transient
	private transient DateTimeFormatter compiledPattern;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected AbstractDateProperty() {
	}

	/**
	 * Конструктор предназначен для инстанцирования свойства его холдером. Не делай этот конструктор публичным. Не делай
	 * других публичных конструкторов. Свойство должны инстанцироваться сугубо холдером или спецификацией (делегирует
	 * холдеру) для обеспецения корректного связывания холдера(спецификации) и свойства.
	 * 
	 * @param holder
	 *            - владелец свойства, часть спецификации
	 * @param id
	 *            - уникальный идентификатор свойства. Получается при помощи генератора инкапсулированного в
	 *            MetadataUnit.generateId()
	 * 
	 * @see TypePropertyHolder#createProperty(Class, String, Long)
	 * @see MetadataUnit#generateId()
	 * @see MetadataUnit#generateId(javax.persistence.EntityManager)
	 */
	protected AbstractDateProperty(TypePropertyHolder holder, Long id) {
		super(holder, id);
	}

	/**
	 * Возвращает шаблон, по которому необходимо форматировать и интерпретировать текущее представление даты/времени на
	 * уровне пользовательского интерфейса.
	 * <p>
	 * Примеры шаблонов представления датовремени:
	 * <ul>
	 * <li>dd.MM.yyyy HH:mm
	 * <li>dd.MM.yyyy HH:mm (ZZ)
	 * <li>dd.MM.yyyy HH:mm:ss.SSS
	 * <li>и т.д.
	 * </ul>
	 * 
	 * Значение шаблона должно быть всегда определено и должно быть всегда корреткным. Если значение шаблона не
	 * определено пользователем при конфигурировании свойста, то будет использовано значение по-умолчанию, равное
	 * {@link DateUtils#DATE_DEFAULT_PATTERN}. Корректность значения определяется путем компиляции форматтера при
	 * попытке изменения шаблона в сеттере этого свойства.
	 * 
	 * @return всегда определенное и валидное значение шаблона датовремени текущего свойства
	 */
	public String getPattern() {
		return pattern != null ? pattern : DateUtils.DATE_DEFAULT_PATTERN;
	}

	/**
	 * Устанавливает новое значение шаблона датовремени. При установке значения выполняется его валидация путем
	 * компиляции форматтера. Если компиляция будет неуспешной, то будет брошено {@link BusinessException}
	 * 
	 * @param pattern
	 *            - шаблон датовремени или null, если необходимо сбросить шаблон в значение по-умолчанию;
	 */
	public void setPattern(String pattern) {
		if (!Objects.equals(this.pattern, pattern)) {
			this.compiledPattern = compilePattern(pattern);
			this.pattern = pattern;
		}
	}

	/**
	 * Возвращает скомпилированный форматтер для строкового представления датовремени. В качестве шаблона для форматтера
	 * используется {@link #getPattern()}. Если шаблон не указано пользователем при конфигурировании свойства, то
	 * используется {@link DateUtils#DATE_DEFAULT_PATTERN}
	 * 
	 * @return форматтер отображаемого пользователю значения, всегда не null
	 */
	protected DateTimeFormatter getCompiledPattern() {
		if (compiledPattern == null) {
			compiledPattern = compilePattern(pattern);
		}
		return compiledPattern;
	}

	/**
	 * Компилирует шаблон. При компилляции выполняется синтаксическая проверка корректности указанного пользователем
	 * шаблона
	 */
	private DateTimeFormatter compilePattern(String pattern) {
		Locale locale = LocaleUtils.getCurrentLocale();
		DateTimeZone dtz = DateTimeZone.forTimeZone(TZ.getUserTimeZone());
		if (pattern != null) {
			try {
				return compilePattern(pattern, locale, dtz);
			} catch (Exception e) {
				TypeMessagesBundle messages = LocaleUtils.getMessages(TypeMessagesBundle.class);
				throw messages.dateValuePatternCompileException(pattern, e);
			}
		}
		return compilePattern(DateUtils.DATE_DEFAULT_PATTERN, locale, dtz);
	}

	/**
	 * Компилирует шаблон с учетом указанных параметров локали и временной зоны
	 */
	private DateTimeFormatter compilePattern(String pattern, Locale locale, DateTimeZone dtz) {
		return DateTimeFormat.forPattern(pattern).withZone(dtz).withLocale(locale);
	}

	/**
	 * Форматирует значение датовремени с использованием {@link #getCompiledPattern()}
	 * 
	 * @param value
	 *            - датовремя
	 * 
	 * @return строковое представление датовремени
	 */
	protected String formatDateValueWithPattern(Date value) {
		if (value != null) {
			return getCompiledPattern().print(value.getTime());
		}
		return null;
	}

	/**
	 * Форматирует интервал значений датовремени с использованием {@link #getCompiledPattern()}
	 * 
	 * @param intervalValue
	 *            - интервал датовремени
	 * 
	 * @return строковое представление интервала датовремени
	 */
	protected String formatIntervalValueWithPattern(DateIntervalValue intervalValue) {
		if (intervalValue != null) {
			long startInstant = intervalValue.getStartDate().getTime();
			long endInstant = intervalValue.getStartDate().getTime();

			return getCompiledPattern().print(startInstant) + ".." + getCompiledPattern().print(endInstant);
		}
		return null;
	}
}
