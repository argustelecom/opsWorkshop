package ru.argustelecom.box.env.telephony.tariff;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

import javax.inject.Named;

import ru.argustelecom.system.inf.page.PresentationState;

@Named(value = "tariffCardVs")
@PresentationState
public class TariffCardViewState implements Serializable {

	@Getter
	@Setter
	private TariffDto tariffDto;

	@Getter
	@Setter
	private TariffEntryDto tariffEntryDto;

	public boolean isEmpty() {
		return tariffDto != null;
	}

	private static final long serialVersionUID = -6857441160122640863L;
}
