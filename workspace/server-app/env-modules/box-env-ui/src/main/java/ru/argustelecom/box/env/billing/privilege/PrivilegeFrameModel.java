package ru.argustelecom.box.env.billing.privilege;

import static java.lang.String.format;
import static ru.argustelecom.box.env.billing.privilege.PrivilegeTypeRef.DISCOUNT;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.collect.Ordering;

import lombok.Getter;
import ru.argustelecom.box.env.billing.invoice.LongTermInvoiceAppService;
import ru.argustelecom.box.env.privilege.PrivilegeAppService;
import ru.argustelecom.box.env.privilege.discount.DiscountAppService;
import ru.argustelecom.box.env.privilege.discount.model.Discount;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "privilegeFm")
@PresentationModel
public class PrivilegeFrameModel implements Serializable {

	@Inject
	private LongTermInvoiceAppService invoiceAs;

	@Inject
	private PrivilegeAppService privilegeAs;

	@Inject
	private DiscountAppService discountAs;

	@Inject
	private PrivilegeDtoTranslator privilegeDtoTr;

	@Getter
	private PrivilegeSubjectDto subject;

	@Getter
	private List<PrivilegeDto> privileges;

	@Getter
	private List<PrivilegeDto> history;

	public void preRender(PrivilegeSubjectDto subject) {
		this.subject = subject;
		Date now = new Date();
		initPrivileges(now);
		// TODO изначально планировалось загружать историю по требованию, но пока сделаем так из-за:
		// 1. BOX-2130
		// 2. dynamic true для overlayPanel, в которой находится история, не работает по непонятным причинам и история
		// все равно загружается при загрузке страницы
		initHistory(now);
	}

	public Callback<PrivilegeDto> getCallbackAfterCreation() {
		return privilege -> {
			privileges.add(privilege);
			initHistory(new Date());
		};
	}

	public Callback<PrivilegeDto> getCallbackAfterEditing() {
		return privilege -> {
			privileges.remove(privilege);
			privileges.add(privilege);
		};
	}

	public Callback<PrivilegeDto> getCallbackAfterRemove() {
		return privilege -> privileges.remove(privilege);
	}

	public boolean hasPrivilege() {
		return !privileges.isEmpty();
	}

	public String getRemoveButtonTitle(PrivilegeDto privilege) {
		switch (privilege.getType()) {
		case TRUST_PERIOD:
		case TRIAL_PERIOD:
			return "Закрыть досрочно";
		case DISCOUNT:
			return "Удалить скидку";
		default:
			throw new SystemException(format("'%s' privilege type can not be remove or close", privilege.getType()));
		}
	}

	public boolean canRemoveDiscount(PrivilegeDto discount) {
		return discount.getType().equals(DISCOUNT) && invoiceAs.doesDiscountHaveInvoices(discount.getId());
	}

	private void initPrivileges(Date now) {
		privileges = loadPrivileges();
		privileges.removeIf(p -> p.getValidTo().before(now));
		privileges.sort(new ByPrivilegeSubjectComparator().thenComparing(new ByPrivilegeValidFromComparator()));
	}

	private void initHistory(Date now) {
		history = loadPrivileges();
		history.removeIf(p -> p.getValidTo().after(now));
		history.sort(new ByPrivilegeValidFromComparator());
	}

	private List<PrivilegeDto> loadPrivileges() {
		List<PrivilegeDto> allPrivileges;
		switch (subject.getType()) {
		case SUBSCRIPTION:
			allPrivileges = privilegeDtoTr.translate(privilegeAs.findPrivilegesBySubscription(subject.getId()));
			List<Discount> discounts = discountAs.findDiscounts(subject.getId());
			allPrivileges.addAll(discounts.stream().map(privilegeDtoTr::translate).collect(Collectors.toList()));
			break;
		case PERSONAL_ACCOUNT:
			allPrivileges = privilegeDtoTr.translate(privilegeAs.findPrivilegesByPersonalAccount(subject.getId()));
			break;
		case CUSTOMER:
			allPrivileges = privilegeDtoTr.translate(privilegeAs.findPrivilegesByCustomer(subject.getId()));
			break;
		default:
			throw new SystemException(format("Unsupported subject type '%s'", subject.getType()));
		}
		return allPrivileges;
	}

	private static final long serialVersionUID = -2998447603538068397L;

	private static class ByPrivilegeValidFromComparator implements Comparator<PrivilegeDto> {

		@Override
		public int compare(PrivilegeDto privilege1, PrivilegeDto privilege2) {
			return privilege2.getValidFrom().compareTo(privilege1.getValidFrom());
		}

	}

	private static class ByPrivilegeSubjectComparator implements Comparator<PrivilegeDto> {

		private static final Ordering<PrivilegeSubjectType> BY_SUBJECT = Ordering.explicit(
				PrivilegeSubjectType.SUBSCRIPTION, PrivilegeSubjectType.PERSONAL_ACCOUNT,
				PrivilegeSubjectType.CUSTOMER);

		@Override
		public int compare(PrivilegeDto privilege1, PrivilegeDto privilege2) {
			return BY_SUBJECT.compare(privilege1.getSubject().getType(), privilege2.getSubject().getType());
		}

	}

}