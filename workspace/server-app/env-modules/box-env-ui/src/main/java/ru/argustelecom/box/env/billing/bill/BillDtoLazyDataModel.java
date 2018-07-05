package ru.argustelecom.box.env.billing.bill;

import ru.argustelecom.system.inf.model.LazyAbstractDirectoryEditDataModel;

import java.util.List;

public class BillDtoLazyDataModel extends LazyAbstractDirectoryEditDataModel<BillDto> {
	private static final long serialVersionUID = -7386431541842329112L;

	public BillDtoLazyDataModel(List<BillDto> queryResultList) {
		super(queryResultList);
	}

	@Override
	protected int defaultSort(BillDto t1, BillDto t2) {
		return t2.getId().compareTo(t1.getId());
	}

	@Override
	public String getRowKey(BillDto object) {
		return object.getId().toString();
	}
}
