package ru.argustelecom.box.env.billing.invoice;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.invoice.model.ShortTermInvoice;
import ru.argustelecom.box.env.pricing.model.MeasuredProductOffering;
import ru.argustelecom.box.inf.service.ApplicationService;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

@ApplicationService
public class ShortTermInvoiceAppService implements Serializable {

	private static final long serialVersionUID = 4804447799695801696L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private ShortTermInvoiceRepository shortTermInvoiceRp;

	public ShortTermInvoice createInvoice(Long personalAccountId,
										  List<Long> entriesIds) {
		checkNotNull(personalAccountId);
		checkArgument(entriesIds != null && !entriesIds.isEmpty());

		PersonalAccount personalAccount = em.find(PersonalAccount.class, personalAccountId);
		checkNotNull(personalAccount);

		List<MeasuredProductOffering> entries = entriesIds.stream().map(entryId -> {
			MeasuredProductOffering entry = em.find(MeasuredProductOffering.class, entryId);
			checkNotNull(entry);

			return entry;
		}).collect(toList());

		return shortTermInvoiceRp.createInvoice(personalAccount, entries);
	}
}
