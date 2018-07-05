package ru.argustelecom.box.env.billing.bill;

import static java.lang.String.format;
import static ru.argustelecom.box.inf.queue.api.QueueProducer.Priority.MEDIUM;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.bill.dto.AdditionBillInfoDto;
import ru.argustelecom.box.env.billing.bill.dto.AdditionBillInfoDtoTranslator;
import ru.argustelecom.box.env.billing.bill.dto.BillAttributesDto;
import ru.argustelecom.box.env.billing.bill.dto.BillAttributesDtoTranslator;
import ru.argustelecom.box.env.billing.bill.dto.BillAttributesFromBillHistoryDtoTranslator;
import ru.argustelecom.box.env.billing.bill.dto.BillHistoryDtoTranslator;
import ru.argustelecom.box.env.billing.bill.dto.BillHistoryItemDto;
import ru.argustelecom.box.env.billing.bill.dto.ChargesDto;
import ru.argustelecom.box.env.billing.bill.dto.ChargesDtoTranslator;
import ru.argustelecom.box.env.billing.bill.dto.ReportModelTemplateDto;
import ru.argustelecom.box.env.billing.bill.model.Bill;
import ru.argustelecom.box.env.billing.bill.model.BillAnalyticType;
import ru.argustelecom.box.env.billing.bill.model.BillAnalyticType.AnalyticCategory;
import ru.argustelecom.box.env.billing.bill.model.BillGroup;
import ru.argustelecom.box.env.billing.bill.model.BillHistoryItem;
import ru.argustelecom.box.env.billing.bill.model.BillPeriod;
import ru.argustelecom.box.env.billing.bill.model.BillPeriodType;
import ru.argustelecom.box.env.billing.bill.model.BillType;
import ru.argustelecom.box.env.billing.bill.model.GroupingMethod;
import ru.argustelecom.box.env.billing.bill.queue.BillCreationContext;
import ru.argustelecom.box.env.billing.bill.queue.BillCreationHandler;
import ru.argustelecom.box.env.billing.bill.queue.BillRecalcContext;
import ru.argustelecom.box.env.billing.bill.queue.BillRecalcHandler;
import ru.argustelecom.box.env.billing.bill.queue.BillSendService;
import ru.argustelecom.box.env.contact.EmailContact;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.dto2.DefaultDtoConverterUtils;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.env.report.model.ReportModelTemplate;
import ru.argustelecom.box.env.stl.EmailAddress;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.inf.login.EmployeePrincipal;
import ru.argustelecom.box.inf.queue.api.QueueProducer;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

@ApplicationService
public class BillAppService implements Serializable {

	private final static EntityConverter entityConverter = new EntityConverter();
	public static final String BILL_QUEUE_ID_PREFIX = "BILL_";

	@PersistenceContext
	private EntityManager em;

	@Inject
	private BillCreationService billCreationSvc;

	@Inject
	private BillRepository billRp;

	@Inject
	private BillAttributesDtoTranslator billAttributesDtoTr;

	@Inject
	private QueueProducer queueProducer;

	@Inject
	private IdSequenceService idSequenceService;

	@Inject
	private ChargesDtoTranslator chargesDtoTr;

	@Inject
	private AdditionBillInfoDtoTranslator additionBillInfoDtoTr;

	@Inject
	private BillHistoryDtoTranslator billHistoryDtoTr;

	@Inject
	private BillAttributesFromBillHistoryDtoTranslator billAttributesFromBillHistoryDtoTr;

	@Inject
	private BillHistoryRepository billHistoryRp;

	@Inject
	private BillService billService;

	@Inject
	private PrefTableRepository prefTableRp;

	@Inject
	private BillSendService billSendSvc;

	@Inject
	private BillAnalyticTypeRepository analyticTypeRp;

	@Inject
	private BillDateUtils billDateUtils;

	public BillAttributesDto getBillAttributesDtoFromBill(String billRef) {
		return getBillAttributesDto(entityConverter.convertToObject(Bill.class, billRef));
	}

