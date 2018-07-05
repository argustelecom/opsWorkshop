package ru.argustelecom.box.env.billing.transaction;

import static ru.argustelecom.box.env.billing.transaction.TransactionFilterState.TransactionFilter.FROM;
import static ru.argustelecom.box.env.billing.transaction.TransactionFilterState.TransactionFilter.PERSONAL_ACCOUNT;
import static ru.argustelecom.box.env.billing.transaction.TransactionFilterState.TransactionFilter.TO;

import java.util.Date;
import java.util.Map;
import java.util.function.Supplier;

import javax.inject.Inject;

import ru.argustelecom.box.env.BaseEQConvertibleDtoFilterModel;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.transaction.model.Transaction.TransactionQuery;

public class TransactionFilterModel extends BaseEQConvertibleDtoFilterModel<TransactionQuery> {

	@Inject
	private TransactionFilterState transactionFilterState;

	@Override
	public void buildPredicates(TransactionQuery query) {
		for (Map.Entry<String, Object> e : transactionFilterState.getFilterMap().entrySet()) {
			if (e.getValue() != null) {
				switch (e.getKey()) {
				case PERSONAL_ACCOUNT:
					addPredicate(query.personalAccount().equal((PersonalAccount) e.getValue()));
					break;
				case FROM:
					addPredicate(query.transactionDate().greaterOrEqualTo((Date) e.getValue()));
					break;
				case TO:
					addPredicate(query.transactionDate().lessOrEqualTo((Date) e.getValue()));
					break;
				default:
					break;
				}
			}
		}
	}

	@Override
	public Supplier<TransactionQuery> entityQuerySupplier() {
		return TransactionQuery::new;
	}

	private static final long serialVersionUID = -9094282197665074002L;
}
