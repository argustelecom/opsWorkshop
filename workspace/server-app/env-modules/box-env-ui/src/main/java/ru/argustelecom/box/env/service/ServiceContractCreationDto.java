package ru.argustelecom.box.env.service;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

import ru.argustelecom.box.env.contract.model.ContractType;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.party.model.PartyRole;

@Getter
@Setter
@NoArgsConstructor
public class ServiceContractCreationDto {

	private BusinessObjectDto<ContractType> contractType;
	private Date validFrom;
	private Date validTo;
	private PaymentCondition paymentCondition;
	private String number;
	private BusinessObjectDto<PartyRole> broker;

	@Builder
	public ServiceContractCreationDto(BusinessObjectDto<ContractType> contractType, Date validFrom, Date validTo,
			PaymentCondition paymentCondition, String number, BusinessObjectDto<PartyRole> broker) {
		this.contractType = contractType;
		this.validFrom = validFrom;
		this.validTo = validTo;
		this.paymentCondition = paymentCondition;
		this.number = number;
		this.broker = broker;
	}

}
