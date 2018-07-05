package ru.argustelecom.box.env.converter;

import java.math.BigDecimal;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import com.google.common.base.Strings;

import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.stl.nls.MoneyMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;

@FacesConverter("moneyConverter")
public class MoneyConverter implements Converter {
	private static final int PRECISION = 15;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (Strings.isNullOrEmpty(value)) {
			return null;
		}
		BigDecimal number;
		try {
			if (value.length() > PRECISION) {
				throw new IllegalArgumentException();
			}
			number = new BigDecimal(value.replace(',', '.'));
		} catch (IllegalArgumentException e) {
			MoneyMessagesBundle moneyMessages = LocaleUtils.getMessages(MoneyMessagesBundle.class);
			FacesMessage msg = new FacesMessage(moneyMessages.amountIsNotCorrect());
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ConverterException(msg);
		}
		return new Money(number);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		return value != null ? ((Money) value).getRoundAmount().toString() : null;
	}
}
