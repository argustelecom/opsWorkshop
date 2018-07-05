package ru.argustelecom.box.env.type.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.text.MessageFormat.format;

import java.util.Objects;
import java.util.function.Function;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.box.env.type.model.properties.DateIntervalProperty;
import ru.argustelecom.box.env.type.model.properties.DateIntervalPropertyFilter;
import ru.argustelecom.box.env.type.model.properties.DateProperty;
import ru.argustelecom.box.env.type.model.properties.DatePropertyFilter;
import ru.argustelecom.box.env.type.model.properties.DoubleProperty;
import ru.argustelecom.box.env.type.model.properties.DoublePropertyFilter;
import ru.argustelecom.box.env.type.model.properties.LogicalProperty;
import ru.argustelecom.box.env.type.model.properties.LogicalPropertyFilter;
import ru.argustelecom.box.env.type.model.properties.LongProperty;
import ru.argustelecom.box.env.type.model.properties.LongPropertyFilter;
import ru.argustelecom.box.env.type.model.properties.LookupArrayProperty;
import ru.argustelecom.box.env.type.model.properties.LookupProperty;
import ru.argustelecom.box.env.type.model.properties.LookupPropertyFilter;
import ru.argustelecom.box.env.type.model.properties.MeasuredIntervalProperty;
import ru.argustelecom.box.env.type.model.properties.MeasuredIntervalPropertyFilter;
import ru.argustelecom.box.env.type.model.properties.MeasuredProperty;
import ru.argustelecom.box.env.type.model.properties.MeasuredPropertyFilter;
import ru.argustelecom.box.env.type.model.properties.TextArrayProperty;
import ru.argustelecom.box.env.type.model.properties.TextProperty;
import ru.argustelecom.box.env.type.model.properties.TextPropertyFilter;
import ru.argustelecom.box.env.type.nls.TypeMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

@AllArgsConstructor(access = AccessLevel.MODULE)
public enum TypePropertyRef {

	//@formatter:off

	/**
	 * @see DateProperty
	 */
	DATE(
		DateProperty.class,
		DatePropertyFilter.class,
		TypeMessagesBundle::dateProperty
	),

	/**
	 * @see DateIntervalProperty
	 */
	DATE_INTERVAL(
		DateIntervalProperty.class,
		DateIntervalPropertyFilter.class,
		TypeMessagesBundle::dateIntervalProperty
	),

	/**
	 * @see MeasuredProperty
	 */
	MEASURED(
		MeasuredProperty.class,
		MeasuredPropertyFilter.class,
		TypeMessagesBundle::measuredProperty
	),

	/**
	 * @see MeasuredIntervalProperty
	 */
	MEASURED_INTERVAL(
		MeasuredIntervalProperty.class,
		MeasuredIntervalPropertyFilter.class,
		TypeMessagesBundle::measuredIntervalProperty
	),

	/**
	 * @see LogicalProperty
	 */
	LOGICAL(
		LogicalProperty.class,
		LogicalPropertyFilter.class,
		TypeMessagesBundle::logicalProperty
	),

	/**
	 * @see LongProperty
	 */
	LONG(
		LongProperty.class,
		LongPropertyFilter.class,
		TypeMessagesBundle::longProperty
	),

	/**
	 * @see DoubleProperty
	 */
	DOUBLE(
		DoubleProperty.class,
		DoublePropertyFilter.class,
		TypeMessagesBundle::doubleProperty
	),

	/**
	 * @see LookupProperty
	 */
	LOOKUP(
		LookupProperty.class,
		LookupPropertyFilter.class,
		TypeMessagesBundle::lookupProperty
	),

	/**
	 * @see LookupArrayProperty
	 */
	LOOKUP_ARRAY(
		LookupArrayProperty.class,
		null,
		TypeMessagesBundle::lookupArrayProperty
	),

	/**
	 * @see TextProperty
	 */
	TEXT(
		TextProperty.class,
		TextPropertyFilter.class,
		TypeMessagesBundle::textProperty
	),

	/**
	 * @see TextArrayProperty
	 */
	TEXT_ARRAY(
		TextArrayProperty.class,
		null,
		TypeMessagesBundle::textArrayProperty
	);
	
	//@formatter:on

	@Getter
	private Class<? extends TypeProperty<?>> propertyClass;

	@Getter
	private Class<? extends TypePropertyFilter<?, ?>> filterClass;

	private Function<TypeMessagesBundle, String> nameGetter;

	public String getName() {
		TypeMessagesBundle messages = LocaleUtils.getMessages(TypeMessagesBundle.class);
		return nameGetter.apply(messages);
	}

	public boolean isFilterSupported() {
		return filterClass != null;
	}

	public static <T extends TypeProperty<?>> TypePropertyRef forClass(Class<T> propertyClass) {
		checkNotNull(propertyClass);
		for (TypePropertyRef type : TypePropertyRef.values()) {
			if (Objects.equals(type.getPropertyClass(), propertyClass)) {
				return type;
			}
		}
		throw new SystemException(format("Property class {0} does not supported", propertyClass.getSimpleName()));
	}

}
