package ru.argustelecom.box.env.billing.invoice;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named("longTermInvoiceAttributesFm")
@PresentationModel
public class LongTermInvoiceAttributesFrameModel implements Serializable {

	private static final long serialVersionUID = 3170018751496396039L;

	@Inject
	private LongTermInvoiceAttributeTranslator tr;

	@Getter
	private LongTermInvoiceAttributeDto invoice;

	public void preRender(LongTermInvoice invoice) {
		this.invoice = tr.translate(invoice);
	}
}