	public BillAttributesDto getBillAttributesDtoFromBillHistory(String billHistoryRef) {
		return billAttributesFromBillHistoryDtoTr.translate(getBillHistoryItem(billHistoryRef));
	}

	private BillAttributesDto getBillAttributesDto(Bill bill) {
		return billAttributesDtoTr.translate(bill);
	}

	public ChargesDto getChargesDtoFromBill(String billRef) {
		return getChargesDto(getBill(billRef));
	}

	public ChargesDto getChargesDtoFromBillHistory(String billHistoryRef) {
		return getChargesDto(getBillHistoryItem(billHistoryRef).getBill());
	}

	private ChargesDto getChargesDto(Bill bill) {
		return chargesDtoTr.translate(bill.getAggDataContainer().getDataHolder().getChargesAggList());
	}

	public AdditionBillInfoDto getAdditionBillInfoDtoFromBill(String billRef) {
		return additionBillInfoDtoTr.translate(getBill(billRef).getAggDataContainer());
	}

	public AdditionBillInfoDto getAdditionBillInfoDtoFromBillHistory(String billHistoryRef) {
		return additionBillInfoDtoTr.translate(getBillHistoryItem(billHistoryRef).getAggDataContainer());
	}

	public List<BillHistoryItemDto> getBillHistoryItemDtosFromBillHistory(String billHistoryRef) {
		return DefaultDtoConverterUtils.translate(billHistoryDtoTr,
				billHistoryRp.find(getBillHistoryItem(billHistoryRef).getBill()));
	}

	public List<BillHistoryItemDto> getBillHistoryItemDtosFromBill(String billRef) {
		return DefaultDtoConverterUtils.translate(billHistoryDtoTr, billHistoryRp.find(getBill(billRef)));
	}

	public void recalculateBill(String billRef, Date billDate) {
		billService.recalculateBill(getBill(billRef), billDate);
	}

	public void recalculateBill(Long billId, Date billDate, Long employeeId) {
		checkRequiredArgument(billId, "billId");
		checkRequiredArgument(billDate, "billDate");
		checkRequiredArgument(employeeId, "employeeId");

		Bill bill = em.find(Bill.class, billId);
		Employee employee = em.find(Employee.class, employeeId);
		billService.recalculateBill(bill, billDate, employee);
	}

	public void changeBillNumber(String billRef, String number) {
		billService.changeNumber(getBill(billRef), number);
	}

	public void changeBillTemplate(String billRef, ReportModelTemplateDto reportModelTemplateDto) {
		getBill(billRef).setTemplate(Optional.ofNullable(reportModelTemplateDto)
				.map(dto -> em.find(ReportModelTemplate.class, dto.getId())).orElse(null));
	}

	public List<Bill> findBills(List<Long> groupIds, GroupingMethod groupingMethod, Date startDate, Date endDate) {
		return billRp.findBills(groupIds, groupingMethod, startDate, endDate);
	}

	public boolean isBillAlreadyExists(GroupingMethod groupingMethod, Long groupId, PaymentCondition paymentCondition,
			Date startDate, Date endDate) {
		return billRp.isBillAlreadyExists(groupingMethod, groupId, paymentCondition, startDate, endDate);
	}

	public List<Bill> createBill(Long billTypeId, String number, GroupingMethod groupingMethod, Long groupId,
			PaymentCondition paymentCondition, BillPeriod period, Date billDate, Long reportTemplateId) {
		BillType billType = em.find(BillType.class, billTypeId);
		ReportModelTemplate reportModelTemplate = reportTemplateId != null
				? em.find(ReportModelTemplate.class, reportTemplateId)
				: null;
		return billCreationSvc.create(number, new Date(), billDate, period, paymentCondition, groupingMethod, groupId,
				billType, reportModelTemplate);
	}

