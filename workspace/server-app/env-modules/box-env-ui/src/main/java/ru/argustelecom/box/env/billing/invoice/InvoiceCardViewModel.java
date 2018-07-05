package ru.argustelecom.box.env.billing.invoice;

import lombok.Getter;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static ru.argustelecom.box.env.billing.invoice.InvoiceCardViewModel.InvoiceCardType.USAGE;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;

import com.google.common.collect.Lists;

import ru.argustelecom.box.env.billing.invoice.model.AbstractInvoice;
import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.billing.invoice.model.ShortTermInvoice;
import ru.argustelecom.box.env.billing.invoice.model.UsageInvoice;
import ru.argustelecom.box.env.billing.privilege.PrivilegeDto;
import ru.argustelecom.box.env.billing.privilege.PrivilegeDtoTranslator;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@PresentationModel
@Named("invoiceCardVm")
public class InvoiceCardViewModel extends ViewModel {

	private static final long serialVersionUID = 9204940381488662712L;

	private static final Logger log = Logger.getLogger(InvoiceCardViewModel.class);

	public static final String VIEW_ID = "/views/env/billing/invoice/InvoiceCardView.xhtml";

	@Inject
	private CurrentInvoice currentInvoice;

	@Inject
	private PrivilegeDtoTranslator privilegeDtoTr;

	@Inject
	private UsageInvoiceEntryDtoTranslator usageInvoiceEntryDtoTr;

	@Getter
	private AbstractInvoice invoice;

	@Getter
	private UsageInvoiceEntryDto usageInvoiceEntriesDto;

	@Getter
	private InvoiceCardType cardType;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		refresh();
		unitOfWork.makePermaLong();
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private void refresh() {
		checkNotNull(currentInvoice.getValue(), "currentInvoice required");
		if (currentInvoice.changed(invoice)) {
			invoice = currentInvoice.getValue();
			log.debugv("postConstruct. invoice_id={0}", invoice.getId());
			cardType = InvoiceCardType.getCardType(invoice);

			if (cardType.equals(USAGE)) {
				usageInvoiceEntriesDto = usageInvoiceEntryDtoTr.translate(((UsageInvoice) invoice).getEntriesHolder());
			}
		}
	}

	public List<PrivilegeDto> getPrivileges() {
		if (cardType.equals(InvoiceCardType.LONG_TERM)) {
			LongTermInvoice invoice = (LongTermInvoice) this.invoice;
			List<PrivilegeDto> allPrivileges = ofNullable(invoice.getPrivilege()).map(privilegeDtoTr::translate)
					.map(Lists::newArrayList).orElseGet(Lists::newArrayList);
			allPrivileges.addAll(invoice.getDiscounts().stream().map(privilegeDtoTr::translate).collect(toList()));
			return allPrivileges;
		}
		return null;
	}

	public enum InvoiceCardType {
		SHORT_TERM,
		LONG_TERM,
		USAGE;

		public static InvoiceCardType getCardType(AbstractInvoice invoice) {
			if (invoice instanceof ShortTermInvoice) {
				return SHORT_TERM;
			} else if (invoice instanceof LongTermInvoice) {
				return LONG_TERM;
			} else if (invoice instanceof UsageInvoice) {
				return USAGE;
			} else {
				throw new SystemException("Unsupported Invoice");
			}
		}

	}
}