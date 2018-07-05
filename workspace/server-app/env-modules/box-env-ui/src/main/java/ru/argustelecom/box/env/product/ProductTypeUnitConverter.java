package ru.argustelecom.box.env.product;

import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.apache.commons.lang.StringUtils;

import ru.argustelecom.box.env.product.model.AbstractProductType;
import ru.argustelecom.box.env.product.model.ProductType;
import ru.argustelecom.box.env.product.model.ProductTypeComposite;
import ru.argustelecom.box.env.product.model.ProductTypeGroup;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

@ManagedBean
public class ProductTypeUnitConverter implements Converter {

	private EntityConverter entityConverter;

	@Override
	public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
		if (value == null) {
			return null;
		}
		return convert(value);
	}

	@Override
	public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object object) {
		if (object == null) {
			return StringUtils.EMPTY;
		}
		return getEntityConverter().convertToString(((AbstractProductUnit) object).getWrappedEntity());
	}

	private AbstractProductUnit convert(String value) {
		Object object = getEntityConverter().convertToObject(value);

		if (object instanceof ProductTypeGroup)
			return new ProductTypeGroupUnit((ProductTypeGroup) object);
		if (object instanceof ProductType)
			return new ProductTypeUnit((AbstractProductType) object);
		if (object instanceof ProductTypeComposite)
			return new ProductTypeCompositeUnit((AbstractProductType) object);

		return null;
	}

	private EntityConverter getEntityConverter() {
		if (entityConverter == null) {
			entityConverter = new EntityConverter();
		}
		return entityConverter;
	}
}