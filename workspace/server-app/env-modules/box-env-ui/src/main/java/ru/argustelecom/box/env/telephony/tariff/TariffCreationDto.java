package ru.argustelecom.box.env.telephony.tariff;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

import ru.argustelecom.box.env.billing.provision.model.RoundingPolicy;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.env.telephony.tariff.model.CommonTariff;

/**
 * DTO для создания тарифного плана
 */
@Getter
@Setter
@NoArgsConstructor
public class TariffCreationDto {
	private String name;
	private Date validFrom;
	private Date validTo;
	private BusinessObjectDto<Customer> customer;
	private PeriodUnit ratedUnit;
	private RoundingPolicy roundingPolicy;
	private BusinessObjectDto<CommonTariff> parent;
	private BusinessObjectDto<CustomerType> customerType;
}
