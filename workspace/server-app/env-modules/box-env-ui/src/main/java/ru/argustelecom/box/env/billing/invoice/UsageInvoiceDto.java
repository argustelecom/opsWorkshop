package ru.argustelecom.box.env.billing.invoice;

import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import ru.argustelecom.box.env.billing.invoice.model.AbstractInvoice;
import ru.argustelecom.box.env.billing.invoice.model.InvoiceState;
import ru.argustelecom.box.env.billing.invoice.model.UsageInvoice;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class UsageInvoiceDto extends ConvertibleDto {

	private Class<? extends AbstractInvoice> clazz;
	private Long id;
	private Money price;
	private Date startDate;
	private Date endDate;
	private Date closingDate;
	private InvoiceState state;
	private String optionName;
	private BusinessObjectDto<Service> service;
	private BusinessObjectDto<PartyRole> provider;

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return UsageInvoice.class;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return UsageInvoiceDtoTranslator.class;
	}
}