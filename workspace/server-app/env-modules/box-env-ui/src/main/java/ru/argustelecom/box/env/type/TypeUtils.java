package ru.argustelecom.box.env.type;

import static lombok.AccessLevel.PRIVATE;

import com.google.common.collect.Sets;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.env.type.model.SupportFiltering;
import ru.argustelecom.box.env.type.model.SupportUniqueProperty;
import ru.argustelecom.box.env.type.model.Type;
import ru.argustelecom.box.env.type.model.TypePropertyRef;

import java.util.Set;

@NoArgsConstructor(access = PRIVATE)
public final class TypeUtils {

	public static boolean supportUniqueProperty(Class<? extends Type> typeClass) {
		return typeClass != null && typeClass.isAnnotationPresent(SupportUniqueProperty.class);
	}

	public static boolean supportFilteringByType(Class<? extends Type> typeClass) {
		return typeClass != null && typeClass.isAnnotationPresent(SupportFiltering.class);
	}

	public static boolean supportFilteringByProp(TypePropertyRef propRef) {
		// @formatter:off
		return propRef != null
			&& propRef != TypePropertyRef.LOGICAL
			&& propRef != TypePropertyRef.DATE_INTERVAL
			&& propRef != TypePropertyRef.MEASURED_INTERVAL
			&& propRef.isFilterSupported();
		// @formatter:on
	}

	public static boolean supportFiltering(Class<? extends Type> typeClass, TypePropertyRef propRef) {
		return supportFilteringByType(typeClass) && supportFilteringByProp(propRef);
	}
}
