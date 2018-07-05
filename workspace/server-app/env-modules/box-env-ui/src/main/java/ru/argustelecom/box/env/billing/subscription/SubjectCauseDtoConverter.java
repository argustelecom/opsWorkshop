package ru.argustelecom.box.env.billing.subscription;

import static java.lang.String.format;
import static java.util.regex.Pattern.compile;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static ru.argustelecom.box.env.billing.subscription.SubjectCauseType.CONTRACT;
import static ru.argustelecom.box.env.billing.subscription.SubjectCauseType.ORDER;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.persistence.EntityManager;

import ru.argustelecom.box.env.billing.subscription.model.SubscriptionSubjectCause;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.order.model.Order;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.utils.CDIHelper;

@FacesConverter(forClass = SubjectCauseDto.class)
public class SubjectCauseDtoConverter implements Converter {

	private static final Pattern pattern = compile("^.* \\{id=(\\d*), causeId=(\\d+), type=([A-Z]+)}$");

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null)
			return null;

		Matcher m = pattern.matcher(value);
		if (!m.matches())
			throw new SystemException(format("Can't parse string: '%s'", value));

		Long id = m.group(1).trim().equals(EMPTY) ? null : Long.parseLong(m.group(1));
		Long subjectCauseId = Long.valueOf(m.group(2));
		SubjectCauseType type = SubjectCauseType.valueOf(m.group(3));

		SubjectCauseDtoTranslator subjectCauseDtoTr = CDIHelper.lookupCDIBean(SubjectCauseDtoTranslator.class);
		EntityManager em = EntityManagerUtils.ensure(null);

		if (id != null)
			return subjectCauseDtoTr.translate(em.find(SubscriptionSubjectCause.class, id));
		else if (subjectCauseId != null)
			if (CONTRACT.equals(type))
				return subjectCauseDtoTr.translate(em.find(AbstractContract.class, subjectCauseId));
			else if (ORDER.equals(type))
				return subjectCauseDtoTr.translate(em.find(Order.class, subjectCauseId));

		throw new SystemException(format("Unsupported subject cause type: '%s'", type));
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		return value.toString();
	}

}