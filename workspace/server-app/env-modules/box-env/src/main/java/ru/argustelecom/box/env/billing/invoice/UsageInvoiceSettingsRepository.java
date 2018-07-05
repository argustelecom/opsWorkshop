package ru.argustelecom.box.env.billing.invoice;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.invoice.model.InvoicePeriodEnd;
import ru.argustelecom.box.env.billing.invoice.model.UsageInvoiceSettings;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionType;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.cache.DirectoryCacheService;

@Repository
public class UsageInvoiceSettingsRepository implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService iss;

	@Inject
	private DirectoryCacheService directoryCacheSrv;

	/**
	 * Сейчас хранится только одни экземпляр настроек
	 */
	public UsageInvoiceSettings create(int scheduleUnitAmount, PeriodUnit scheduleUnit, Date scheduleStartTime,
			int closeInvoiceUnitAmount, PeriodUnit closeInvoiceUnit, InvoicePeriodEnd invoicePeriodEnd,
			boolean reserveFunds, TelephonyOptionType optionType) {

		checkNotNull(scheduleUnit);
		checkNotNull(scheduleStartTime);
		checkNotNull(closeInvoiceUnit);
		checkNotNull(invoicePeriodEnd);
		checkNotNull(optionType);

		UsageInvoiceSettings settings = new UsageInvoiceSettings(iss.nextValue(UsageInvoiceSettings.class));

		settings.setScheduleUnitAmount(scheduleUnitAmount);
		settings.setScheduleUnit(scheduleUnit);
		settings.setScheduleStartTime(scheduleStartTime);
		settings.setCloseInvoiceUnitAmount(closeInvoiceUnitAmount);
		settings.setCloseInvoiceUnit(closeInvoiceUnit);
		settings.setInvoicePeriodEnd(invoicePeriodEnd);
		settings.setReserveFunds(reserveFunds);
		settings.setTelephonyOptionType(optionType);

		em.persist(settings);

		return settings;
	}

	public UsageInvoiceSettings find() {
		List<UsageInvoiceSettings> settings = directoryCacheSrv.getDirectoryObjects(UsageInvoiceSettings.class);
		checkState(settings.size() == 1, "Должен существовать только одни экземпляр правил тарификации");
		return settings.get(0);
	}

	private static final long serialVersionUID = 4688795850908901941L;
}