	public void createBills(List<BillGroup> billGroups, Long billTypeId, BillPeriodType periodType,
			PeriodUnit periodUnit, PaymentCondition paymentCondition, Date startDate, Date endDate, Date billDate,
			Long templateId) {
		billGroups.forEach(billGroup -> {
			Long id = idSequenceService.nextValue(Bill.class);
			//@formatter:off
			BillCreationContext context
					= BillCreationContext.builder()
					.id(id)
					.paymentCondition(paymentCondition)
					.billGroup(billGroup)
					.periodType(periodType)
					.periodUnit(periodUnit)
					.periodStartDate(startDate)
					.periodEndDate(endDate)
					.billDate(billDate)
					.billCreationDate(new Date())
					.billTypeId(billTypeId)
					.templateId(templateId)
					.build();
			//@formatter:on
			queueProducer.schedule(BILL_QUEUE_ID_PREFIX + id, null, MEDIUM, new Date(),
					BillCreationHandler.HANDLER_NAME, context);
		});
	}

	public void send(List<Long> notSentBillIds, Long batchSize, String senderName, Long interval, Date sendDate) {
		send(notSentBillIds, batchSize, senderName, interval, sendDate, false);
	}

	public void send(List<Long> billIds, Long batchSize, String senderName, Long interval, Date sendDate,
			boolean forcedSending) {
		prefTableRp.createOrSetSenderName(senderName);
		billSendSvc.scheduleSendingPlansForBills(billIds, senderName, batchSize, interval, sendDate, forcedSending);
	}

	public List<Bill> findBillsByCustomer(Long customerId) {
		return billRp.findByCustomer(em.find(Customer.class, customerId));
	}

	public List<BillGroup> findBillGroups(GroupingMethod groupingMethod, Long billTypeId, BillPeriod billPeriod,
			Date billDate, PaymentCondition paymentCondition, Long customerTypeId, Long customerId, Long groupId,
			Set<Long> providerIds) {
		BillGroupsSearchService searchSvc = BillGroupsSearchServiceUtils.lookupSearchService(groupingMethod);
		CustomerType customerType = customerTypeId != null ? em.find(CustomerType.class, customerTypeId) : null;
		BillType billType = em.find(BillType.class, billTypeId);
		Set<BillAnalyticType> analyticTypes = analyticTypeRp.find(billType, billPeriod, AnalyticCategory.CHARGE);
		Date start = billDateUtils.findMinStartDate(analyticTypes, billPeriod, billDate, null);
		Date end = billDateUtils.findMaxEndDate(analyticTypes, billPeriod, billDate, null);
		return searchSvc.find(billPeriod, start, end, paymentCondition, customerType, customerId, groupId, providerIds);
	}

	public void scheduleRecalcAndSendBills(List<Long> billIds, Date billDate, boolean needSend, String senderName,
			Date sendDate) {
		billIds.forEach(billId -> {
			BillRecalcContext context = new BillRecalcContext(billId, billDate,
					EmployeePrincipal.instance().getEmployeeId(), needSend, senderName, sendDate);
			String queueId = format("RECALC_BILL_%d", billId);
			queueProducer.remove(queueId);
			queueProducer.schedule(queueId, null, MEDIUM, new Date(), BillRecalcHandler.HANDLER_NAME, context);
		});
	}

	public List<Bill> findBills(Long billTypeId, Date startDate, Date endDate) {
		return billRp.findBills(em.find(BillType.class, billTypeId), startDate, endDate);
	}

	/**
	 * Возвращает основой email адрес клиента, для счёта.
	 */
	public EmailAddress findMainEmail(Long billId) {
		Customer customer = em.find(Bill.class, billId).getCustomer();
		Optional<EmailContact> emailContact = Optional.ofNullable(customer.getMainEmail());
		return emailContact.map(EmailContact::getValue).orElse(null);
	}

	private Bill getBill(String billRef) {
		return entityConverter.convertToObject(Bill.class, billRef);
	}

	private BillHistoryItem getBillHistoryItem(String billHistoryRef) {
		return entityConverter.convertToObject(BillHistoryItem.class, billHistoryRef);
	}

	private static final long serialVersionUID = -249402310675694354L;

}
