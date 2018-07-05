package ru.argustelecom.box.env.billing.privilege;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Range.closed;
import static java.lang.String.format;
import static ru.argustelecom.box.env.billing.privilege.PrivilegeSubjectType.SUBSCRIPTION;
import static ru.argustelecom.box.env.billing.privilege.PrivilegeTypeRef.DISCOUNT;
import static ru.argustelecom.box.env.billing.privilege.PrivilegeTypeRef.TRUST_PERIOD;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.fromLocalDateTime;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.plusMillis;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.toLocalDate;
import static ru.argustelecom.system.inf.chrono.DateUtils.DATETIME_DEFAULT_PATTERN;
import static ru.argustelecom.system.inf.chrono.DateUtils.DATE_DEFAULT_PATTERN;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

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
import ru.argustelecom.box.env.privilege.discount.model.Discount;
import ru.argustelecom.box.env.privilege.model.Privilege;
import ru.argustelecom.box.inf.chrono.ChronoUtils;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "privilegeEditDm")
@PresentationModel
public class PrivilegeEditDialogModel implements Serializable {

	private static final String CREATION_DIALOG_HEADER = "Создание привилегии";
	private static final String SUMMARY = "Ошибка создания доверительного периода";
	private static final String DETAIL = "У %s уж существует привилегия в указанный интервал дат";

	@Inject
	private LongTermInvoiceAppService invoiceAs;

	@Inject
	private PrivilegeAppService privilegeAs;

	@Inject
	private DiscountAppService discountAs;

	@Inject
	private PrivilegeDtoTranslator privilegeDtoTr;

	@Getter
	@Setter
	private PrivilegeSubjectDto subject;

	@Setter
	private Callback<PrivilegeDto> callbackAfterCreation;

	@Setter
	private Callback<PrivilegeDto> callbackAfterEditing;

	@Getter
	@Setter
	private PrivilegeDto privilege;

	private Date trustPeriodMinDate;

	private LongTermInvoice lastInvoice;

	@Getter
	private LongTermInvoice lastClosedInvoice;

	@Getter
	private boolean discountIntersectionWithOpenInvoice;

	@Getter
	@Setter
	private boolean ignoreWarns;

	public void openDialog() {
		RequestContext.getCurrentInstance().update("privilege_edit_form");
		RequestContext.getCurrentInstance().execute("PF('privilegeEditDlgVar').show()");

		trustPeriodMinDate = ChronoUtils.fromLocalDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

		initPrivilege();
		initLastInvoices();
	}

	public void submit() {
		checkNotNull(subject, "Subject is required for trust period creation");

		if (!validBoundaries()) {
			return;
		}

		checkDiscountIntersectionWithOpenInvoice();

		if (isDisableCreation()) {
			return;
		}

		if (!isEditMode()) {
			PrivilegeDto result = create();
			if (result != null) {
				callbackAfterCreation.execute(result);
				cancel();
			}
		} else {
			if (!change()) {
				return;
			}
			callbackAfterEditing.execute(privilege);
			cancel();
		}
		RequestContext.getCurrentInstance().execute("PF('privilegeEditDlgVar').hide()");
	}

	public void cancel() {
		subject = null;
		callbackAfterCreation = null;
		callbackAfterEditing = null;
		privilege = null;
		discountIntersectionWithOpenInvoice = false;
		ignoreWarns = false;
		trustPeriodMinDate = new Date();
	}

	public boolean isEditMode() {
		return privilege != null && privilege.getId() != null;
	}

	public Date getMinDate() {
		return Optional.ofNullable(privilege).map(PrivilegeDto::getType).map(this::getMinDate).orElse(null);
	}

	public void onTypeChanged() {
		discountIntersectionWithOpenInvoice = false;
		ignoreWarns = false;
		if (DISCOUNT.equals(privilege.getType())) {
			checkDiscountIntersectionWithOpenInvoice();
		}
	}

	public boolean isValidFromGreatLastClosedInvoiceEndDate() {
		return lastClosedInvoice == null || privilege.getValidFrom() == null
				|| privilege.getValidFrom().after(lastClosedInvoice.getEndDate());
	}

