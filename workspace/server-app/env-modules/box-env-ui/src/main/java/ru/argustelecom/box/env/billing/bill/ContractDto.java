package ru.argustelecom.box.env.billing.bill;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Setter
@NoArgsConstructor
public class ContractDto extends ConvertibleDto {
	private Long id;
	private String documentNumber;
	private PaymentCondition paymentCondition;

	@Builder
	public ContractDto(Long id, String documentNumber, PaymentCondition paymentCondition) {
		this.id = id;
		this.documentNumber = documentNumber;
		this.paymentCondition = paymentCondition;
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
