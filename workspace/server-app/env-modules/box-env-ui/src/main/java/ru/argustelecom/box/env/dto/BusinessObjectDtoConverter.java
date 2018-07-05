package ru.argustelecom.box.env.dto;

import static java.lang.String.format;
import static java.util.regex.Pattern.compile;
import static ru.argustelecom.system.inf.utils.CDIHelper.lookupCDIBean;

import java.util.Optional;
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

import ru.argustelecom.box.env.overall.nls.OverallMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.configuration.ServerRuntimeProperties;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.modelbase.Identifiable;
import ru.argustelecom.system.inf.modelbase.NamedObject;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

@FacesConverter(forClass = BusinessObjectDto.class)
public class BusinessObjectDtoConverter implements Converter {

	private static final Logger log = Logger.getLogger(DefaultDtoConverter.class);

	private static final Pattern pattern = compile("^([a-zA-Z]+-\\d+)$");

	private EntityConverter entityConverter;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null)
			return null;

		Matcher matcher = pattern.matcher(value);
		checkMatches(matcher, value);

		Identifiable identifiable = getEntityConverter().convertToObject(value);

		return convertToDto(identifiable, BusinessObjectDtoTranslator.class.getName());
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object modelValue) {
		return Optional.ofNullable(modelValue).map(Object::toString).orElse(null);
	}

	@SuppressWarnings("unchecked")
	private BusinessObjectDto convertToDto(Identifiable identifiable, String translatorClassValue) {
		try {
			Class<?> translatorClass = Class.forName(translatorClassValue);
			BusinessObjectDtoTranslator translator = (BusinessObjectDtoTranslator) lookupCDIBean(translatorClass);
			return translator.translate((Identifiable & NamedObject) identifiable);
		} catch (ClassNotFoundException e) {
			throw new SystemException(format("Couldn't find translator class for '%s'", translatorClassValue), e);
		}
	}

	private void checkMatches(Matcher matcher, String value) {
		if (!matcher.matches()) {
			log.debug(format("Can't parse string: '%s'", value));
			OverallMessagesBundle overallMessages = LocaleUtils.getMessages(OverallMessagesBundle.class);

			throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, overallMessages.error(),
					"Для поиска по имени клиента необходимо выбрать значение из предложенных"));
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