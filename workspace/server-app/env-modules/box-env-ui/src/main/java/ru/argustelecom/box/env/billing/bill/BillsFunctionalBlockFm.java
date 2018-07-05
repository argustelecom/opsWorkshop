package ru.argustelecom.box.env.billing.bill;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.bill.model.GroupingMethod;
import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.customer.CustomerDto;
import ru.argustelecom.box.env.customer.CustomerDtoTranslator;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.dto.DefaultDtoConverterUtils;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

@Getter
@Setter
@Named("billsFunctionalBlockFm")
@PresentationModel
public class BillsFunctionalBlockFm implements Serializable {

	@Inject
	private CustomerDtoTranslator customerDtoTr;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Inject
	private BillDtoTranslator billDtoTranslator;

	@Inject
	private BillRepository billRepository;

	@Inject
	private BillAppService billAppService;

	private BillDtoLazyDataModel lazyDataModel;

	private List<BillDto> billDtoList;

	private CustomerDto customerDto;
	private BusinessObjectDto<?> groupingObject;
	private GroupingMethod groupingMethod;

	public void preRender(Customer customer) {
		customerDto = customerDtoTr.translate(customer);
		billDtoList = DefaultDtoConverterUtils.translate(billDtoTranslator,
				billAppService.findBillsByCustomer(customerDto.getId()));
		lazyDataModel = new BillDtoLazyDataModel(billDtoList);
	}

	public void preRender(Contract contract) {
		groupingObject = businessObjectDtoTr.translate(contract);
		customerDto = customerDtoTr.translate(contract.getCustomer());
		groupingMethod = GroupingMethod.CONTRACT;
		billDtoList = DefaultDtoConverterUtils.translate(billDtoTranslator,
				billRepository.findByContract(groupingObject.getId()));
		lazyDataModel = new BillDtoLazyDataModel(billDtoList);
	}

	public void preRender(PersonalAccount personalAccount) {
		groupingObject = businessObjectDtoTr.translate(personalAccount);
		customerDto = customerDtoTr.translate(personalAccount.getCustomer());
		groupingMethod = GroupingMethod.PERSONAL_ACCOUNT;
		billDtoList = DefaultDtoConverterUtils.translate(billDtoTranslator,
				billRepository.findByPersonalAccount(personalAccount.getId()));
		lazyDataModel = new BillDtoLazyDataModel(billDtoList);
	}

	public Callback<List<BillDto>> getCallbackAfterCreation() {
		return newBills -> billDtoList.addAll(newBills);
	}

	public void onCreationDialogOpen() {
		RequestContext.getCurrentInstance().update("bill_create_form");
		RequestContext.getCurrentInstance().execute("PF('billCreateDlgVar').show();");
	}

	private static final long serialVersionUID = -1440110456602969880L;
}
