package ru.argustelecom.box.env.billing.invoice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import ru.argustelecom.box.env.billing.invoice.model.AbstractInvoice;
import ru.argustelecom.box.env.billing.invoice.model.InvoiceState;
import ru.argustelecom.box.env.billing.invoice.model.UsageInvoiceEntry;
import ru.argustelecom.box.env.billing.invoice.model.UsageInvoiceEntryData;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.env.telephony.tariff.model.TelephonyZone;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsageInvoiceEntryDto extends ConvertibleDto {

	private Long id;
	private List<UsageInvoiceEntryData> entries;

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return UsageInvoiceEntryDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return UsageInvoiceEntry.class;
	}

}