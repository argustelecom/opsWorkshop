package ru.argustelecom.box.env.billing.bill;

import static java.util.Collections.singletonList;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import ru.argustelecom.box.env.billing.account.PersonalAccountAppService;
import ru.argustelecom.box.env.billing.bill.BillParamsDto.BillContext;
import ru.argustelecom.box.env.billing.bill.model.GroupingMethod;
import ru.argustelecom.box.env.contract.ContractAppService;
import ru.argustelecom.box.env.customer.CustomerDto;
import ru.argustelecom.box.env.customer.CustomerDtoTranslator;
import ru.argustelecom.box.env.customer.CustomerTypeDto;
import ru.argustelecom.box.env.customer.CustomerTypeDtoTranslator;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.dto.DefaultDtoConverterUtils;
import ru.argustelecom.box.env.party.CustomerAppService;
import ru.argustelecom.box.env.party.CustomerTypeAppService;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named("billParamsFm")
@PresentationModel
public class BillParamsFrameModel implements Serializable {

	private static final long serialVersionUID = 955751692331175013L;

	@Inject
	private BillTypeAppService billTypeAs;

	@Inject
	private CustomerTypeAppService customerTypeAs;

	@Inject
	private CustomerAppService customerAs;

	@Inject
	private ContractAppService contractAs;

	@Inject
	private PersonalAccountAppService personalAccountAs;

	@Inject
	private CustomerTypeDtoTranslator customerTypeDtoTr;

	@Inject
	private CustomerDtoTranslator customerDtoTr;

	@Inject
	private BillCreationTypeDtoTranslator billTypeDtoTr;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Getter
	private BillParamsDto billParams;

	private List<BillCreationTypeDto> possibleBillTypes;
	private List<CustomerTypeDto> possibleCustomerTypes;

	public void preRender(BillParamsDto billParams) {
		this.billParams = billParams;
		clear();
	}

	public void clear() {
		possibleBillTypes = null;
		possibleCustomerTypes = null;
	}

	public List<BillCreationTypeDto> getPossibleBillTypes() {
		if (billParams == null || billParams.getCurrentContext() == null) {
			return possibleBillTypes;
		}
		if (possibleBillTypes != null) {
			return possibleBillTypes;
		}

		switch (billParams.getCurrentContext()) {
		case PERSONAL_ACCOUNT:
			return possibleBillTypes = billTypeDtoTr.translate(billTypeAs.findAll()).stream()
					.filter(billType -> billType.getGroupingMethod().equals(GroupingMethod.PERSONAL_ACCOUNT))
					.collect(Collectors.toList());
		case CONTRACT:
			return possibleBillTypes = billTypeDtoTr.translate(billTypeAs.findAll()).stream()
					.filter(billType -> billType.getGroupingMethod().equals(GroupingMethod.CONTRACT))
					.collect(Collectors.toList());
		case LIST:
		case MASS:
			return possibleBillTypes = billTypeDtoTr.translate(billTypeAs.findAll());
		default:
			throw new SystemException("Unsupported billParamsContext");
		}
	}

	public List<CustomerTypeDto> getPossibleCustomerTypes() {
		if (billParams == null || billParams.getCurrentContext() == null) {
			return possibleCustomerTypes;
		}
		if (billParams.getBillType() != null) {
			if (billParams.getBillType().getCustomerType() != null) {
				billParams.setCustomerType(billParams.getBillType().getCustomerType());
				return singletonList(billParams.getCustomerType());
			}
		} else if (possibleCustomerTypes == null) {
			return possibleCustomerTypes = DefaultDtoConverterUtils.translate(customerTypeDtoTr,
					customerTypeAs.findAllCustomerTypes());
		}

		return possibleCustomerTypes;
	}

	public List<CustomerDto> completeCustomer(String customerName) {
		if (billParams.getCustomerType() != null) {
			return customerAs.findCustomerBy(billParams.getCustomerType().getId(), customerName).stream()
					.map(customerDtoTr::translate).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	public List<? extends BusinessObjectDto<?>> getPossibleGroupingObjects() {
		if (!billParams.getCurrentContext().equals(BillContext.LIST)) {
			return Collections.emptyList();
		}
		if (billParams.getCustomer() == null) {
			return Collections.emptyList();
		}
		if (billParams.getGroupingMethod() == null) {
			return Collections.emptyList();
		}

		return billParams.getGroupingMethod().equals(GroupingMethod.PERSONAL_ACCOUNT)
				? businessObjectDtoTr
						.translate(personalAccountAs.findPersonalAccounts(billParams.getCustomer().getId()))
				: businessObjectDtoTr.translate(contractAs.findContracts(billParams.getCustomer().getId()));
	}

}
