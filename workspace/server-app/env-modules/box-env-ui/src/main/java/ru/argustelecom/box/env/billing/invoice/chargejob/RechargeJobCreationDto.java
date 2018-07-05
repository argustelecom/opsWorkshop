package ru.argustelecom.box.env.billing.invoice.chargejob;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

import ru.argustelecom.box.env.billing.invoice.chargejob.ChargeJobCreationDialogModel.RechargeCause;
import ru.argustelecom.box.env.billing.invoice.model.FilterAggData;
import ru.argustelecom.box.env.commodity.model.ServiceType;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class RechargeJobCreationDto {
	private BusinessObjectDto<ServiceType> serviceType;
	private ServiceChargeJobContextDto service;
	private BusinessObjectDto<Customer> customer;
	private BusinessObjectDto<CustomerType> customerType;
	private BusinessObjectDto<AbstractTariff> tariff;
	private FilterAggData filter;
	private RechargeCause cause;


	public RechargeJobCreationDto() {
		filter = new FilterAggData();
	}

	public void setService(ServiceChargeJobContextDto service) {
		if (!Objects.equals(this.service, service)) {
			this.service = service;
			filter.setServiceId(service != null ? service.getId() : null);
		}
	}

	public void setTariff(BusinessObjectDto<AbstractTariff> tariff) {
		if (!Objects.equals(this.tariff, tariff)) {
			this.tariff = tariff;
			filter.setTariffId(tariff != null ? tariff.getId() : null);
		}
	}

	public void setCause(RechargeCause cause) {
		if (!Objects.equals(this.cause, cause)) {
			this.cause = cause;
			filter.setCause(cause.getName());
			filter.setProcessingStage(cause.getProcessingStage());
		}
	}

	public void setServiceType(BusinessObjectDto<ServiceType> serviceType) {
		if (!Objects.equals(this.serviceType, serviceType)) {
			this.serviceType = serviceType;
			setService(null);
		}
	}

	public void setCustomerType(BusinessObjectDto<CustomerType> customerType) {
		if (!Objects.equals(this.customerType, customerType)) {
			this.customerType = customerType;
			setCustomer(null);
		}
	}

	public void setCustomer(BusinessObjectDto<Customer> customer) {
		if (!Objects.equals(this.customer, customer)) {
			this.customer = customer;
			setServiceType(null);
		}
	}
}
