package ru.argustelecom.box.env.page.navigation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.bill.model.Bill;
import ru.argustelecom.box.env.billing.bill.model.BillHistoryItem;
import ru.argustelecom.box.env.billing.invoice.model.AbstractInvoice;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJob;
import ru.argustelecom.box.env.billing.provision.model.AbstractProvisionTerms;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.contract.model.ContractExtension;
import ru.argustelecom.box.env.order.model.Order;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.party.model.role.Supplier;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.box.env.product.model.AbstractProductType;
import ru.argustelecom.box.env.security.model.Role;
import ru.argustelecom.box.env.task.model.Task;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@AllArgsConstructor(access = AccessLevel.MODULE)
public enum CardType {

	//@formatter:off
	EMPLOYEE("/views/env/personnel/EmployeeCardView.xhtml", "employee", "System_PersonalView"),
	ROLE("/views/env/security/RoleCardView.xhtml", "role", "System_RoleView"),
	PERSONAL_ACCOUNT("/views/env/billing/account/PersonalAccountView.xhtml", "personalAccount", "Billing_PersonalAccountView"),
	INVOICE("/views/env/billing/invoice/InvoiceCardView.xhtml", "invoice", "Billing_InvoiceView"),
	CUSTOMER("/views/env/customer/CustomerCardView.xhtml", "customer", "CRM_CustomerView"),
	CONTRACT("/views/env/contract/ContractCardView.xhtml", "contract", "CRM_ContractView"),
	CONTRACT_EXTENSION("/views/env/contract/ContractExtensionCardView.xhtml", "contractExtension", "CRM_ContractExtensionView"),
	ORDER("/views/env/order/OrderCardView.xhtml", "order", "CRM_OrderView"),
	PRICELIST("/views/env/pricing/PricelistCardView.xhtml", "pricelist", "ProductManagment_PriceListView"),
	SUBSCRIPTION("/views/env/billing/subscription/SubscriptionCardView.xhtml", "subscription", "Billing_PersonalAccountView"),
	PROVISION_TERMS("/views/env/billing/provision/ProvisionTermsDirectoryView.xhtml", "terms", "ProductManagment_ProvidingTermsEdit"),
	BILL("/views/env/billing/bill/BillCardView.xhtml", "bill", "Billing_BillView"),
	BILL_HISTORY("/views/env/billing/bill/BillCardView.xhtml", "billHistory", "Billing_BillView"),
	TASK("/views/env/task/TaskCardView.xhtml", "task", "System_TaskListView"),
	PRODUCT("/views/env/product/ProductTypeDirectoryView.xhtml", "selectedProduct", "ProductManagment_CatalogEdit"),
	SERVICE("/views/env/services/ServiceCardView.xhtml", "service", "System_ServiceView"),
	SUPPLIER("/views/env/contractor/SupplierCardView.xhtml", "supplier", "CRM_CustomerView"),
	TARIFF("/views/env/telephony/tariff/TariffCardView.xhtml", "tariff", "ProductManagment_TariffView"),
	OWNER("/views/env/companyinfo/CompanyInfoView.xhtml", "owner", "System_OwnerView"),
	CHARGE_JOB("/views/env/billing/invoice/chargejob/ChargeJobCardView.xhtml", "chargeJob", "Billing_ChargeJobView");
	//@formatter:on

	@Getter
	private String viewId;
	@Getter
	private String paramName;
	@Getter
	private String permissionId;

	public static CardType findByIdentifiable(Identifiable value) {

		Class<?> clazz = value.getClass();

		if (Employee.class.isAssignableFrom(clazz)) {
			return EMPLOYEE;
		}
		if (Role.class.isAssignableFrom(clazz)) {
			return ROLE;
		}
		if (PersonalAccount.class.isAssignableFrom(clazz)) {
			return PERSONAL_ACCOUNT;
		}
		if (AbstractInvoice.class.isAssignableFrom(clazz)) {
			return INVOICE;
		}
		if (Customer.class.isAssignableFrom(clazz)) {
			return CUSTOMER;
		}
		if (Contract.class.isAssignableFrom(clazz)) {
			return CONTRACT;
		}
		if (ContractExtension.class.isAssignableFrom(clazz)) {
			return CONTRACT_EXTENSION;
		}
		if (Order.class.isAssignableFrom(clazz)) {
			return ORDER;
		}
		if (AbstractPricelist.class.isAssignableFrom(clazz)) {
			return PRICELIST;
		}
		if (Subscription.class.isAssignableFrom(clazz)) {
			return SUBSCRIPTION;
		}
		if (AbstractProvisionTerms.class.isAssignableFrom(clazz)) {
			return PROVISION_TERMS;
		}
		if (Bill.class.isAssignableFrom(clazz)) {
			return BILL;
		}
		if (BillHistoryItem.class.isAssignableFrom(clazz)) {
			return BILL_HISTORY;
		}

		if (Task.class.isAssignableFrom(clazz)) {
			return TASK;
		}

		if (AbstractProductType.class.isAssignableFrom(clazz)) {
			return PRODUCT;
		}

		if (Service.class.isAssignableFrom(clazz)) {
			return SERVICE;
		}

		if (Supplier.class.isAssignableFrom(clazz)) {
			return SUPPLIER;
		}

		if (AbstractTariff.class.isAssignableFrom(clazz)) {
			return TARIFF;
		}

		if (Owner.class.isAssignableFrom(clazz)) {
			return OWNER;
		}

		if (ChargeJob.class.isAssignableFrom(clazz)) {
			return CHARGE_JOB;
		}

		throw new SystemException(String.format("Don't have a card type for : '%s'", value));

	}

}