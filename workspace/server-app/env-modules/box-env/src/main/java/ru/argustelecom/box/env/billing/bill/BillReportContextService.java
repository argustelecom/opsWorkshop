package ru.argustelecom.box.env.billing.bill;

import static java.lang.Boolean.parseBoolean;
import static java.lang.System.getProperty;
import static ru.argustelecom.box.env.barcode.ST00012QrCodeDataFormatter.ST00012QrCodeItem.FIRST_NAME;
import static ru.argustelecom.box.env.barcode.ST00012QrCodeDataFormatter.ST00012QrCodeItem.LAST_NAME;
import static ru.argustelecom.box.env.barcode.ST00012QrCodeDataFormatter.ST00012QrCodeItem.MIDDLE_NAME;
import static ru.argustelecom.box.env.barcode.ST00012QrCodeDataFormatter.ST00012QrCodeItem.PAYER_ADDRESS;
import static ru.argustelecom.box.env.barcode.ST00012QrCodeDataFormatter.ST00012QrCodeItem.PAYM_PERIOD;
import static ru.argustelecom.box.env.barcode.ST00012QrCodeDataFormatter.ST00012QrCodeItem.PERS_ACC;
import static ru.argustelecom.box.env.barcode.ST00012QrCodeDataFormatter.ST00012QrCodeItem.PURPOSE;
import static ru.argustelecom.box.env.barcode.ST00012QrCodeDataFormatter.ST00012QrCodeItem.SUM;
import static ru.argustelecom.box.env.billing.bill.BillReportContextService.BillReportContextBands.BarCode;
import static ru.argustelecom.box.env.billing.bill.BillReportContextService.BillReportContextBands.Bill;
import static ru.argustelecom.box.env.billing.bill.BillReportContextService.BillReportContextBands.BillEntries;
import static ru.argustelecom.box.env.billing.bill.BillReportContextService.BillReportContextBands.Owner;
import static ru.argustelecom.box.env.billing.bill.model.ChargesType.USAGE;
import static ru.argustelecom.box.env.party.model.role.Owner.QR_CODE_PATTERN_ENABLED;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.val;
import ru.argustelecom.box.env.address.model.AddressRdo;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.barcode.BarcodeGenerator;
import ru.argustelecom.box.env.barcode.QrCodeDataFormatter;
import ru.argustelecom.box.env.barcode.QrCodePreference;
import ru.argustelecom.box.env.barcode.ST00012QrCodeDataFormatter;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.account.model.PersonalAccountRdo;
import ru.argustelecom.box.env.billing.bill.model.BarCodeRdo;
import ru.argustelecom.box.env.billing.bill.model.Bill;
import ru.argustelecom.box.env.billing.bill.model.BillEntryRdo;
import ru.argustelecom.box.env.billing.bill.model.BillRdo;
import ru.argustelecom.box.env.billing.bill.model.GroupingMethod;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.commodity.telephony.model.Option;
import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.contract.model.ContractRdo;
import ru.argustelecom.box.env.party.OwnerRepository;
import ru.argustelecom.box.env.party.model.PersonRdo;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.party.model.role.OwnerRdo;
import ru.argustelecom.box.env.product.model.AbstractProductType;
import ru.argustelecom.box.env.report.api.ReportContext;
import ru.argustelecom.box.env.report.api.data.ReportDataImage;
import ru.argustelecom.box.env.report.api.data.ReportDataImage.ImageFormat;
import ru.argustelecom.box.env.report.api.data.ReportDataList;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.inf.service.DomainService;
import ru.argustelecom.system.inf.chrono.DateUtils;
import ru.argustelecom.system.inf.chrono.TZ;

@DomainService
public class BillReportContextService implements Serializable {

	private static final long serialVersionUID = -4916814122680113783L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private BarcodeGenerator barcodeGenerator;

	@Inject
	private OwnerRepository ownerRepository;

	@Inject
	private BillAnalyticTypeRepository billAnalyticTypeRp;

	public void fillReportContext(Bill bill, ReportContext reportContext) {

		AtomicLong counter = new AtomicLong(10);

		//@formatter:off
		ReportDataList<BillEntryRdo> billEntriesRdos = bill.getAggDataContainer().getDataHolder().getBillEntries()
				.stream()
				.map(billEntry -> {
					val billAnalyticType = billAnalyticTypeRp.findBillAnalyticTypeBy(billEntry.getAnalyticTypeId());
					String subjectName;
					String subjectDescription;

					if (USAGE.equals(billAnalyticType.getChargesType())) {
						Option<?, ?> option = em.find(Option.class, billEntry.getSubjectId());
						subjectName = option.getObjectName();
						subjectDescription = option.getType().getDescription();
					}
					else {
						AbstractProductType product = em.find(AbstractProductType.class, billEntry.getSubjectId());
						subjectName = product.getObjectName();
						subjectDescription = product.getDescription();
					}

					return BillEntryRdo.builder()
							.id(counter.getAndIncrement())
							.subjectName(subjectName)
							.subjectDescription(subjectDescription)
							.amountWithTax(billEntry.getSum())
							.amountWithoutTax(billEntry.getSumWithoutTax())
							.taxAmount(billEntry.getTax())
						.build();
				})
				.collect(Collectors.toCollection(ReportDataList::new));
		//@formatter:on

		BillRdo billRdo = createReportData(bill);

		Owner principal = ownerRepository.findPrincipal();
		OwnerRdo ownerRdo = principal != null ? principal.createReportData() : null;

		reportContext.put(Bill.toString(), billRdo);
		reportContext.put(BillEntries.toString(), billEntriesRdos);
		reportContext.put(Owner.toString(), ownerRdo);
		if (parseBoolean(getProperty(QR_CODE_PATTERN_ENABLED))) {
			reportContext.put(BarCode.toString(), createBarCodeReportData(bill, billRdo));
		}
	}

