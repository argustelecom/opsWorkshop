package ru.argustelecom.box.env.billing.invoice;

import java.util.Date;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.billing.invoice.model.InvoicePeriodEnd;
import ru.argustelecom.box.env.billing.invoice.model.UsageInvoiceSettings;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionType;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class UsageInvoiceSettingsDto extends ConvertibleDto {
	private Long id;
	private Integer scheduleUnitAmount;
	private PeriodUnit scheduleUnit;
	private Date scheduleStartTime;
	private Integer closeInvoiceUnitAmount;
	private PeriodUnit closeInvoiceUnit;
	private InvoicePeriodEnd invoicePeriodEnd;
	private Boolean reserveFunds;
	private BusinessObjectDto<TelephonyOptionType> optionType;

	@Builder
	public UsageInvoiceSettingsDto(Long id, Integer scheduleUnitAmount, PeriodUnit scheduleUnit, Date scheduleStartTime,
			Integer closeInvoiceUnitAmount, PeriodUnit closeInvoiceUnit, InvoicePeriodEnd invoicePeriodEnd,
			Boolean reserveFunds, BusinessObjectDto<TelephonyOptionType> optionType) {
		this.id = id;
		this.scheduleUnitAmount = scheduleUnitAmount;
		this.scheduleUnit = scheduleUnit;
		this.scheduleStartTime = scheduleStartTime;
		this.closeInvoiceUnitAmount = closeInvoiceUnitAmount;
		this.closeInvoiceUnit = closeInvoiceUnit;
		this.invoicePeriodEnd = invoicePeriodEnd;
		this.reserveFunds = reserveFunds;
		this.optionType = optionType;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return UsageInvoiceSettingsTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return UsageInvoiceSettings.class;
	}
}