	public void checkDiscountIntersectionWithOpenInvoice() {
		if (!Objects.equals(subject.getType(), SUBSCRIPTION)) {
			return;
		}

		if (lastInvoice != null && lastInvoice.getState().equals(InvoiceState.ACTIVE)) {
			if (privilege.getValidFrom() == null || privilege.getValidTo() == null) {
				discountIntersectionWithOpenInvoice = false;
				ignoreWarns = false;
				return;
			}
			Range<Date> invoiceBoundaries = closed(lastInvoice.getStartDate(), lastInvoice.getEndDate());
			boolean validFromInsideBoundaries = invoiceBoundaries.contains(privilege.getValidFrom());
			boolean validToInsideBoundaries = invoiceBoundaries.contains(privilege.getValidTo());
			discountIntersectionWithOpenInvoice = validFromInsideBoundaries || validToInsideBoundaries;
		}
	}

	public String getDatePattern() {
		if (privilege == null || privilege.getType() == null) {
			return DATETIME_DEFAULT_PATTERN;
		}
		return privilege.getType().equals(PrivilegeTypeRef.DISCOUNT) ? DATE_DEFAULT_PATTERN : DATETIME_DEFAULT_PATTERN;
	}

	public boolean isDisableCreation() {
		return privilege != null && DISCOUNT.equals(privilege.getType()) && discountIntersectionWithOpenInvoice
				&& !ignoreWarns;
	}

	public String getDialogHeader() {
		return !isEditMode() ? CREATION_DIALOG_HEADER : privilege.getType().getEditDlgHeader();
	}

	public boolean canCreateDiscount() {
		return subject != null && subject.getType().equals(SUBSCRIPTION);
	}

	public Date getDiscountPossibleStartDate() {
		if (lastClosedInvoice == null) {
			return null;
		}
		return plusMillis(lastClosedInvoice.getEndDate(), 1);
	}

	public boolean isReserveFunds() {
		return subject != null && SUBSCRIPTION.equals(subject.getType())
				&& subject.getProvisionTerms().isReserveFunds();
	}

	private boolean validBoundaries() {
		switch (privilege.getType()) {
		case TRUST_PERIOD:
			return validTrustPeriodBoundaries();
		case DISCOUNT:
			return validDiscountBoundaries();
		default:
			return false;
		}
	}

	private boolean validTrustPeriodBoundaries() {
		if (!checkValidFromGreatOrEqualsNow()) {
			Notification.warn("Ошибка при формировании периода", "\"Действует с\" не может быть меньше текущей даты");
			return false;
		}
		if (!checkValidFromLessOrEqualsValidTo()) {
			Notification.warn("Ошибка при формировании периода",
					"Дата начала периода не должна превышать дату окончания периода");
			return false;
		}
		return true;
	}

	private boolean validDiscountBoundaries() {
		if (!checkValidFromLessOrEqualsValidTo()) {
			Notification.warn("Ошибка при формировании периода",
					"Дата начала периода не должна превышать дату окончания периода");
			return false;
		}
		if (!checkDiscountStartIsAfterMin()) {
			Notification.warn("Ошибка при формировании периода", "\"Действует с\" не может быть меньше текущей даты ");
			return false;
		}
		return true;
	}

	private boolean checkValidFromGreatOrEqualsNow() {
		return privilege.getValidFrom().after(trustPeriodMinDate)
				|| privilege.getValidFrom().equals(trustPeriodMinDate);
	}

	private boolean checkValidFromLessOrEqualsValidTo() {
		return privilege.getValidFrom().before(privilege.getValidTo())
				|| privilege.getValidFrom().equals(privilege.getValidTo());
	}

	private boolean checkDiscountStartIsAfterMin() {
		Date minDate = getMinDate();
		if (minDate != null) {
			Date alignedMinDate = fromLocalDateTime(toLocalDate(minDate).atStartOfDay());
			return privilege.getValidFrom().after(alignedMinDate);
		}
		// null здесь может быть только если нет последнего закрытого инвойса. В этом случае нет необходимости как-то
		// ограничивать создание привилегии
		return true;
	}

