package ru.argustelecom.box.env.billing.bill;

import static ru.argustelecom.box.env.contract.model.ContractCategory.AGENCY;
import static ru.argustelecom.box.env.dto.DefaultDtoConverterUtils.translate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.collect.Lists;

import lombok.Getter;
import ru.argustelecom.box.env.billing.bill.model.GroupingMethod;
import ru.argustelecom.box.env.contract.ContractAppService;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.customer.CustomerDto;
import ru.argustelecom.box.env.customer.CustomerDtoTranslator;
import ru.argustelecom.box.env.customer.CustomerTypeDto;
import ru.argustelecom.box.env.customer.CustomerTypeDtoTranslator;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.party.CustomerAppService;
import ru.argustelecom.box.env.party.CustomerTypeRepository;
import ru.argustelecom.box.env.party.OwnerAppService;
import ru.argustelecom.box.env.party.SupplierAppService;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@Named(value = "billListVm")
@PresentationModel
public class BillListViewModel extends ViewModel {

	@Getter
	@Inject
	private BillLazyDataModel lazyDm;

	@Inject
	private BillListViewState billListVs;

	@Inject
	private BillTypeRepository billTypeRp;

	@Inject
	private BillTypeDtoTranslator billTypeDtoTr;

	@Inject
	private CustomerAppService customerAs;

	@Inject
	private SupplierAppService supplierAs;

	@Inject
	private OwnerAppService ownerAs;

	@Inject
	private ContractAppService contractAs;

	@Inject
	private CustomerDtoTranslator customerDtoTr;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Inject
	private CustomerTypeRepository customerTypeRp;

	@Inject
	private CustomerTypeDtoTranslator customerTypeDtoTr;

	@Getter
	private List<CustomerTypeDto> customerTypes;

	@Getter
	private List<BillTypeDto> billTypes;

	@Getter
	private List<GroupingMethod> groupingMethods;

	@Getter
	private List<PaymentCondition> paymentConditions;

	private List<BusinessObjectDto<Owner>> brokers;
	private List<BusinessObjectDto<? extends PartyRole>> suppliers;
	private Integer agencyContractsCount;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
		initFilterData();
	}

	private void initFilterData() {
		customerTypes = translate(customerTypeDtoTr, customerTypeRp.getAllCustomerTypes());
		billTypes = translate(billTypeDtoTr, billTypeRp.findAll());
		groupingMethods = Lists.newArrayList(GroupingMethod.values());
		paymentConditions = Lists.newArrayList(PaymentCondition.values());
	}

	public Callback<List<BillDto>> getCallbackAfterCreation() {
		return newBills -> lazyDm.reloadData();
	}

	public List<CustomerDto> completeCustomer(String customerName) {
		if (billListVs.getCustomerType() != null) {
			return customerAs.findCustomerBy(billListVs.getCustomerType().getId(), customerName).stream()
					.map(customerDtoTr::translate).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	public List<BusinessObjectDto<? extends PartyRole>> getProviders() {
		if (suppliers == null) {
			suppliers = new ArrayList<>(
					supplierAs.findAll().stream().map(businessObjectDtoTr::translate).collect(Collectors.toList()));
			suppliers.addAll(getBrokers());
		}
		return suppliers;
	}

	public List<BusinessObjectDto<Owner>> getBrokers() {
		if (brokers == null) {
			brokers = ownerAs.findAll().stream().map(businessObjectDtoTr::translate).collect(Collectors.toList());
		}
		return brokers;
	}

	public boolean providerFilterRendered() {
		// return getAgencyContractsCount() > 0 && getBrokers().size() > 0;
		return true;
	}

	public boolean brokerFilterRendered() {
		// return getAgencyContractsCount() > 0;
		return true;
	}

	private int getAgencyContractsCount() {
		if (agencyContractsCount == null) {
			agencyContractsCount = contractAs.findContracts(AGENCY).size();
		}
		return agencyContractsCount;
	}

	private static final long serialVersionUID = -221684960155833628L;

}