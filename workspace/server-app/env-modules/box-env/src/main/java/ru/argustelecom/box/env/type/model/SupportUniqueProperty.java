package ru.argustelecom.box.env.type.model;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Указывает, что Type поддерживает уникальные свойства. <br/>
 * Также см. {@link TypeInstanceDescriptor} и {@link ru.argustelecom.box.env.type.BaseTypePropertyUniqueHandler}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SupportUniqueProperty {
}
