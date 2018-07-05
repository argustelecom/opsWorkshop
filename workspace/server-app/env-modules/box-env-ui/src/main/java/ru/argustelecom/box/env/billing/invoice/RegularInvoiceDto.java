package ru.argustelecom.box.env.billing.invoice;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

import ru.argustelecom.box.env.billing.invoice.model.InvoiceState;
import ru.argustelecom.box.env.billing.invoice.model.RegularInvoice;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.privilege.model.Privilege;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(of = "id", callSuper = false)
public class RegularInvoiceDto extends ConvertibleDto {
	private Long id;
	private InvoiceState state;
	private String subscriptionName;
	private Money totalPrice;
	private Date startDate;
	private Date endDate;
	private BusinessObjectDto<Privilege> privilege;

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return RegularInvoiceDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return RegularInvoice.class;
	}
}
