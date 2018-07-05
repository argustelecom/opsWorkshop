package ru.argustelecom.box.env.components;

import static java.lang.String.format;
import static java.util.regex.Pattern.compile;
import static org.apache.commons.lang.StringUtils.EMPTY;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import org.apache.commons.lang.StringUtils;
import org.jboss.logging.Logger;

import ru.argustelecom.box.env.address.AddressQueryResult;

@FacesConverter(value = "addressConverter")
public class AddressConverter implements Converter {

	private static final Logger log = Logger.getLogger(AddressConverter.class);

	private static final Pattern PATTERN = compile("^([a-zA-Z]+) \\[id=(\\d+), displayName=(.*)]$");

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}

		Matcher matcher = PATTERN.matcher(value);
		checkMatches(matcher, value);

		String locationLevel = matcher.group(1);
		Long id = Long.valueOf(matcher.group(2));
		String fullName = matcher.group(3);

		return new AddressQueryResult(id, locationLevel, fullName);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		return Optional.ofNullable(value.toString()).orElse(EMPTY);
	}

	private void checkMatches(Matcher matcher, String value) {
		if (!matcher.matches()) {
			log.debug(format("Can't parse string: '%s'", value));
			throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка валидации",
					"Выберете один из предлагаемых адресов"));
		}
	}

}