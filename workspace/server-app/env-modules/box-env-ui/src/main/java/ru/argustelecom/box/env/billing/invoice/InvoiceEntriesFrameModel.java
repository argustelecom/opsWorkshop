package ru.argustelecom.box.env.billing.invoice;

import java.io.Serializable;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ru.argustelecom.box.env.billing.invoice.model.AbstractInvoice;
import ru.argustelecom.box.env.billing.invoice.model.InvoiceEntry;
import ru.argustelecom.box.env.billing.invoice.model.ShortTermInvoice;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.box.env.pricing.model.ProductOffering;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class InvoiceEntriesFrameModel implements Serializable {

	private static final long serialVersionUID = -4709431498847160291L;

	private ShortTermInvoice invoice;

	private TreeNode entriesNode;

	public void preRender(ShortTermInvoice invoice) {
		this.invoice = invoice;
		initEntriesNode();
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private void initEntriesNode() {
		entriesNode = new DefaultTreeNode("root", null);
		invoice.getEntries().forEach(entry -> new DefaultTreeNode(new EntryViewAdapter(entry), entriesNode));
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public AbstractInvoice getInvoice() {
		return invoice;
	}

	public TreeNode getEntriesNode() {
		return entriesNode;
	}

	// *****************************************************************************************************************
	// Inner classes
	// *****************************************************************************************************************

	public static class EntryViewAdapter {

		protected ProductOffering product;
		protected AbstractPricelist pricelist;
		protected Money amount;

		public EntryViewAdapter(InvoiceEntry entry) {
			product = entry.getProductOffering();
			pricelist = entry.getProductOffering().getPricelist();
			amount = entry.getAmount();
		}

		public ProductOffering getProduct() {
			return product;
		}

		public AbstractPricelist getPricelist() {
			return pricelist;
		}

		public Money getAmount() {
			return amount;
		}

	}

}