package ru.argustelecom.box.env.billing.invoice;

import java.util.Date;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import ru.argustelecom.box.env.billing.invoice.model.AbstractInvoice;
import ru.argustelecom.box.env.billing.invoice.model.InvoiceState;
import ru.argustelecom.box.env.dto.IdentifiableDto;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@EqualsAndHashCode(of = "id")
public class LongTermInvoiceDto implements IdentifiableDto {

	private Long id;
	private Money price;
	private Date startDate;
	private Date endDate;
	private InvoiceState state;
	private Long privilegeId;
	private String privilegeName;

	@Builder
	public LongTermInvoiceDto(Long id, Money price, Date startDate, Date endDate, InvoiceState state, Long privilegeId,
			String privilegeName) {
		this.id = id;
		this.price = price;
		this.startDate = startDate;
		this.endDate = endDate;
		this.state = state;
		this.privilegeId = privilegeId;
		this.privilegeName = privilegeName;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return AbstractInvoice.class;
	}

	@Override
	public String toString() {
		return (new StringBuilder(128)).append("Инвойс: ").append(id).append("; Сумма: ").append(price).append(" руб.")
				.toString();
	}

}