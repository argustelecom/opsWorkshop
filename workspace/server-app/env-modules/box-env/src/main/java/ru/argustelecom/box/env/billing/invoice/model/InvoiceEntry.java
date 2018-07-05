package ru.argustelecom.box.env.billing.invoice.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.pricing.model.ProductOffering;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapperDef;
import ru.argustelecom.box.publang.billing.model.IInvoiceEntry;

@Entity
@Access(AccessType.FIELD)
@EntityWrapperDef(name = IInvoiceEntry.WRAPPER_NAME)
public class InvoiceEntry extends BusinessObject {

	private static final long serialVersionUID = 4746614447394466630L;

	@Getter
	@ManyToOne(optional = false)
	private ShortTermInvoice invoice;

	@Getter
	@ManyToOne(optional = false)
	private ProductOffering productOffering;

	@Getter
	@Embedded
	private Money amount;

	@Getter
	@Setter
	private boolean main;

	protected InvoiceEntry() {
	}

	public InvoiceEntry(Long id, ShortTermInvoice invoice, ProductOffering productOffering) {
		super(id);
		this.invoice = invoice;
		this.productOffering = productOffering;
	}

	public void setAmount(Money amount) {
		if (!amount.equals(this.amount)) {
			this.amount = amount;
		}
	}
}
