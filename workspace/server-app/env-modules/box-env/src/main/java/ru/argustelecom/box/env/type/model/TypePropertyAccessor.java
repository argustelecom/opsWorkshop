package ru.argustelecom.box.env.type.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.text.MessageFormat.format;

import java.util.Collections;
import java.util.List;

import ru.argustelecom.box.env.type.model.lookup.LookupEntry;
import ru.argustelecom.box.env.type.model.properties.LookupProperty;
import ru.argustelecom.system.inf.exception.SystemException;

/**
 * Инкапсулирует метаданные свойства и экземпляр спецификации, в контексте которого необходимо работать со значением
 * этого свойства. Предназначен для связвания свойства с экземпляром спецификации и предоставления простых
 * геттеров/сеттеров для доступа к значениям.
 * 
 * @param <V>
 *            - тип, возвращаемый свойством
 */
public class TypePropertyAccessor<V> {

	private TypeProperty<V> property;
	private TypeInstance<?> instance;
	private List<LookupEntry> possibleLookupValues;
	private boolean privileged;

	protected TypePropertyAccessor(TypeProperty<V> property, TypeInstance<?> instance, boolean privileged) {
		checkConsistency(property, instance);
		this.property = checkNotNull(property);
		this.instance = checkNotNull(instance);
		this.privileged = privileged;
	}

	public TypeProperty<V> getProperty() {
		return property;
	}

	public TypeInstance<?> getInstance() {
		return instance;
	}

	public V getValue() {
		return property.getValue(instance);
	}

	public void setValue(V value) {
		if (privileged) {
			property.setValuePrivileged(instance, value);
		} else {
			property.setValue(instance, value);
		}
	}

	public String getAsString() {
		return property.getAsString(instance);
	}

	public boolean isLocked() {
		return !privileged && property.isValueLocked(instance);
	}

	public List<LookupEntry> getPossibleLookupValues() {
		if (possibleLookupValues == null) {
			if (property instanceof LookupProperty) {
				LookupProperty lkp = (LookupProperty) property;
				possibleLookupValues = lkp.getCategory().getPossibleValues(null);
			} else {
				possibleLookupValues = Collections.emptyList();
			}
		}
		return possibleLookupValues;
	}

	private void checkConsistency(TypeProperty<V> property, TypeInstance<?> instance) {
		if (property == null || instance == null || !instance.getType().hasProperty(property)) {
			throw new SystemException(format("Property {0} is not applicable to an instance {1}", property, instance));
		}
	}
}
