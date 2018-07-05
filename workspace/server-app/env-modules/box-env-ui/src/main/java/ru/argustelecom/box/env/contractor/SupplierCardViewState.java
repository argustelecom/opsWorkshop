package ru.argustelecom.box.env.contractor;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

import javax.inject.Named;

import ru.argustelecom.system.inf.page.PresentationState;

@Named(value = "supplierCardVs")
@PresentationState
public class SupplierCardViewState implements Serializable {

	@Getter
	@Setter
	private SupplierDto supplierDto;

	public boolean isEmpty() {
		return supplierDto != null;
	}

	private static final long serialVersionUID = 7681576602676491638L;

}