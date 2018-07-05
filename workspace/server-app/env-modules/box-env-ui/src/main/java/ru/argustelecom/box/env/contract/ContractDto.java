package ru.argustelecom.box.env.contract;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Setter
@NoArgsConstructor
public class ContractDto extends ConvertibleDto {
	private Long id;
	private String documentNumber;
	private String documentType;
	private String customer;
	private String customerType;
	private Date validFrom;
	private Date validTo;
	private String state;
	private String providerName;
	private String brokerName;

	@Builder
	public ContractDto(Long id, String documentNumber, String documentType, String customer, String customerType,
			Date validFrom, Date validTo, String state, String providerName, String brokerName) {
		this.id = id;
		this.documentNumber = documentNumber;
		this.documentType = documentType;
		this.customer = customer;
		this.customerType = customerType;
		this.validFrom = validFrom;
		this.validTo = validTo;
		this.state = state;
		this.providerName = providerName;
		this.brokerName = brokerName;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return ContractDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return Contract.class;
	}
}
