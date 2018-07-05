package ru.argustelecom.box.env.billing.invoice.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import lombok.Builder;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.pricing.model.ProductOffering;
import ru.argustelecom.box.env.stl.Money;

@Entity
@Access(AccessType.FIELD)
public class ShortTermInvoice extends AbstractInvoice {

	private static final long serialVersionUID = -6591511824978673280L;

	@OneToMany(mappedBy = "invoice", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<InvoiceEntry> entries = new ArrayList<>();

	protected ShortTermInvoice() {
	}

	@Builder
	protected ShortTermInvoice(Long id, PersonalAccount personalAccount) {
		super(id, personalAccount);
		setClosingDate(getCreationDate());
	}

	public InvoiceEntry createEntry(Long entryId, ProductOffering productOffering) {
		InvoiceEntry entry = new InvoiceEntry(entryId, this, productOffering);
		entries.add(entry);
		entry.setAmount(productOffering.getPrice());
		return entry;
	}

	public void removeEntry(InvoiceEntry entry) {
		entries.remove(entry);
	}

	@Override
	public Money getTotalPrice() {
		return entries.stream().filter(e -> e.getAmount() != null).map(InvoiceEntry::getAmount).reduce(Money.ZERO,
				Money::add);
	}

	public List<InvoiceEntry> getEntries() {
		return Collections.unmodifiableList(entries);
	}

	public static class ShortTermInvoiceQuery extends InvoiceQuery<ShortTermInvoice> {

		public ShortTermInvoiceQuery() {
			super(ShortTermInvoice.class);
		}

	}
}