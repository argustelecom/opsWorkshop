package ru.argustelecom.box.env.filter.model;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.proxy.HibernateProxy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.filter.FilterParamMapper;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.exception.SystemException;

@Getter
@Setter
@JsonIgnoreProperties(value = { "value" })
public class FilterParam implements Serializable {

	private static final long serialVersionUID = -280971987645287861L;

	private String name;
	private String valueAsString;
	private String valueClassName;

	public static FilterParam create(String name, Object value) {
		checkArgument(StringUtils.isNotBlank(name));
		checkArgument(value != null);
		
		FilterParam filter = new FilterParam(name);
		filter.setValue(value);
		checkArgument(FilterParamMapper.canSerializeFilterParamValue(filter.getValueClassName()));
		return filter;
	}

	private FilterParam() {
		super();
	}

	private FilterParam(String name) {
		this();
		this.name = name;
	}

	public Object getValue() {
		try {
			Class<?> valueClass = Class.forName(valueClassName);
			return FilterParamMapper.deserializeFilterParamValue(valueClass, valueAsString);
		} catch (ClassNotFoundException e) {
			throw new SystemException("Unable to get filter class", e);
		}
	}

	private void setValue(Object value) {
		if (value instanceof HibernateProxy) {
			value = EntityManagerUtils.initializeAndUnproxy(value);
		}
		valueClassName = value.getClass().getName();
		valueAsString = FilterParamMapper.serializeFilterParamValue(value);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + name.hashCode();
		result = prime * result + valueAsString.hashCode();
		result = prime * result + valueClassName.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FilterParam other = (FilterParam) obj;
		if (!name.equals(other.name))
			return false;
		if (!valueAsString.equals(other.valueAsString))
			return false;
		if (!valueClassName.equals(other.valueClassName))
			return false;
		return true;
	}

}
