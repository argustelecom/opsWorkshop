package ru.argustelecom.box.env.mediation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.commodity.model.ServiceType;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;

@Getter
@Setter
@NoArgsConstructor
public class ChargeJobCreationDto {
	private Date dateFrom;
	private Date dateTo;
	private BusinessObjectDto<Service> service;
	private BusinessObjectDto<AbstractTariff> tariff;
	private BusinessObjectDto<ServiceType> serviceType;
}
