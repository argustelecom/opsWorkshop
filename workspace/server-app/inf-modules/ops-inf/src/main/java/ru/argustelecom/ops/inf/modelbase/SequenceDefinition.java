package ru.argustelecom.ops.inf.modelbase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для определения имени секвенции, участвующей в определении ID
 * 
 * @author a.frolov
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD })
public @interface SequenceDefinition {

	String name() default "system.gen_object_id";

}
