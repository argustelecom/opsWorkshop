package ru.argustelecom.box.env.billing.invoice;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Date;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.invoice.model.InvoicePeriodEnd;
import ru.argustelecom.box.env.billing.invoice.model.UsageInvoiceSettings;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionType;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class UsageInvoiceSettingsAppService implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private UsageInvoiceSettingsRepository usageInvoiceSettingsRp;

	public UsageInvoiceSettings find() {
		return usageInvoiceSettingsRp.find();
	}

	public void update(Long settingsId, Integer scheduleUnitAmount, PeriodUnit scheduleUnit, Date scheduleStartTime,
			Integer closeInvoiceUnitAmount, PeriodUnit closeInvoiceUnit, InvoicePeriodEnd invoicePeriodEnd,
			boolean reserveFunds, Long optionTypeId) {
		checkNotNull(settingsId);
		checkNotNull(scheduleUnit);
		checkNotNull(scheduleStartTime);
		checkNotNull(closeInvoiceUnit);
		checkNotNull(invoicePeriodEnd);

		UsageInvoiceSettings settings = checkNotNull(find());

		TelephonyOptionType optionType = null;
		if (optionTypeId != null) {
			optionType = checkNotNull(em.find(TelephonyOptionType.class, optionTypeId));
		}

		settings.setScheduleUnitAmount(scheduleUnitAmount);
		settings.setScheduleUnit(scheduleUnit);
		settings.setScheduleStartTime(scheduleStartTime);
		settings.setCloseInvoiceUnitAmount(closeInvoiceUnitAmount);
		settings.setCloseInvoiceUnit(closeInvoiceUnit);
		settings.setInvoicePeriodEnd(invoicePeriodEnd);
		settings.setReserveFunds(reserveFunds);
		settings.setTelephonyOptionType(optionType);
	}

	private static final long serialVersionUID = -48736557800636680L;
}
