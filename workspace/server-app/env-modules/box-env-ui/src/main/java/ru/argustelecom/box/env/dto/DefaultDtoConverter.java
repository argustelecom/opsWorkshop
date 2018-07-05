package ru.argustelecom.box.env.dto;

import static java.lang.String.format;
import static java.util.regex.Pattern.compile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.persistence.EntityManager;

import org.jboss.logging.Logger;

import ru.argustelecom.system.inf.configuration.ServerRuntimeProperties;
import ru.argustelecom.box.env.dto.nls.DtoMessagesBundle;
import ru.argustelecom.box.env.overall.nls.OverallMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.modelbase.Identifiable;
import ru.argustelecom.system.inf.utils.CDIHelper;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

@FacesConverter(forClass = ConvertibleDto.class)
public class DefaultDtoConverter implements Converter {

	private static final Logger log = Logger.getLogger(DefaultDtoConverter.class);


	private static final Pattern pattern = compile("^.* \\{([a-zA-Z]+-\\d+) translatorClass=([a-zA-Z\\.]+)}$");

	private EntityConverter entityConverter;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null)
			return null;

		Matcher matcher = pattern.matcher(value);
		checkMatches(matcher, value);

		String identifiableStringValue = matcher.group(1);
		String translatorClassValue = matcher.group(2);

		Identifiable identifiable = getEntityConverter().convertToObject(identifiableStringValue);

		return convertToDto(identifiable, translatorClassValue);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object modelValue) {
		return modelValue.toString();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private IdentifiableDto convertToDto(Identifiable identifiable, String translatorClassValue) {
		try {
			Class<?> translatorClass = Class.forName(translatorClassValue);
			DefaultDtoTranslator defaultDtoTr = (DefaultDtoTranslator) CDIHelper.lookupCDIBean(translatorClass);
			return defaultDtoTr.translate(identifiable);
		} catch (ClassNotFoundException e) {
			throw new SystemException(format("Couldn't find translator class for '%s'", translatorClassValue), e);
		}
	}

	private void checkMatches(Matcher matcher, String value) {
		if (!matcher.matches()) {
			log.debug(format("Can't parse string: '%s'", value));
			OverallMessagesBundle overallMessages = LocaleUtils.getMessages(OverallMessagesBundle.class);
			DtoMessagesBundle dtoMessages = LocaleUtils.getMessages(DtoMessagesBundle.class);

			throw new ConverterException(new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					overallMessages.error(),
					dtoMessages.customerShouldBeChosenFromList()
			));
		}
	}

	private EntityConverter getEntityConverter() {
		if (entityConverter == null) {
			EntityManager em = ServerRuntimeProperties.instance().lookupEntityManager();
			entityConverter = new EntityConverter(em);
		}
		return entityConverter;
	}
}