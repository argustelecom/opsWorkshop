package ru.argustelecom.box.env.billing.invoice;

import lombok.Getter;
import ru.argustelecom.box.env.billing.invoice.model.ShortTermInvoice;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named("shortTermInvoiceAttributesFm")
@PresentationModel
public class ShortTermInvoiceAttributesFrameModel implements Serializable {

	private static final long serialVersionUID = 1004508711741202019L;

	@Inject
	private ShortTermInvoiceAttributeDtoTranslator tr;

	@Getter
	private ShortTermInvoiceAttributeDto invoice;

	public void preRender(ShortTermInvoice invoice) {
		this.invoice = tr.translate(invoice);
	}
}
