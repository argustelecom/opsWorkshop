package ru.argustelecom.box.env.billing.bill;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static ru.argustelecom.box.env.billing.bill.BillParamsDto.BillContext.isFromCard;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.StreamedContent;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.billing.bill.model.Bill;
import ru.argustelecom.box.env.billing.bill.model.BillGroup;
import ru.argustelecom.box.env.billing.bill.nls.BillMessagesBundle;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.overall.nls.OverallMessagesBundle;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.page.outcome.OutcomeConstructor;
import ru.argustelecom.box.inf.page.outcome.param.IdentifiableOutcomeParam;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.chrono.DateUtils;
import ru.argustelecom.system.inf.page.PresentationModel;

@Getter
@Setter
@Named("billCreationDm")
@PresentationModel
public class BillCreationDialogModel extends BillReportGenDialog implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private OutcomeConstructor outcomeConstructor;

	@Inject
	private BillAppService billAs;

	@Inject
	private BillDtoTranslator billDtoTr;

	@Inject
	private BillReportDtoTranslator billReportDtoTr;

	private List<BillGroup> validBillGroups;
	private Callback<List<BillDto>> callbackAfterCreation;

	private BillParamsDto billParams = new BillParamsDto();
	private Step currentStep;

	public void openCreationDlg(String formId, String dlgId, String widgetVar) {
		RequestContext.getCurrentInstance().reset(formId);
		RequestContext.getCurrentInstance().update(format("%s-%s", formId, dlgId));
		RequestContext.getCurrentInstance().execute(format("PF('%s').show()", widgetVar));
	}

	public String createBill() {
		boolean validParams = validateBillPeriod() && validateBillUniqueness() && validatePostpaymentCondition();
		if (!validParams) {
			return null;
		}

		List<Bill> bills = billAs.createBill(billParams.getBillType().getId(), billParams.getBillNumber(),
				billParams.getGroupingMethod(), billParams.getGroupingObject().getId(),
				billParams.getPaymentCondition(), billParams.getBillPeriod(), billParams.getBillDate(),
				billParams.getTemplate() != null ? billParams.getTemplate().getId() : null);

		if (bills.size() == 1) {
			boolean fromCard = isFromCard(billParams.getCurrentContext());
			if (fromCard) {
				callbackAfterCreation.execute(singletonList(billDtoTr.translate(bills.get(0))));
			}

			cleanParams();
			return fromCard ? null : outcome(bills.iterator().next());
		} else {
			BillMessagesBundle messages = LocaleUtils.getMessages(BillMessagesBundle.class);
			StringBuilder builder = new StringBuilder(messages.billsSuccessfulCreated());
			bills.forEach(b -> {
				builder.append(b.getObjectName()).append(". ");
				callbackAfterCreation.execute(bills.stream().map(billDtoTr::translate).collect(Collectors.toList()));
			});
			Notification.info("", builder.toString());
		}

		return StringUtils.EMPTY;
	}

	public void createBills() {
		billAs.createBills(validBillGroups, billParams.getBillType().getId(), billParams.getBillPeriod().getType(),
				billParams.getBillPeriod().getPeriodUnit(), billParams.getPaymentCondition(),
				billParams.getBillPeriod().startDate(), billParams.getBillPeriod().endDate(), billParams.getBillDate(),
				billParams.getTemplate() != null ? billParams.getTemplate().getId() : null);
		cleanParams();
	}

	public void validateBeforeMassCreation() {
		if (!validateBillPeriod()) {
			return;
		}

		Long customerTypeId = billParams.getBillType().getCustomerType() != null
				? billParams.getBillType().getCustomerType().getId()
				: null;

		validBillGroups = billAs.findBillGroups(billParams.getGroupingMethod(), billParams.getBillType().getId(),
				billParams.getBillPeriod(), billParams.getBillDate(), billParams.getPaymentCondition(), customerTypeId,
				null, null, getProviders().stream().map(BusinessObjectDto::getId).collect(Collectors.toSet()));

		if (!validBillGroups.isEmpty()) {
			List<Bill> alreadyCreatedBills = billAs.findBills(
					validBillGroups.stream().map(BillGroup::getId).collect(Collectors.toList()),
					billParams.getGroupingMethod(), billParams.getBillPeriod().startDate(),
					billParams.getBillPeriod().endDate());
			if (alreadyCreatedBills != null) {
				validBillGroups.removeIf(billGroup -> alreadyCreatedBills.stream()
						.anyMatch(bill -> bill.getGroupId().equals(billGroup.getId())));
				billErrorReportDtoList = translateBillsToBillErrorReportDtos(billReportDtoTr, alreadyCreatedBills);
			}
		}
	}

	private boolean validateBillPeriod() {
		if (!DateUtils.before(billParams.getBillPeriod().startDate(), billParams.getBillPeriod().endDate())) {

			OverallMessagesBundle overallMessages = LocaleUtils.getMessages(OverallMessagesBundle.class);
			BillMessagesBundle billMessages = LocaleUtils.getMessages(BillMessagesBundle.class);

			FacesContext.getCurrentInstance().validationFailed();
			Notification.error(overallMessages.error(), billMessages.billInvalidPeriodViolation());
			return false;

		}
		return true;
	}

	private boolean validateBillUniqueness() {
		if (billAs.isBillAlreadyExists(billParams.getGroupingMethod(), billParams.getGroupingObject().getId(),
				billParams.getPaymentCondition(), billParams.getBillPeriod().startDate(),
				billParams.getBillPeriod().endDate())) {

			OverallMessagesBundle overallMessages = LocaleUtils.getMessages(OverallMessagesBundle.class);
			BillMessagesBundle billMessages = LocaleUtils.getMessages(BillMessagesBundle.class);

			FacesContext.getCurrentInstance().validationFailed();
			Notification.error(overallMessages.error(), billMessages.billUniqueConditionsViolation());
			return false;

		}
		return true;
	}

	private boolean validatePostpaymentCondition() {
		if (PaymentCondition.POSTPAYMENT.equals(billParams.getPaymentCondition())
				&& !new Date().after(billParams.getBillPeriod().startDate())) {

			OverallMessagesBundle overallMessages = LocaleUtils.getMessages(OverallMessagesBundle.class);
			BillMessagesBundle billMessages = LocaleUtils.getMessages(BillMessagesBundle.class);

			FacesContext.getCurrentInstance().validationFailed();
			Notification.error(overallMessages.error(), billMessages.billPostpaymentPeriodViolation());
			return false;

		}

		return true;
	}

	public StreamedContent onDownload() throws IOException {
		BillMessagesBundle messages = LocaleUtils.getMessages(BillMessagesBundle.class);
		return downloadReport(messages.nonUniqueBills(), messages.nonUniqueBillsToSend(), messages.billNumbers());
	}

	private String outcome(Bill bill) {
		return outcomeConstructor.construct(BillCardViewModel.VIEW_ID, IdentifiableOutcomeParam.of("bill", bill));
	}

	public void onCancel() {
		cleanParams();
	}

	private void cleanParams() {
		billParams = new BillParamsDto();
		currentStep = null;
	}

	public List<BusinessObjectDto<PartyRole>> getProviders() {
		return billParams.getBillType() != null ? billParams.getBillType().getProviders() : Collections.emptyList();
	}

	public String onFlowProcess(FlowEvent event) {
		String newStep = event.getNewStep();
		currentStep = Step.getStep(newStep);
		if (currentStep == Step.CONFIRM) {
			validateBeforeMassCreation();
		}
		return newStep;
	}

	public enum Step {
		CREATION("creation"), CONFIRM("confirm");

		@Getter
		private String id;

		Step(String id) {
			this.id = id;
		}

		public static Step getStep(String id) {
			return Arrays.stream(values()).filter(step -> step.getId().equals(id)).findFirst()
					.orElseThrow(() -> new IllegalArgumentException("Unknown step id"));
		}
	}

	private static final long serialVersionUID = -4743978036257499035L;

}