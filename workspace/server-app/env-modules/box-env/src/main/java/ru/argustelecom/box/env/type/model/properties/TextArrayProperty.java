package ru.argustelecom.box.env.type.model.properties;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.argustelecom.box.env.stl.NamedObjectArrayList;
import ru.argustelecom.box.env.stl.json.JsonHelper;
import ru.argustelecom.box.env.type.model.TypeInstance;
import ru.argustelecom.box.env.type.model.TypeProperty;
import ru.argustelecom.box.env.type.model.TypePropertyHolder;
import ru.argustelecom.box.inf.hibernate.types.StringArrayType;

@Entity
@Access(AccessType.FIELD)
@TypeDef(name = "string-array", typeClass = StringArrayType.class)
public class TextArrayProperty extends TypeProperty<List<String>> {

	private static final long serialVersionUID = 7652876529674361065L;

	@Type(type = "string-array")
	@Column(name = "txtarr_default", nullable = false, columnDefinition = "varchar[]")
	private List<String> defaultValue = new ArrayList<>();

	protected TextArrayProperty() {
	}

	protected TextArrayProperty(TypePropertyHolder holder, Long id) {
		super(holder, id);
	}

	@Override
	public boolean isFiltered() {
		// Временно, пока нет фильтра для массива
		return false;
	}

	@Override
	public Class<?> getValueClass() {
		return List.class;
	}

	@Override
	public List<String> getDefaultValue() {
		return defaultValue;
	}

	@Override
	public void setDefaultValue(List<String> defaultValue) {
		this.defaultValue.clear();
		this.defaultValue.addAll(normalize(defaultValue));
	}

	@Override
	public String getDefaultValueAsString() {
		return arrayToString(defaultValue);
	}

	@Override
	protected List<String> extractValue(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName) {
		return new NamedObjectArrayList<>(JsonHelper.STRING_ARRAY.get(propertiesRoot, qualifiedName, emptyList()));
	}

	@Override
	protected String extractValueAsString(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName) {
		return arrayToString(extractValue(context, propertiesRoot, qualifiedName));
	}

	@Override
	protected void putValue(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName,
			List<String> value) {
		checkState(value != null);
		JsonHelper.STRING_ARRAY.set(propertiesRoot, qualifiedName, normalize(value));
	}

	@Override
	protected void putNullValue(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName) {
		propertiesRoot.putArray(qualifiedName);
	}

	private List<String> normalize(List<String> values) {
		if (values == null) {
			return emptyList();
		}
		return values.stream().filter(StringUtils::isNotBlank).collect(toList());
	}

	private String arrayToString(List<String> values) {
		return values != null && !values.isEmpty() ? StringUtils.join(values, ", ") : null;
	}

}
