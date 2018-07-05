package ru.argustelecom.box.env.billing.invoice.chargejob;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

import javax.inject.Named;

import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.system.inf.page.PresentationState;

@PresentationState
@Named("chargeJobCardVs")
public class ChargeJobCardViewState implements Serializable {

	private static final long serialVersionUID = -4273045760746056784L;

	@Getter
	@Setter
	private ChargeJobDto chargeJob;

	@Getter
	@Setter
	private BusinessObjectDto<AbstractTariff> tariff;

	@Getter
	@Setter
	private BusinessObjectDto<Service> service;
}
