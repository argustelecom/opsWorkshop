package ru.argustelecom.box.env.billing.invoice;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.account.PersonalAccountAppService;
import ru.argustelecom.box.env.billing.invoice.nls.InvoiceMessagesBundle;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.pricing.PricelistAppService;
import ru.argustelecom.box.env.pricing.ProductOfferingAppService;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.page.PresentationModel;

import static java.util.stream.Collectors.toList;

@PresentationModel
public class InvoiceCreationDialogModel implements Serializable {

	private static final long serialVersionUID = 8748429955563703323L;

	@Inject
	private ShortTermInvoiceAppService shortTermInvoiceAs;

	@Inject
	private PricelistAppService pricelistAs;

	@Inject
	private ProductOfferingAppService productOfferingAs;

	@Inject
	private PersonalAccountAppService personalAccountAs;

	@Inject
	private MeasuredProductOfferingDtoTranslator measuredProductOfferingDtoTr;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	private List<BusinessObjectDto<AbstractPricelist>> priceLists;

	@Getter
	private List<MeasuredProductOfferingDto> entries = new ArrayList<>();

	@Getter
	private List<MeasuredProductOfferingDto> priceListEntries;

	@Getter
	@Setter
	private BusinessObjectDto<AbstractPricelist> selectedPriceList;

	@Getter
	private PersonalAccountDto personalAccount;

	@Getter
	private Money availableAmount;

	@Getter
	private Money totalAmount;

	@Getter
	private Date currentDate;

	@Getter
	@Setter
	private MeasuredProductOfferingDto selectedEntry;

	public void setPersonalAccount(PersonalAccountDto personalAccount) {
		this.personalAccount = personalAccount;
		this.availableAmount = personalAccountAs.getAvailableBalance(personalAccount.getId());
	}

	public List<BusinessObjectDto<AbstractPricelist>> getPriceLists() {
		if (priceLists == null) {
			priceLists = businessObjectDtoTr.translate(pricelistAs.findActivePricelistsWithNonRecurrentProductsAndSuitableForCustomers(new Date(),
					personalAccount.getCustomer().getId()));
		}
		return priceLists;
	}

	public void onPriceListSelection() {
		if (selectedPriceList != null) {
			priceListEntries = measuredProductOfferingDtoTr.translate(productOfferingAs.getNonRecurrentProductEntries(selectedPriceList.getId()));
			priceListEntries.removeAll(entries);
		} else {
			priceListEntries = null;
		}
	}

	public void init() {
		currentDate = new Date();
		totalAmount = Money.ZERO;
	}

	public void create() {
		shortTermInvoiceAs.createInvoice(personalAccount.getId(), entries.stream()
				.map(MeasuredProductOfferingDto::getId).collect(toList()));;
		reset();
	}

	public void cancel() {
		reset();
	}

	private void reset() {
		personalAccount = null;
		currentDate = null;
		totalAmount = null;
		selectedPriceList = null;
		selectedEntry = null;
		priceListEntries = null;
		entries = new ArrayList<>();
	}

	public void addEntry() {
		if (selectedEntry != null) {
			if (getActualAvailableAmount().compareTo(selectedEntry.getPrice()) != -1) {
				entries.add(selectedEntry);
				totalAmount = totalAmount.add(selectedEntry.getPrice());
				priceListEntries.remove(selectedEntry);
			} else {
				InvoiceMessagesBundle invoiceMb = LocaleUtils.getMessages(InvoiceMessagesBundle.class);
				Notification.error(invoiceMb.cannotAddEntry(selectedEntry.getObjectName()),
						invoiceMb.insufficientFunds(getActualAvailableAmount().toString(), totalAmount.toString()));
			}
		}
	}

	public void remove(MeasuredProductOfferingDto entry) {
		entries.remove(entry);
		totalAmount = totalAmount.subtract(entry.getPrice());
		priceListEntries.add(entry);
	}

	public String styleClassForEntryPrice(MeasuredProductOfferingDto entry) {
		return getActualAvailableAmount().compareTo(entry.getPrice()) != -1 ? "m-green" : "m-red";
	}

	private Money getActualAvailableAmount() {
		return availableAmount.subtract(totalAmount).add(personalAccount.getThreshold().negate());
	}

}