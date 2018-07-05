package ru.argustelecom.box.env.billing.transaction;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.filter.FilterMapEntry;
import ru.argustelecom.box.env.filter.FilterViewState;
import ru.argustelecom.system.inf.page.PresentationState;

import java.util.Date;

import static ru.argustelecom.box.env.billing.transaction.TransactionFilterState.TransactionFilter.FROM;
import static ru.argustelecom.box.env.billing.transaction.TransactionFilterState.TransactionFilter.PERSONAL_ACCOUNT;
import static ru.argustelecom.box.env.billing.transaction.TransactionFilterState.TransactionFilter.TO;

@PresentationState
@Getter
@Setter
public class TransactionFilterState extends FilterViewState {

	@FilterMapEntry(FROM)
	private Date startDate;
	@FilterMapEntry(TO)
	private Date endDate;
	@FilterMapEntry(PERSONAL_ACCOUNT)
	private PersonalAccount personalAccount;

	public final class TransactionFilter {
		public static final String PERSONAL_ACCOUNT = "PERSONAL_ACCOUNT";
		public static final String FROM = "FROM";
		public static final String TO = "TO";
	}

	private static final long serialVersionUID = 6985049687870030643L;
}
