package ru.argustelecom.box.env.billing.bill;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import ru.argustelecom.box.env.EQConvertibleDtoFilterModel;
import ru.argustelecom.box.env.EQConvertibleDtoLazyDataModel;
import ru.argustelecom.box.env.billing.bill.BillLazyDataModel.BillSort;
import ru.argustelecom.box.env.billing.bill.model.Bill;
import ru.argustelecom.box.env.billing.bill.model.Bill.BillQuery;
import ru.argustelecom.box.env.billing.bill.model.Bill_;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.CustomerTypeInstance_;
import ru.argustelecom.box.env.party.model.CustomerType_;
import ru.argustelecom.box.env.party.model.role.Customer_;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class BillLazyDataModel extends EQConvertibleDtoLazyDataModel<Bill, BillDto, BillQuery<Bill>, BillSort> {

	@Inject
	private BillDtoTranslator billDtoTr;

	@Inject
	private BillListFilterModel billListFm;

	@PostConstruct
	private void postConstruct() {
		initPathMap();
	}

	@Override
	protected EQConvertibleDtoFilterModel<BillQuery<Bill>> getFilterModel() {
		return billListFm;
	}

	@Override
	protected Class<BillSort> getSortableEnum() {
		return BillSort.class;
	}

	@Override
	protected DefaultDtoTranslator<BillDto, Bill> getDtoTranslator() {
		return billDtoTr;
	}

	private void initPathMap() {
		addPath(BillSort.id, query -> query.root().get(Bill_.id));
		addPath(BillSort.number, query -> query.root().get(Bill_.documentNumber));
		addPath(BillSort.billDate, query -> query.root().get(Bill_.documentDate));
		addPath(BillSort.totalAmount, query -> query.root().get(Bill_.totalAmount));
		addPath(BillSort.billType, query -> query.root().get(Bill_.type));
		addPath(BillSort.paymentCondition, query -> query.root().get(Bill_.paymentCondition));
		addPath(BillSort.groupingMethod, query -> query.root().get(Bill_.groupingMethod));
		addPath(BillSort.customerType, query -> query.root().join(Bill_.customer).join(Customer_.typeInstance)
				.join(CustomerTypeInstance_.type).get(CustomerType_.name));
		addPath(BillSort.customer, query -> query.root().get(Bill_.customer));
		addPath(BillSort.provider, query -> query.root().get(Bill_.provider));
		addPath(BillSort.broker, query -> query.root().get(Bill_.broker));
	}

	public enum BillSort {
		id, number, billDate, totalAmount, billType, paymentCondition, groupingMethod, customerType, customer, provider, broker
	}

	private static final long serialVersionUID = -6231464236378172168L;

}