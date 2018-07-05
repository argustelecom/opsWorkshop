package ru.argustelecom.box.env.billing.bill;

import static ru.argustelecom.box.env.billing.bill.BillListViewState.BillFilter.BILL_DATE;
import static ru.argustelecom.box.env.billing.bill.BillListViewState.BillFilter.BILL_TYPE;
import static ru.argustelecom.box.env.billing.bill.BillListViewState.BillFilter.BROKER;
import static ru.argustelecom.box.env.billing.bill.BillListViewState.BillFilter.CUSTOMER;
import static ru.argustelecom.box.env.billing.bill.BillListViewState.BillFilter.CUSTOMER_TYPE;
import static ru.argustelecom.box.env.billing.bill.BillListViewState.BillFilter.END_DATE;
import static ru.argustelecom.box.env.billing.bill.BillListViewState.BillFilter.NUMBER;
import static ru.argustelecom.box.env.billing.bill.BillListViewState.BillFilter.PERIOD_TYPE;
import static ru.argustelecom.box.env.billing.bill.BillListViewState.BillFilter.PERIOD_UNIT;
import static ru.argustelecom.box.env.billing.bill.BillListViewState.BillFilter.PROVIDER;
import static ru.argustelecom.box.env.billing.bill.BillListViewState.BillFilter.START_DATE;

import java.util.Date;
import java.util.Map;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.BaseEQConvertibleDtoFilterModel;
import ru.argustelecom.box.env.billing.bill.model.Bill;
import ru.argustelecom.box.env.billing.bill.model.Bill.BillQuery;
import ru.argustelecom.box.env.billing.bill.model.BillPeriodType;
import ru.argustelecom.box.env.billing.bill.model.BillType;
import ru.argustelecom.box.env.customer.CustomerDto;
import ru.argustelecom.box.env.customer.CustomerTypeDto;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.stl.period.PeriodUnit;

public class BillListFilterModel extends BaseEQConvertibleDtoFilterModel<BillQuery<Bill>> {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private BillListViewState billListVs;

	@Override
	public void buildPredicates(BillQuery<Bill> query) {
		Map<String, Object> filterMap = billListVs.getFilterMap();
		for (Map.Entry<String, Object> filterEntry : filterMap.entrySet()) {
			if (filterEntry != null) {
				switch (filterEntry.getKey()) {
				case NUMBER:
					addPredicate(query.documentNumber().equal((String) filterEntry.getValue()));
					break;
				case CUSTOMER_TYPE:
					addPredicate(query.byCustomerType(
							(CustomerType) ((CustomerTypeDto) filterEntry.getValue()).getIdentifiable(em)));
					break;
				case CUSTOMER:
					addPredicate(query.customer()
							.equal((Customer) ((CustomerDto) filterEntry.getValue()).getIdentifiable(em)));
					break;
				case PROVIDER:
					addPredicate(query.provider().equal(((PartyRole) ((BusinessObjectDto<PartyRole>) filterEntry.getValue()).getIdentifiable(em))));
					break;
				case BROKER:
					addPredicate(query.broker().equal(((Owner) ((BusinessObjectDto<Owner>) filterEntry.getValue()).getIdentifiable(em))));
					break;
				case BILL_TYPE:
					addPredicate(
							query.type().equal((BillType) ((BillTypeDto) filterEntry.getValue()).getIdentifiable(em)));
					break;
				case BILL_DATE:
					addPredicate(query.documentDate().equal((Date) filterEntry.getValue()));
					break;
				case PERIOD_TYPE:
					addPredicate(query.periodType().equal((BillPeriodType) filterEntry.getValue()));
					break;
				case PERIOD_UNIT:
					addPredicate(query.periodUnit().equal((PeriodUnit) filterEntry.getValue()));
					break;
				case START_DATE:
					addPredicate(query.startDate().greaterOrEqualTo((Date) filterEntry.getValue()));
					break;
				case END_DATE:
					addPredicate(query.endDate().lessOrEqualTo((Date) filterEntry.getValue()));
					break;
				default:
					break;
				}
			}
		}
	}

	@Override
	public Supplier<BillQuery<Bill>> entityQuerySupplier() {
		return () -> new BillQuery<>(Bill.class);
	}

	private static final long serialVersionUID = 3997008523385843755L;

}