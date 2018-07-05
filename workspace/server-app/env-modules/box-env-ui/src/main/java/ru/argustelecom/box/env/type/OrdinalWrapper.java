package ru.argustelecom.box.env.type;

import lombok.Setter;
import ru.argustelecom.box.env.type.model.Ordinal;

public class OrdinalWrapper {
	@Setter
	private Ordinal ordinal;

	public Integer getOrdinalNumber() {
		return ordinal.getOrdinalNumber();
	}

	public void setOrdinalNumber(Integer ordinalNumber) {
		ordinal.changeOrdinalNumber(ordinalNumber);
	}

	public long getMaxOrdinalNumber() {
		return ordinal.group().size();
	}

}
