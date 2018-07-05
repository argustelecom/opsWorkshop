package ru.argustelecom.box.env.billing.transaction;

import static ru.argustelecom.box.env.billing.transaction.TransactionLazyDataModel.TransactionSort;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import ru.argustelecom.box.env.EQConvertibleDtoFilterModel;
import ru.argustelecom.box.env.EQConvertibleDtoLazyDataModel;
import ru.argustelecom.box.env.billing.transaction.model.Transaction;
import ru.argustelecom.box.env.billing.transaction.model.Transaction.TransactionQuery;
import ru.argustelecom.box.env.billing.transaction.model.Transaction_;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class TransactionLazyDataModel extends
		EQConvertibleDtoLazyDataModel<Transaction, TransactionDto, TransactionQuery, TransactionSort> {

	@Inject
	private TransactionDtoTranslator transactionDtoTranslator;

	@Inject
	private TransactionFilterModel transactionFilterModel;

	@PostConstruct
	private void postConstruct() {
		addPath(TransactionSort.date, query -> query.root().get(Transaction_.transactionDate));
	}

	@Override
	protected Class<TransactionSort> getSortableEnum() {
		return TransactionSort.class;
	}

	@Override
	protected DefaultDtoTranslator<TransactionDto, Transaction> getDtoTranslator() {
		return transactionDtoTranslator;
	}

	@Override
	protected EQConvertibleDtoFilterModel<TransactionQuery> getFilterModel() {
		return transactionFilterModel;
	}

	public enum TransactionSort {
		date
	}

	private static final long serialVersionUID = 2773622926379312595L;
}