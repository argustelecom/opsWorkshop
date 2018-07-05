package ru.argustelecom.box.env.billing.invoice;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;

import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.billing.invoice.model.UsageInvoice;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named("usageInvoiceAttrFm")
@PresentationModel
public class UsageInvoiceAttributesFrameModel implements Serializable {

	private static final long serialVersionUID = 6187738331487542785L;

	@Inject
	private UsageInvoiceDtoTranslator usageInvoiceDtoTr;

	@Getter
	private UsageInvoiceDto invoice;

	public void preRender(UsageInvoice invoice) {
		this.invoice = usageInvoiceDtoTr.translate(invoice);
	}
}