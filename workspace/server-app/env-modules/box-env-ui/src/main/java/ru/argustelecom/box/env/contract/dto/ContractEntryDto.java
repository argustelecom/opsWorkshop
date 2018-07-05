package ru.argustelecom.box.env.contract.dto;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.billing.bill.PersonalAccountDto;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.box.env.service.ServiceDto;
import ru.argustelecom.box.env.telephony.tariff.model.TelephonyZone;

/**
 * DTO для функционального блока позиций договора.
 */
@Getter
@Builder
@AllArgsConstructor
public class ContractEntryDto {

	private Long id;

	@Setter
	private String subjectName;

	@Setter
	private String subjectFullName;

	@Setter
	private BusinessObjectDto<AbstractPricelist> pricelist;

	private PersonalAccountDto personalAccount;

	private List<String> addresses;

	@Setter
	private List<ServiceDto> services;

	/**
	 * Для позиции договора на основании опции. Зоны, к которым привязана опция.
	 */
	@Setter
	private List<BusinessObjectDto<TelephonyZone>> zones;

	/**
	 * Для позиции договора на основании опции. Услуга, к которой принадлежит опция.
	 */
	@Setter
	private BusinessObjectDto<Service> service;

	/**
	 * Для позиции договора на основании опции. Договор, в рамках которого предоставляется услуга, к которой принадлежит
	 * опция.
	 */
	@Setter
	private BusinessObjectDto<AbstractContract> serviceContract;

	@Setter
	private boolean optionContractEntry;

	public boolean hasOneService() {
		return !CollectionUtils.isEmpty(getServices()) && getServices().size() == 1;
	}

	public boolean hasSeveralServices() {
		return !CollectionUtils.isEmpty(getServices()) && getServices().size() > 1;
	}

	public ServiceDto getFirstService() {
		return !CollectionUtils.isEmpty(getServices()) ? getServices().get(0) : null;
	}

}