package ru.argustelecom.box.env.type.event.qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import ru.argustelecom.box.env.type.model.Type;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
public @interface MakeUnique {
	Class<? extends Type> typeClass();

	UniqueMode mode();
}
