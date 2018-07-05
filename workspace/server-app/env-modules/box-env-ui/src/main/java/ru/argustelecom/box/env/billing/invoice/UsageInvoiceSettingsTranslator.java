package ru.argustelecom.box.env.billing.invoice;

import static java.util.Optional.ofNullable;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.invoice.UsageInvoiceSettingsDto.UsageInvoiceSettingsDtoBuilder;
import ru.argustelecom.box.env.billing.invoice.model.UsageInvoiceSettings;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class UsageInvoiceSettingsTranslator
		implements DefaultDtoTranslator<UsageInvoiceSettingsDto, UsageInvoiceSettings> {

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Override
	public UsageInvoiceSettingsDto translate(UsageInvoiceSettings settings) {
		//@formatter:off
		UsageInvoiceSettingsDtoBuilder builder = UsageInvoiceSettingsDto.builder();
		ofNullable(settings.getTelephonyOptionType()).map(businessObjectDtoTr::translate).ifPresent(builder::optionType);
		return builder
					.id(settings.getId())
					.scheduleUnitAmount(settings.getScheduleUnitAmount())
					.scheduleUnit(settings.getScheduleUnit())
					.scheduleStartTime(settings.getScheduleStartTime())
					.closeInvoiceUnitAmount(settings.getCloseInvoiceUnitAmount())
					.closeInvoiceUnit(settings.getCloseInvoiceUnit())
					.invoicePeriodEnd(settings.getInvoicePeriodEnd())
					.reserveFunds(settings.isReserveFunds())
				.build();
		//@formatter:on
	}
}
