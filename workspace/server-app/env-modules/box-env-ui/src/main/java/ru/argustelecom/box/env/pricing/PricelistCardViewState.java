package ru.argustelecom.box.env.pricing;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

import javax.inject.Named;

import ru.argustelecom.system.inf.page.PresentationState;

@Named(value = "pricelistCardViewState")
@PresentationState
public class PricelistCardViewState implements Serializable {

	@Getter
	@Setter
	private PricelistAttributesDto pricelistDto;

	public boolean isEmpty() {
		return pricelistDto != null;
	}

	private static final long serialVersionUID = 7681576602676491638L;

}