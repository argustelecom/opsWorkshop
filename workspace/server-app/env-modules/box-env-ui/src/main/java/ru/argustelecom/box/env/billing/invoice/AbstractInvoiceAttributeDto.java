package ru.argustelecom.box.env.billing.invoice;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.argustelecom.box.env.billing.invoice.model.InvoiceState;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.stl.Money;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public abstract class AbstractInvoiceAttributeDto extends ConvertibleDto {
	private Long id;
	private InvoiceState state;
	private Date closingDate;
	private Money totalPrice;
}
