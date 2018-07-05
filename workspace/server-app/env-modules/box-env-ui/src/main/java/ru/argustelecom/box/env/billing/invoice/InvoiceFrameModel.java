package ru.argustelecom.box.env.billing.invoice;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toList;
import static ru.argustelecom.box.env.billing.invoice.model.InvoiceState.ACTIVE;
import static ru.argustelecom.box.env.billing.invoice.model.InvoiceState.CREATED;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.invoice.model.InvoiceState;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class InvoiceFrameModel implements Serializable {

	private static final long serialVersionUID = 3243332693967226547L;

	@Inject
	private RegularInvoiceAppService regularInvoiceAs;

	@Inject
	private RegularInvoiceDtoTranslator invoiceFrameDtoTr;

	@Inject
	private PersonalAccountDtoTranslator personalAccountDtoTr;

	@Getter
	private PersonalAccountDto personalAccount;

	@Getter
	private int frameHeight;

	@Getter
	private List<RegularInvoiceDto> invoices;

	@Getter
	@Setter
	private boolean showOnlyActive = true;

	public void preRender(PersonalAccount personalAccount, Integer frameHeight) {
		this.personalAccount = personalAccountDtoTr.translate(personalAccount);
		this.frameHeight = frameHeight;
		refresh();
	}

	public void refresh() {
		List<InvoiceState> states = showOnlyActive ? newArrayList(CREATED, ACTIVE) : newArrayList();
		//@formatter:off
		this.invoices = invoiceFrameDtoTr.translate(regularInvoiceAs.findInvoices(personalAccount.getId(), states))
				.stream()
				.sorted(comparing(RegularInvoiceDto::getId, reverseOrder()))
				.collect(toList());
		//@formatter:on
	}
}