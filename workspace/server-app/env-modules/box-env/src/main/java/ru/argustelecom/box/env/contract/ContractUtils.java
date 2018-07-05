package ru.argustelecom.box.env.contract;

import java.util.Objects;

import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.contract.model.ContractExtension;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.system.inf.exception.SystemException;

public final class ContractUtils {

	private ContractUtils() {
	}

	public static boolean isSameCustomer(AbstractContract<?> a, AbstractContract<?> b) {
		Customer customerA = a != null ? a.getCustomer() : null;
		Customer customerB = b != null ? b.getCustomer() : null;
		return isSameCustomer(customerA, customerB);
	}

	public static boolean isSameCustomer(Customer a, Customer b) {
		return Objects.equals(a, b);
	}

	public static boolean isSameCustomerType(Customer a, Customer b) {
		CustomerType typeA = a != null ? a.getTypeInstance().getType() : null;
		CustomerType typeB = b != null ? b.getTypeInstance().getType() : null;
		return isSameCustomerType(typeA, typeB);
	}

	public static boolean isSameCustomerType(Customer a, CustomerType b) {
		CustomerType typeA = a != null ? a.getTypeInstance().getType() : null;
		return isSameCustomerType(typeA, b);
	}

	public static boolean isSameCustomerType(AbstractContract<?> a, AbstractContract<?> b) {
		CustomerType typeA = a != null ? a.getType().getCustomerType() : null;
		CustomerType typeB = b != null ? b.getType().getCustomerType() : null;
		return isSameCustomerType(typeA, typeB);
	}

	public static boolean isSameCustomerType(AbstractContract<?> a, CustomerType b) {
		CustomerType typeA = a != null ? a.getType().getCustomerType() : null;
		return isSameCustomerType(typeA, b);
	}

	public static boolean isSameCustomerType(AbstractContract<?> a, Customer b) {
		CustomerType typeA = a != null ? a.getType().getCustomerType() : null;
		CustomerType typeB = b != null ? b.getTypeInstance().getType() : null;
		return isSameCustomerType(typeA, typeB);
	}

	public static boolean isSameCustomerType(CustomerType a, CustomerType b) {
		return Objects.equals(a, b);
	}

	public static Customer checkCustomerForContract(AbstractContract<?> contract, Customer customer) {
		if (!isSameCustomerType(contract, customer)) {
			// TODO сформулировать текст сообщения...
			throw new SystemException();
		}
		return customer;
	}

	public static ContractExtension checkExtensionForContract(Contract contract, ContractExtension extension) {
		if (!isSameCustomerType(contract, extension)) {
			// TODO сформулировать текст сообщения...
			throw new SystemException();
		}
		if (extension.getCustomer() != null && !isSameCustomer(contract, extension)) {
			// TODO сформулировать текст сообщения...
			throw new SystemException();
		}
		return extension;
	}
}