	public BillRdo createReportData(Bill bill) {
		String period = String.format("%s - %s",
				DateUtils.format(bill.getStartDate(), DateUtils.DATE_DEFAULT_PATTERN, TZ.getServerTimeZone()),
				DateUtils.format(bill.getEndDate(), DateUtils.DATE_DEFAULT_PATTERN, TZ.getServerTimeZone()));

		//@formatter:off
		ReportDataList<AddressRdo> addressesRdo = bill.getAggDataContainer().getDataHolder().getSubscriptionIdList()
				.stream()
				.map(subsId -> em.getReference(Subscription.class, subsId))
				.flatMap(subscription -> subscription.getLocations().stream())
				.distinct()
				.map(Location::createReportData)
				.collect(Collectors.toCollection(ReportDataList::new));
		//@formatter:on

		AddressRdo firstAddressRdo = addressesRdo.stream().min(Comparator.comparing(AddressRdo::getId)).orElse(null);
		DecimalFormat df = new DecimalFormat(BillRdo.MONEY_FORMAT_PATTERN);
		Map<String, String> analytics = bill.getAggDataContainer().getDataHolder().getAnalytics().stream()
				.collect(Collectors.toMap(AggData::getKeyword,
						aggData -> df.format(new Money(aggData.getSum()).getRoundAmount())));

		String addressesAsString = addressesRdo.stream().map(AddressRdo::getFullName).collect(Collectors.joining(", "));

		PersonalAccountRdo personalAccount = null;
		ContractRdo contract = null;
		if (bill.getGroupingMethod().equals(GroupingMethod.PERSONAL_ACCOUNT)) {
			personalAccount = em.getReference(PersonalAccount.class, bill.getGroupId()).createReportData();
		} else {
			contract = em.getReference(Contract.class, bill.getGroupId()).createReportData();
		}

		//@formatter:off
		return BillRdo.builder()
					.id(bill.getId())
					.number(bill.getDocumentNumber())
					.creationDate(bill.getCreationDate())
					.billDate(bill.getDocumentDate())
					.personalAccount(personalAccount)
					.contract(contract)
					.periodStartDate(bill.getStartDate())
					.periodEndDate(bill.getEndDate())
					.period(period)
					.customer(bill.getCustomer().createReportData())
					.address(firstAddressRdo)
					.addresses(addressesRdo)
					.addressesAsString(addressesAsString)
					.provider(bill.getProvider().createReportData())
					.broker(bill.getBroker() != null ? bill.getBroker().createReportData() : null)
					.analytics(analytics)
					.currentBillingPeriodChargesWithoutTax(bill.getAmountWithoutTax().getRoundAmount())
					.currentBillingPeriodChargesWithTax(bill.getAmountWithTax().getRoundAmount())
					.currentBillingPeriodTaxAmount(bill.getTaxAmount().getRoundAmount())
					.totalAmountToPay(bill.getTotalAmount().getRoundAmount())
					.discountAmount(bill.getDiscountAmount().getRoundAmount())
				.build();
		//@formatter:on
	}

	private static final String DEFAULT_PURPOSE = "Оплата услуг связи";

	private BarCodeRdo createBarCodeReportData(Bill bill, BillRdo billRdo) {
		QrCodeDataFormatter qrCodeFormatter = new ST00012QrCodeDataFormatter();

		Owner owner = bill.getBroker() != null ? bill.getBroker() : (Owner) initializeAndUnproxy(bill.getProvider());
		//@formatter:off
		owner.parseQrCodePattern().forEach((item, pair) ->
				qrCodeFormatter.put(
						item.getKeyword(),
						owner.getCharacteristicValue(pair.getKey(), pair.getValue()),
						item.isRequired()
				)
		);
		//@formatter:on

		PersonRdo person = billRdo.getCustomer().getPerson();
		BigDecimal toPayInKopecks = billRdo.getTotalAmountToPay().multiply(BigDecimal.valueOf(100)).setScale(0,
				BigDecimal.ROUND_UP);

		if (billRdo.getPersonalAccount() != null) {
			qrCodeFormatter.put(PERS_ACC.getKeyword(), billRdo.getPersonalAccount().getNumber(), PERS_ACC.isRequired());
		}
		qrCodeFormatter.put(SUM.getKeyword(), toPayInKopecks.toString(), SUM.isRequired());
		qrCodeFormatter.put(PURPOSE.getKeyword(), DEFAULT_PURPOSE, PURPOSE.isRequired());
		if (person != null) {
			qrCodeFormatter.put(LAST_NAME.getKeyword(), person.getLastName(), LAST_NAME.isRequired());
			qrCodeFormatter.put(FIRST_NAME.getKeyword(), person.getFirstName(), FIRST_NAME.isRequired());
			qrCodeFormatter.put(MIDDLE_NAME.getKeyword(), person.getSecondName(), MIDDLE_NAME.isRequired());
		}
		qrCodeFormatter.put(PAYM_PERIOD.getKeyword(), billRdo.getPeriod(), PAYM_PERIOD.isRequired());
		if (billRdo.getAddress() != null) {
			qrCodeFormatter.put(PAYER_ADDRESS.getKeyword(), billRdo.getAddress().getFullName(), PAYER_ADDRESS.isRequired());
		}

		ByteArrayOutputStream qrCodeOs = barcodeGenerator.generate(qrCodeFormatter, new QrCodePreference(230, 230));
		return new BarCodeRdo(1L, ReportDataImage.of(ImageFormat.JPEG, 230, 230, qrCodeOs.toByteArray()));
	}

	public enum BillReportContextBands {

		Bill, BillEntries, Owner, BarCode;

	}

}