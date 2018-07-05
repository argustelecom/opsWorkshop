package ru.argustelecom.box.env.billing.invoice.model;

import static ru.argustelecom.box.env.billing.invoice.model.InvoiceState.ACTIVE;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.account.model.Reserve;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.commodity.telephony.model.Option;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryLogicalFilter;

@Entity
@Access(AccessType.FIELD)
public class UsageInvoice extends RegularInvoice {

	private static final long serialVersionUID = -2621560284102210452L;

	@Getter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "service_id", nullable = false)
	private Service service;

	@Getter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "option_id")
	private Option<?, ?> option;

	@Getter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "provider_id", nullable = false)
	private PartyRole provider;

	@Getter
	@Column(nullable = false)
	private boolean withoutContract;

	@Setter
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "entries_id")
	private UsageInvoiceEntry entries;

	/**
	 * Конструктор для JPA
	 */
	protected UsageInvoice() {
	}

	/**
	 * Основной конструктор, предназначен для инстанцирования инвойса через UsageInvoiceBuilder
	 *
	 * @param id
	 *            - идентификатор инвойса, должен быть определен извне
	 * @param personalAccount
	 *            - лицевой счет, на котором необходимо создать инвойс
	 */
	@Builder
	protected UsageInvoice(Long id, PersonalAccount personalAccount, Service service, Option<?, ?> option,
			PartyRole provider, Date startDate, Date endDate, boolean withoutContract, UsageInvoiceEntry entries) {
		super(id, personalAccount);
		this.service = service;
		this.option = option;
		this.provider = provider;
		this.withoutContract = withoutContract;
		this.entries = entries;

		setStartDate(startDate);
		setEndDate(endDate);
		setPrice(Money.ZERO);
		setState(ACTIVE);
	}

	/**
	 * Если поддерживается резервирование средств, то переданный объект Reserve ассоциируется с текущим инвойсом
	 *
	 * @return true, если резервирование поддерживается
	 */
	public boolean attachReserve(Reserve reserve, UsageInvoiceSettings settings) {
		Boolean reserveSupported = settings.isReserveFunds();
		if (reserveSupported) {
			doAttachReserve(reserve);
		}
		return reserveSupported;
	}

	@Override
	public Money getTotalPrice() {
		return getPrice();
	}

	public boolean addEntry(UsageInvoiceEntryData entry) {
		boolean added = entries.getUsageInvoiceEntryContainer().addEntry(entry);
		if (added) {
			updatePrice();
		}
		return added;
	}

	public boolean removeEntry(UsageInvoiceEntryData entry) {
		boolean removed = entries.getUsageInvoiceEntryContainer().removeEntry(entry);
		if (removed) {
			updatePrice();
		}
		return removed;
	}

	public void removeAllEntries() {
		entries.getUsageInvoiceEntryContainer().removeAllEntries();
	}

	public boolean hasEntry(UsageInvoiceEntryData entry) {
		return entries.getUsageInvoiceEntryContainer().hasEntry(entry);
	}

	public List<UsageInvoiceEntryData> getEntries() {
		return entries.getUsageInvoiceEntryContainer().getEntries();
	}

	public UsageInvoiceEntry getEntriesHolder() {
		return entries;
	}

	private void updatePrice() {
		BigDecimal price = new BigDecimal(0);

		for (UsageInvoiceEntryData entry : getEntries()) {
			price = price.add(entry.getAmount());
		}

		setPrice(new Money(price));
	}

	@SuppressWarnings("rawtypes")
	public static class UsageInvoiceQuery extends RegularInvoiceQuery<UsageInvoice> {

		private EntityQueryEntityFilter<UsageInvoice, Service> service;

		private EntityQueryEntityFilter<UsageInvoice, Option> option;
		private EntityQueryEntityFilter<UsageInvoice, PartyRole> provider;
		private EntityQueryLogicalFilter<UsageInvoice> withoutContract;

		public UsageInvoiceQuery() {
			super(UsageInvoice.class);
			service = createEntityFilter(UsageInvoice_.service);
			option = createEntityFilter(UsageInvoice_.option);
			provider = createEntityFilter(UsageInvoice_.provider);
			withoutContract = createLogicalFilter(UsageInvoice_.withoutContract);
		}

		public EntityQueryEntityFilter<UsageInvoice, Service> service() {
			return service;
		}

		public EntityQueryEntityFilter<UsageInvoice, Option> option() {
			return option;
		}

		public EntityQueryEntityFilter<UsageInvoice, PartyRole> provider() {
			return provider;
		}

		public EntityQueryLogicalFilter<UsageInvoice> withoutContract() {
			return withoutContract;
		}

	}
}
