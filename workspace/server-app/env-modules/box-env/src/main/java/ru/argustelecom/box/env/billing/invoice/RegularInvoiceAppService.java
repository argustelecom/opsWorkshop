package ru.argustelecom.box.env.billing.invoice;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.invoice.model.InvoiceState;
import ru.argustelecom.box.env.billing.invoice.model.RegularInvoice;
import ru.argustelecom.box.inf.service.ApplicationService;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@ApplicationService
public class RegularInvoiceAppService implements Serializable {

	private static final long serialVersionUID = -8037322244707664684L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private RegularInvoiceRepository regularInvoiceRp;

	public List<RegularInvoice> findInvoices(Long personalAccountId, List<InvoiceState> states) {
		checkNotNull(personalAccountId);

		PersonalAccount personalAccount = em.find(PersonalAccount.class, personalAccountId);
		checkNotNull(personalAccount);

		return regularInvoiceRp.findInvoices(personalAccount, states);
	}
}
