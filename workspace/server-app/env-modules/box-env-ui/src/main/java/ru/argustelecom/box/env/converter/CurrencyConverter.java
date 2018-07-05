package ru.argustelecom.box.env.converter;

import java.util.Currency;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.google.common.base.Strings;

@FacesConverter(value = "currencyConverter")
public class CurrencyConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		return Strings.isNullOrEmpty(value) ? null : Currency.getInstance(value);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		return value != null ? ((Currency) value).getCurrencyCode() : null;
	}
}
