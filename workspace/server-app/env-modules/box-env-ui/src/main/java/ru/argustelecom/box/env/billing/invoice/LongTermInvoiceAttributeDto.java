package ru.argustelecom.box.env.billing.invoice;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;
import ru.argustelecom.box.env.billing.invoice.model.InvoiceState;
import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.product.model.AbstractProductType;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
public class LongTermInvoiceAttributeDto extends AbstractInvoiceAttributeDto {
	private BusinessObjectDto<Subscription> subscription;
	private BusinessObjectDto<AbstractProductType> typeProduct;
	private Date startDate;
	private Date endDate;
	private PeriodUnit periodUnit;

	@Builder
	public LongTermInvoiceAttributeDto(Long id, InvoiceState state, Date closingDate,
			BusinessObjectDto<Subscription> subscription, BusinessObjectDto<AbstractProductType> typeProduct,
			Money totalPrice, Date startDate, Date endDate, PeriodUnit periodUnit) {
		super(id, state, closingDate, totalPrice);
		this.subscription = subscription;
		this.typeProduct = typeProduct;
		this.startDate = startDate;
		this.endDate = endDate;
		this.periodUnit = periodUnit;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return LongTermInvoiceAttributeTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return LongTermInvoice.class;
	}
}
