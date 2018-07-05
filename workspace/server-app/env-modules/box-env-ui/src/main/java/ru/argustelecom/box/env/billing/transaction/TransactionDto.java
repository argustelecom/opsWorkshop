package ru.argustelecom.box.env.billing.transaction;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.billing.transaction.model.Transaction;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.system.inf.modelbase.Identifiable;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class TransactionDto extends ConvertibleDto {
	private Long id;
	private Date transactionDate;
	private String reasonType;
	private String reasonNumber;
	private Money amount;

	@Builder
	public TransactionDto(Long id, Date transactionDate, String reasonType, String reasonNumber, Money amount) {
		this.id = id;
		this.transactionDate = transactionDate;
		this.reasonType = reasonType;
		this.reasonNumber = reasonNumber;
		this.amount = amount;
	}


	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return TransactionDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return Transaction.class;
	}
}
