package ru.argustelecom.box.env.components;

import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;

import org.apache.commons.lang.StringUtils;

import ru.argustelecom.box.env.MoneyBeautifier;
import ru.argustelecom.box.env.stl.Money;

@FacesComponent("money")
public class MoneyComponent extends UINamingContainer {

	private static final String ATTR_VALUE = "value";

	public String getBeautyValue() {
		return new MoneyBeautifier().toBeauty((Money) getAttributes().get(ATTR_VALUE));
	}

	public String getFullValue() {
		Money value = (Money) getAttributes().get(ATTR_VALUE);
		if (value != null) {
			return value.getAmount().toString();
		}
		return StringUtils.EMPTY;
	}

}
