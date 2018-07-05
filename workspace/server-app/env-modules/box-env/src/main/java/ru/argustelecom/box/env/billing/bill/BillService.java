package ru.argustelecom.box.env.billing.bill;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.bill.model.Bill;
import ru.argustelecom.box.env.billing.bill.model.RawDataHolder;
import ru.argustelecom.box.env.login.LoginService;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.inf.service.DomainService;

@DomainService
public class BillService implements Serializable {

	@Inject
	private BillCreationService billCreationService;

	@Inject
	private BillHistoryRepository billHistoryRepository;

	@Inject
	private LoginService loginService;

	@Inject
	private BillRawDataRepository billRawDataRepository;

	public void changeNumber(Bill bill, String number) {
		if (!bill.getDocumentNumber().equals(number)) {
			billHistoryRepository.create(bill, loginService.getCurrentEmployee());
			bill.setDocumentNumber(number);
			// не нужно, т.к. будет изменен номер, но для консистентности добавлю
			bill.setLastUpdate(new Date());
		}
	}

	public void recalculateBills(Collection<Bill> bills, Date billDate, Employee employee) {
		bills.forEach(bill -> recalculateBill(bill, billDate, employee));
	}

	public void recalculateBill(Bill bill, Date billDate) {
		recalculateBill(bill, billDate, loginService.getCurrentEmployee());
	}

	public void recalculateBill(Bill bill, Date billDate, Employee employee) {
		RawDataHolder rawDataHolder = bill.getBillRawData().getRawDataContainer().getDataHolder();
		DataHolder recalculatedDataHolder = billCreationService.dataRecreation(bill, billDate);

		if (!rawDataHolder.equals(recalculatedDataHolder.getRawDataHolder())) {
			bill.changeData(billRawDataRepository.create(recalculatedDataHolder.getRawDataHolder()),
					recalculatedDataHolder.getAggDataHolder());
		}

		billHistoryRepository.create(bill, employee);
		bill.setDocumentDate(billDate);
		// если данные после пересчета не изменились и billDate равен текущей дате в счете, то нужно явно изменить
		// счет, для того, чтобы повысилась его версия
		bill.setLastUpdate(new Date());
	}

	private static final long serialVersionUID = -856063702759654775L;
}
