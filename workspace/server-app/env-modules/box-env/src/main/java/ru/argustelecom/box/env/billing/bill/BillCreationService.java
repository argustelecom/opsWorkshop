package ru.argustelecom.box.env.billing.bill;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.google.common.collect.Lists;

import ru.argustelecom.box.env.billing.bill.model.AggDataHolder;
import ru.argustelecom.box.env.billing.bill.model.Bill;
import ru.argustelecom.box.env.billing.bill.model.BillAnalyticType;
import ru.argustelecom.box.env.billing.bill.model.BillAnalyticType.AnalyticCategory;
import ru.argustelecom.box.env.billing.bill.model.BillGroup;
import ru.argustelecom.box.env.billing.bill.model.BillPeriod;
import ru.argustelecom.box.env.billing.bill.model.BillType;
import ru.argustelecom.box.env.billing.bill.model.ChargesAgg;
import ru.argustelecom.box.env.billing.bill.model.ChargesRaw;
import ru.argustelecom.box.env.billing.bill.model.GroupingMethod;
import ru.argustelecom.box.env.billing.bill.model.IncomesAgg;
import ru.argustelecom.box.env.billing.bill.model.IncomesRaw;
import ru.argustelecom.box.env.billing.bill.model.RawDataHolder;
import ru.argustelecom.box.env.billing.bill.model.Summary;
import ru.argustelecom.box.env.billing.bill.nls.BillMessagesBundle;
import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.report.model.ReportModelTemplate;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.service.DomainService;
import ru.argustelecom.system.inf.exception.BusinessException;
import ru.argustelecom.system.inf.exception.SystemException;

/**
 * Сервис для создания {@linkplain Bill счёта}.
 */
@DomainService
public class BillCreationService implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private BillDataLoader loader;

	@Inject
	private BillRepository billRp;

	@Inject
	private ChargesService chargesSvc;

	@Inject
	private IncomesService incomesSvc;

	@Inject
	private AggregationService aggregationSvc;

	@Inject
	private SummaryService summarySvc;

	@Inject
	private BillAnalyticTypeRepository analyticTypeRp;

	@Inject
	private BillDateUtils billDateUtils;

	/**
	 * Создание счёта. Данный метод предназначен для создания счетов по конкретному объекту группировки:
	 * {@linkplain Contract договору} или {@linkplain ru.argustelecom.box.env.billing.account.model.PersonalAccount
	 * лицевому счёту}. Для лицевого счёта может возращать список счетов, так как на одном ЛС может быть несколько
	 * подписок от разных {@linkplain ru.argustelecom.box.env.party.model.role.Supplier поставщиков} и
	 * {@linkplain ru.argustelecom.box.env.party.model.role.Owner агентов}.
	 */
	public List<Bill> create(String number, Date creationDate, Date billDate, BillPeriod period,
			PaymentCondition paymentCondition, GroupingMethod groupingMethod, Long groupId, BillType billType,
			ReportModelTemplate template) {
		List<BillData> billDataList = createBillData(number, creationDate, billDate, period, paymentCondition,
				groupingMethod, groupId, billType, template);

		return billDataList.stream().map(this::create).collect(toList());
	}

	/**
	 * Создание счёта. Данный метод должен дергать обработчик очереди, при массовом создании счётов.
	 */
	public Bill create(BillData billData) {
		DataHolder dataHolder = createData(billData);

		//@formatter:off
		return billRp.create(
				billData.getId(),
				billData.getBillType(),
				billData.getNumber(),
				billData.getCustomer(),
				billData.getGroupingMethod(),
				billData.getGroupId(),
				billData.getPaymentCondition(),
				billData.getPeriod().getType(),
				billData.getPeriod(),
				billData.getBillDate(),
				billData.getTemplate(),
				dataHolder,
				billData.getProvider(),
				billData.getBroker()
		);
		//@formatter:on
	}

	/**
	 * Пересоздаёт сырые данные для уже существующего счёта.
	 */
	public DataHolder dataRecreation(Bill bill, Date newBillDate) {
		Date billDate = newBillDate != null ? newBillDate : bill.getDocumentDate();
		List<BillData> billDataList = createBillData(bill.getDocumentNumber(), bill.getCreationDate(), billDate,
				bill.getPeriod(), bill.getPaymentCondition(), bill.getGroupingMethod(), bill.getGroupId(),
				bill.getType(), bill.getTemplate());

		BillData billData = billDataList.stream()
				.filter(d -> Objects.equals(d.getProvider(), bill.getProvider())
						&& Objects.equals(d.getBroker(), bill.getBroker()))
				.findFirst().orElseThrow(() -> new SystemException("Could not find bill data"));

		return createData(billData);
	}

	private List<BillData> createBillData(String number, Date creationDate, Date billDate, BillPeriod period,
			PaymentCondition paymentCondition, GroupingMethod groupingMethod, Long groupId, BillType billType,
			ReportModelTemplate template) {
		BillGroupsSearchService searchSvc = BillGroupsSearchServiceUtils.lookupSearchService(groupingMethod);

		Set<Long> providerIds = new HashSet<>();

		if (GroupingMethod.CONTRACT.equals(groupingMethod)) {
			Contract contract = em.find(Contract.class, groupId);
			providerIds.add(contract.getType().getProvider().getId());
		} else {
			providerIds.addAll(billType.getProviders().stream().map(PartyRole::getId).collect(toSet()));
		}

		Set<BillAnalyticType> analyticTypes = analyticTypeRp.find(billType, period, AnalyticCategory.CHARGE);
		Date startDate = billDateUtils.findMinStartDate(analyticTypes, period, billDate, null);
		Date endDate = billDateUtils.findMaxEndDate(analyticTypes, period, billDate, null);
		List<BillGroup> billGroups = searchSvc.find(period, startDate, endDate, paymentCondition, null, null,
				groupId, providerIds);
		if (billGroups.isEmpty()) {
			BillMessagesBundle messages = LocaleUtils.getMessages(BillMessagesBundle.class);
			throw new BusinessException(messages.noChargesFoundForCreationBill());
		}

		Long templateId = template != null ? template.getId() : null;

		List<BillData> billDataList = new ArrayList<>();

		for (BillGroup group : billGroups) {
			billDataList.add(loader.load(null, number, group, billType.getId(), templateId, paymentCondition,
					creationDate, billDate, period));
		}

		return billDataList;

	}

	private DataHolder createData(BillData billData) {
		List<ChargesRaw> chargesRawList = chargesSvc.initRawData(billData);
		List<IncomesRaw> incomesRawList = incomesSvc.initRawData(billData);

		List<Long> subscriptionIdList = chargesSvc.getSubscriptionIdList(chargesRawList);
		List<ChargesAgg> chargesAggList = !chargesRawList.isEmpty() ? aggregationSvc.aggregateCharges(chargesRawList)
				: Lists.newArrayList();
		List<IncomesAgg> incomesAggList = !incomesRawList.isEmpty() ? aggregationSvc.aggregateIncomes(incomesRawList)
				: Lists.newArrayList();
		List<AggData> allAggData = Stream.concat(chargesAggList.stream(), incomesAggList.stream()).collect(toList());

		List<Summary> summaries = summarySvc.initSummaries(billData.getBillType(), allAggData);
		return DataHolder.of(new RawDataHolder(chargesRawList, incomesRawList),
				new AggDataHolder(subscriptionIdList, chargesAggList, incomesAggList, summaries));
	}

	private static final long serialVersionUID = -5989200865798034016L;

}