package ru.argustelecom.box.env.contractor;

import static ru.argustelecom.box.env.contractor.SupplierLazyDataModel.SupplierSort;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import ru.argustelecom.box.env.EQConvertibleDtoFilterModel;
import ru.argustelecom.box.env.EQConvertibleDtoLazyDataModel;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.Company;
import ru.argustelecom.box.env.party.model.Company_;
import ru.argustelecom.box.env.party.model.role.Supplier;
import ru.argustelecom.box.env.party.model.role.Supplier.SupplierQuery;
import ru.argustelecom.box.env.party.model.role.Supplier_;


public class SupplierLazyDataModel
		extends EQConvertibleDtoLazyDataModel<Supplier, SupplierDto, SupplierQuery, SupplierSort> {

	@Inject
	private SupplierDtoTranslator supplierDtoTranslator;

	@Inject
	private SupplierListFilterModel supplierListFilterModel;

	@PostConstruct
	private void postConstruct() {
		initPathMap();
	}

	private void initPathMap() {
		addPath(SupplierSort.id, query -> query.root().get(Supplier_.id));
		addPath(SupplierSort.legalName, query -> {
			Join<Supplier, Company> join = query.root().join(Supplier_.party.getName(), JoinType.LEFT);
			return join.get(Company_.legalName);
		});
		addPath(SupplierSort.brandName, query -> {
			Join<Supplier, Company> join = query.root().join(Supplier_.party.getName(), JoinType.LEFT);
			return join.get(Company_.brandName);
		});
	}

	@Override
	protected Class<SupplierSort> getSortableEnum() {
		return SupplierSort.class;
	}

	@Override
	protected DefaultDtoTranslator<SupplierDto, Supplier> getDtoTranslator() {
		return supplierDtoTranslator;
	}

	@Override
	protected EQConvertibleDtoFilterModel<SupplierQuery> getFilterModel() {
		return supplierListFilterModel;
	}

	public enum SupplierSort {
		id, legalName, brandName
	}

	private static final long serialVersionUID = 1479491966890562061L;
}
