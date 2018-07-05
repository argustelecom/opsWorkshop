package ru.argustelecom.box.env.report;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.persistence.EntityManager;

import ru.argustelecom.box.env.report.api.ReportOutputFormat;
import ru.argustelecom.box.env.report.model.ReportModelTemplate;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;

@FacesConverter(forClass = ReportItem.class)
public class ReportItemConverter implements Converter {

	private static final Pattern pattern = Pattern.compile("^(.+ \\{ templateId=)(\\d+)(, outputFormat=)([A-Z]+) }$");

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null)
			return null;

		Matcher m = pattern.matcher(value);

		if (!m.matches())
			return null;

		Long templateId = Long.valueOf(m.group(2));
		ReportOutputFormat format = ReportOutputFormat.valueOf(m.group(4));

		EntityManager em = EntityManagerUtils.ensure(null);
		ReportModelTemplate template = em.find(ReportModelTemplate.class, templateId);

		return new ReportItem(template, format);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		return value.toString();
	}

}