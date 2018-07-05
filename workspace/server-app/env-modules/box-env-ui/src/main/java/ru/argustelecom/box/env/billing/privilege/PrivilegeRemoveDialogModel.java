package ru.argustelecom.box.env.billing.privilege;

import static com.google.common.collect.Range.closed;
import static java.lang.String.format;

import java.io.Serializable;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import com.google.common.collect.Range;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.billing.invoice.LongTermInvoiceAppService;
import ru.argustelecom.box.env.billing.invoice.model.InvoiceState;
import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.privilege.PrivilegeAppService;
import ru.argustelecom.box.env.privilege.discount.DiscountAppService;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "privilegeRemoveDm")
@PresentationModel
public class PrivilegeRemoveDialogModel implements Serializable {

	@Inject
	private PrivilegeAppService privilegeAs;

	@Inject
	private DiscountAppService discountAs;

	@Inject
	private LongTermInvoiceAppService invoiceAs;

	@Setter
	private Callback<PrivilegeDto> callbackAfterRemove;

	private LongTermInvoice lastInvoice;

	@Getter
	private boolean discountIntersectionWithOpenInvoice;

	@Getter
	@Setter
	private PrivilegeDto privilege;

	public void openDialog() {
		RequestContext.getCurrentInstance().update("privilege_remove_form");
		RequestContext.getCurrentInstance().execute("PF('privilegeRemoveDlgVar').show()");

		checkDiscountIntersectionWithOpenInvoice();
	}

	public void submit() {
		switch (privilege.getType()) {
		case TRUST_PERIOD:
		case TRIAL_PERIOD:
			privilegeAs.closePrivilege(privilege.getId());
			break;
		case DISCOUNT:
			discountAs.removeDiscount(privilege.getId());
			break;
		default:
			throw new SystemException(format("'%s' privilege type can not be remove or close", privilege.getType()));
		}
		callbackAfterRemove.execute(privilege);
	}

	public void cancel() {
		callbackAfterRemove = null;
		privilege = null;
		lastInvoice = null;
		discountIntersectionWithOpenInvoice = false;
	}

	public String getDialogHeader() {
		if (privilege == null) {
			return "";
		}

		switch (privilege.getType()) {
		case TRUST_PERIOD:
			return "Досрочного закрытие доверительного периода";
		case TRIAL_PERIOD:
			return "Досрочное закрытие пробного периода";
		case DISCOUNT:
			return "Удаление скидки";
		default:
			throw new SystemException(format("'%s' privilege type can not be remove or close", privilege.getType()));
		}
	}

	private void checkDiscountIntersectionWithOpenInvoice() {
		if (!privilege.getType().equals(PrivilegeTypeRef.DISCOUNT)) {
			return;
		}
		lastInvoice = invoiceAs.findLastInvoice(privilege.getSubject().getId());
		if (lastInvoice != null && lastInvoice.getState().equals(InvoiceState.ACTIVE)) {
			Range<Date> invoiceBoundaries = closed(lastInvoice.getStartDate(), lastInvoice.getEndDate());
			boolean validFromInsideBoundaries = invoiceBoundaries.contains(privilege.getValidFrom());
			boolean validToInsideBoundaries = invoiceBoundaries.contains(privilege.getValidTo());
			discountIntersectionWithOpenInvoice = validFromInsideBoundaries || validToInsideBoundaries;
		}
	}

	private static final long serialVersionUID = -6702688211625640008L;

}