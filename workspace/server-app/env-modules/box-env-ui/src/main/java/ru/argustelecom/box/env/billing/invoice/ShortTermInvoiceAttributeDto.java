package ru.argustelecom.box.env.billing.invoice;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;
import ru.argustelecom.box.env.billing.invoice.model.InvoiceState;
import ru.argustelecom.box.env.billing.invoice.model.ShortTermInvoice;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
public class ShortTermInvoiceAttributeDto extends AbstractInvoiceAttributeDto {

	@Builder
	public ShortTermInvoiceAttributeDto(Long id, InvoiceState state, Date closingDate, Money totalPrice) {
		super(id, state, closingDate, totalPrice);
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return ShortTermInvoiceAttributeDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return ShortTermInvoice.class;
	}
}
