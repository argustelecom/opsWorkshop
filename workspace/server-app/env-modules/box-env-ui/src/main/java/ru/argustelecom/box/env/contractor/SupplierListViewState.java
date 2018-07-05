package ru.argustelecom.box.env.contractor;

import static ru.argustelecom.box.env.contractor.SupplierListViewState.SupplierFilter.BRAND_NAME;
import static ru.argustelecom.box.env.contractor.SupplierListViewState.SupplierFilter.LEGAL_NAME;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

import javax.inject.Named;

import ru.argustelecom.box.env.filter.FilterMapEntry;
import ru.argustelecom.box.env.filter.FilterViewState;
import ru.argustelecom.system.inf.page.PresentationState;


@PresentationState
@Getter
@Setter
@Named(value = "supplierListVs")
public class SupplierListViewState extends FilterViewState implements Serializable {

	private static final long serialVersionUID = 3835834237800070336L;

	@FilterMapEntry(BRAND_NAME)
	private String brandName;

	@FilterMapEntry(LEGAL_NAME)
	private String legalName;

	// *****************************************************************************************************************
	// Inner classes
	// *****************************************************************************************************************

	public static class SupplierFilter {
		public static final String BRAND_NAME = "BRAND_NAME";
		public static final String LEGAL_NAME = "LEGAL_NAME";
	}

}