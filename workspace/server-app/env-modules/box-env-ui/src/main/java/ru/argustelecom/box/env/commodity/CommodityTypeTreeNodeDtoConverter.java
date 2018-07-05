package ru.argustelecom.box.env.commodity;

import static java.lang.String.format;
import static ru.argustelecom.system.inf.utils.CDIHelper.lookupCDIBean;

import java.util.Optional;

import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import ru.argustelecom.box.env.commodity.model.CommodityType;
import ru.argustelecom.box.env.commodity.model.CommodityTypeGroup;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.modelbase.Identifiable;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

@ManagedBean
public class CommodityTypeTreeNodeDtoConverter implements Converter {

	private EntityConverter entityConverter;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null) {
			return null;
		}
		return convert(value);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		return Optional.ofNullable(value).map(Object::toString).orElse(null);
	}

	private CommodityTypeTreeNodeDto convert(String value) {
		Identifiable identifiable = getEntityConverter().convertToObject(value);
		CommodityTypeTreeNodeDtoTranslator translator = lookupCDIBean(CommodityTypeTreeNodeDtoTranslator.class);

		if (identifiable instanceof CommodityType) {
			return translator.translate((CommodityType) identifiable);
		} else if (identifiable instanceof CommodityTypeGroup) {
			return translator.translate((CommodityTypeGroup) identifiable);
		} else {
			throw new SystemException(format("Unsupported value for commodity type tree node: '%s'", identifiable));
		}
	}

	private EntityConverter getEntityConverter() {
		if (entityConverter == null) {
			entityConverter = new EntityConverter();
		}
		return entityConverter;
	}

}