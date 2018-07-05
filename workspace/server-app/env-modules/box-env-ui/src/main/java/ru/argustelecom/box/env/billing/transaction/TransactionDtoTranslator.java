package ru.argustelecom.box.env.billing.transaction;

import ru.argustelecom.box.env.billing.transaction.model.Transaction;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class TransactionDtoTranslator implements DefaultDtoTranslator<TransactionDto, Transaction> {

	@Override
	public TransactionDto translate(Transaction trans) {
		//@formatter:off
		return TransactionDto.builder()
				.id(trans.getId())
				.transactionDate(trans.getTransactionDate())
				.reasonType(trans.getReason().getReasonType())
				.reasonNumber(trans.getReason().getReasonNumber())
				.amount(trans.getAmount())
				.build();
		//@formatter:on
	}
}