	private void initPrivilege() {
		if (privilege == null) {
			privilege = new PrivilegeDto();
		}

		if (!canCreateDiscount()) {
			privilege.setType(TRUST_PERIOD);
		}
	}

	private void initLastInvoices() {
		if (subject.getType().equals(SUBSCRIPTION)) {
			lastInvoice = invoiceAs.findLastInvoice(subject.getId());
			if (lastInvoice == null || !lastInvoice.getState().equals(InvoiceState.CLOSED)) {
				lastClosedInvoice = invoiceAs.findLastClosedInvoice(subject.getId());
			} else {
				lastClosedInvoice = lastInvoice;
			}
		}
	}

	private PrivilegeDto create() {
		switch (privilege.getType()) {
		case TRUST_PERIOD:
			return createTrustPeriod();
		case DISCOUNT:
			return createDiscount();
		default:
			throw new SystemException(format("Unsupported privilege type '%s'", privilege.getType()));
		}
	}

	private boolean change() {
		switch (privilege.getType()) {
		case DISCOUNT:
			if (!checkValidFromLessOrEqualsValidTo()) {
				return false;
			}
			changeDiscount();
			break;
		default:
			throw new SystemException(format("Unsupported privilege type for edit mode '%s'", privilege.getType()));
		}
		return true;
	}

	private PrivilegeDto createTrustPeriod() {
		Privilege trustPeriod;

		switch (subject.getType()) {
		case SUBSCRIPTION:
			trustPeriod = createTrustPeriodForSubscription();
			break;
		case PERSONAL_ACCOUNT:
			trustPeriod = createTrustPeriodForPersonalAccount();
			break;
		case CUSTOMER:
			trustPeriod = createTrustPeriodForCustomer();
			break;
		default:
			throw new SystemException(format("Unsupported privilege subject '%s'", subject));
		}
		return Optional.ofNullable(trustPeriod).map(privilegeDtoTr::translate).orElse(null);
	}

	private PrivilegeDto createDiscount() {
		Discount discount = discountAs.create(subject.getId(), privilege.getValidFrom(), privilege.getValidTo(),
				privilege.getRateOfDiscount());
		return privilegeDtoTr.translate(discount);
	}

	private Privilege createTrustPeriodForSubscription() {
		if (privilegeAs.hasSubscriptionPrivilegeInPeriod(subject.getId(), privilege.getValidFrom(),
				privilege.getValidTo())) {
			Notification.error(SUMMARY, format(DETAIL, "подписки"));
			return null;
		}
		return privilegeAs.createTrustPeriodForSubscription(privilege.getValidFrom(), privilege.getValidTo(),
				subject.getId());
	}

	private Privilege createTrustPeriodForPersonalAccount() {
		if (privilegeAs.hasPersonalAccountPrivilegeInPeriod(subject.getId(), privilege.getValidFrom(),
				privilege.getValidTo())) {
			Notification.error(SUMMARY, format(DETAIL, "лицевого счета"));
			return null;
		}
		return privilegeAs.createTrustPeriodForPersonalAccount(privilege.getValidFrom(), privilege.getValidTo(),
				subject.getId());
	}

	private Privilege createTrustPeriodForCustomer() {
		if (privilegeAs.hasCustomerPrivilegeInPeriod(subject.getId(), privilege.getValidFrom(),
				privilege.getValidTo())) {
			Notification.error(SUMMARY, format(DETAIL, "клиента"));
			return null;
		}
		return privilegeAs.createTrustPeriodForCustomer(privilege.getValidFrom(), privilege.getValidTo(),
				subject.getId());
	}

	private void changeDiscount() {
		Discount changed = discountAs.changeDiscount(privilege.getId(), privilege.getValidTo(),
				privilege.getRateOfDiscount());
		privilege = privilegeDtoTr.translate(changed);
	}

	private Date getMinDate(PrivilegeTypeRef type) {
		return TRUST_PERIOD.equals(type) ? trustPeriodMinDate : getDiscountPossibleStartDate();
	}

	private static final long serialVersionUID = 4762448061604894799L;

}