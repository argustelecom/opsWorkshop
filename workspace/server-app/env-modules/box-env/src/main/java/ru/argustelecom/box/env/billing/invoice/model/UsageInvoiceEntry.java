package ru.argustelecom.box.env.billing.invoice.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.inf.modelbase.SequenceDefinition;

@Entity
@SequenceDefinition(name = "system.gen_usage_invoice_entry_id")
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UsageInvoiceEntry extends BusinessObject {

	@Getter
	@Embedded
	private UsageInvoiceEntryContainer usageInvoiceEntryContainer;

	public UsageInvoiceEntry(Long id) {
		super(id);
	}

	public UsageInvoiceEntry(Long id, UsageInvoiceEntryContainer usageInvoiceEntryContainer) {
		super(id);
		this.usageInvoiceEntryContainer = usageInvoiceEntryContainer;
	}

	private static final long serialVersionUID = -2768635980316829182L;
}