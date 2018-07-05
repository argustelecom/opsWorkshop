package ru.argustelecom.box.env.contractor;

import static ru.argustelecom.box.env.contractor.SupplierListViewState.SupplierFilter.BRAND_NAME;
import static ru.argustelecom.box.env.contractor.SupplierListViewState.SupplierFilter.LEGAL_NAME;

import java.util.Map;

import javax.inject.Inject;

import ru.argustelecom.box.env.BaseEQConvertibleDtoFilterModel;
import ru.argustelecom.box.env.party.model.role.Supplier;
import ru.argustelecom.box.env.party.model.role.Supplier.SupplierQuery;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class SupplierListFilterModel extends BaseEQConvertibleDtoFilterModel<SupplierQuery> {

	@Inject
	private SupplierListViewState supplierListViewState;

	@Override
	@SuppressWarnings({ "unchecked", "ConstantConditions" })
	public void buildPredicates(SupplierQuery supplierQuery) {
		Map<String, Object> filterMap = supplierListViewState.getFilterMap();
		for (Map.Entry<String, Object> filterEntry : filterMap.entrySet()) {
			if (filterEntry != null) {
				switch (filterEntry.getKey()) {
				case BRAND_NAME:
					addPredicate(supplierQuery.byBrandName(((String) filterEntry.getValue())));
					break;
				case LEGAL_NAME:
					addPredicate(supplierQuery.byLegalName(((String) filterEntry.getValue())));
					break;
				default:
					break;
				}
			}
		}
	}

	@Override
	public java.util.function.Supplier<SupplierQuery> entityQuerySupplier() {
		return () -> new SupplierQuery<>(Supplier.class);
	}


	private static final long serialVersionUID = 139149040276659056L;
}
