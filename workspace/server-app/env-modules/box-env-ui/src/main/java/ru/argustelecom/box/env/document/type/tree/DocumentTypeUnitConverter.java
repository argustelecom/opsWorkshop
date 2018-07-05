package ru.argustelecom.box.env.document.type.tree;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Strings;

import ru.argustelecom.box.env.document.type.DocumentTypeCategory;
import ru.argustelecom.box.env.document.type.DocumentTypeDto;
import ru.argustelecom.box.env.dto.DefaultDtoConverter;

@FacesConverter(value = "documentTypeUnitConverter")
public class DocumentTypeUnitConverter implements Converter {

	private DefaultDtoConverter converter;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (Strings.isNullOrEmpty(value))
			return null;

		DocumentTypeCategory category = DocumentTypeCategory.of(value);
		if (category != null) {
			return new DocumentTypeCategoryUnit(category);
		}

		DocumentTypeDto documentTypeDto = (DocumentTypeDto) getConverter().getAsObject(context, component, value);
		return new DocumentTypeUnit(documentTypeDto);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null) {
			return StringUtils.EMPTY;
		}

		if (value instanceof DocumentTypeCategoryUnit) {
			return ((DocumentTypeCategoryUnit) value).getDelegate().name();
		}
		if (value instanceof DocumentTypeUnit) {
			return getConverter().getAsString(context, component, ((DocumentTypeUnit) value).getDelegate());
		}

		return null;
	}

	public DefaultDtoConverter getConverter() {
		if (converter == null) {
			converter = new DefaultDtoConverter();
		}
		return converter;
	}
}
