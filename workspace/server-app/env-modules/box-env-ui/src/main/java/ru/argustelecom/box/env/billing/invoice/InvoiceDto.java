package ru.argustelecom.box.env.billing.invoice;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.argustelecom.box.env.billing.invoice.model.AbstractInvoice;
import ru.argustelecom.box.env.billing.invoice.model.InvoiceState;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class InvoiceDto extends ConvertibleDto {

	private Long id;
	private String objectName;
	private InvoiceState state;

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return null;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return AbstractInvoice.class;
	}

}