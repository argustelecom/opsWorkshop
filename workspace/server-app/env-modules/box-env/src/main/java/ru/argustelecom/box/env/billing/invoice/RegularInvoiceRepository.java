package ru.argustelecom.box.env.billing.invoice;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Predicate;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.invoice.model.InvoiceState;
import ru.argustelecom.box.env.billing.invoice.model.RegularInvoice;
import ru.argustelecom.box.env.billing.invoice.model.RegularInvoice.RegularInvoiceQuery;
import ru.argustelecom.box.env.billing.invoice.model.UsageInvoice;
import ru.argustelecom.box.env.billing.invoice.model.UsageInvoice.UsageInvoiceQuery;
import ru.argustelecom.box.env.billing.subscription.SubscriptionRepository;
import ru.argustelecom.box.env.billing.subscription.accounting.impl.InvoicePlannerConfig;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOption;
import ru.argustelecom.box.env.contract.model.ContractType;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.inf.service.Repository;

@Repository
public class RegularInvoiceRepository implements Serializable {

	private static final long serialVersionUID = -2585150455465796956L;

	@PersistenceContext
	private transient EntityManager em;

	/**
	 * Находит все инвойсы на указанном лицевом счете с указанными состояниями
	 *
	 * @param personalAccount
	 *            - лицевой счет, для которого хотим найти инвойсы
	 * @param states
	 *            - список состояний инвойса
	 *
	 * @return список найденных инвойсов или пустую коллекцию, если по условиям поиска ничего не нашлось
	 */
	@SuppressWarnings("unchecked")
	public List<RegularInvoice> findInvoices(PersonalAccount personalAccount, List<InvoiceState> states) {
		checkNotNull(personalAccount);

		RegularInvoiceQuery query = new RegularInvoiceQuery(RegularInvoice.class);

		if (states != null && !states.isEmpty()) {
			query.or(states.stream().map(s -> query.state().equal(s)).collect(toList()).toArray(new Predicate[] {}));
		}

		query.and(query.personalAccount().equal(personalAccount));

		return query.getResultList(em);
	}
}
