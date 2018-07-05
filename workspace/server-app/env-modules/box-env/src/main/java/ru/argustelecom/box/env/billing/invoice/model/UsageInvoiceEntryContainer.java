package ru.argustelecom.box.env.billing.invoice.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.env.billing.bill.model.DataMapper;

@Embeddable
@Access(AccessType.FIELD)
@AttributeOverride(name = "asJson", column = @Column(name = "entry"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UsageInvoiceEntryContainer implements Serializable {

	private static final long serialVersionUID = -2744025242041733716L;

	private String asJson;

	@Transient
	private List<UsageInvoiceEntryData> invoiceEntryData = new ArrayList<>();

	public UsageInvoiceEntryContainer(List<UsageInvoiceEntryData> invoiceEntryData) {
		setUsageInvoiceEntryData(invoiceEntryData);
	}

	public void setUsageInvoiceEntryData(List<UsageInvoiceEntryData> invoiceEntryData) {
		this.invoiceEntryData = invoiceEntryData;
		updateJson();
	}

	public boolean addEntry(UsageInvoiceEntryData entry) {
		checkNotNull(entry);

		Consumer<UsageInvoiceEntryData> updateEntry = newEntry -> {
			List<UsageInvoiceEntryData> entries = getEntries();
			UsageInvoiceEntryData oldEntry = entries.get(entries.indexOf(newEntry));

			BigDecimal amount = oldEntry.getAmount().add(newEntry.getAmount());
			newEntry.setAmount(amount);

			removeEntry(oldEntry);
			addEntry(newEntry);
		};

		boolean contains = hasEntry(entry);
		if (!contains) {
			getEntries().add(entry);
			updateJson();
		} else {
			updateEntry.accept(entry);
			updateJson();
		}

		return true;
	}

	public boolean removeEntry(UsageInvoiceEntryData entry) {
		checkNotNull(entry);
		boolean removed = getEntries().remove(entry);

		if (removed) {
			updateJson();
		}

		return removed;
	}

	public void removeAllEntries() {
		getEntries().clear();
		updateJson();
	}

	public boolean hasEntry(UsageInvoiceEntryData entry) {
		checkNotNull(entry);
		return getEntries().contains(entry);
	}

	public List<UsageInvoiceEntryData> getEntries() {
		if (invoiceEntryData == null || invoiceEntryData.isEmpty()) {
			invoiceEntryData = DataMapper.unmarshalList(asJson, UsageInvoiceEntryData.class);
		}
		return invoiceEntryData;
	}

	private void updateJson() {
		asJson = DataMapper.marshal(invoiceEntryData);
	}
}
