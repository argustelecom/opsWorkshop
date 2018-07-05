package ru.argustelecom.box.env.contract.testdata;

import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.role.Supplier;
import ru.argustelecom.box.env.party.testdata.CustomerTypeTestDataUtils;
import ru.argustelecom.box.env.party.testdata.SupplierTestDataUtils;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import javax.inject.Inject;

public class AgencyContractTypeCreationProvider implements TestDataProvider {

    public static final String CUSTOMER_TYPE_NAME = "BilateralContractTypeCreationProvider.customerType";
    public static final String SUPPLIER_NAME = "BilateralContractTypeCreationProvider.supplier";

    @Inject
    private CustomerTypeTestDataUtils customerTypeTestDataUtils;

    @Inject
    private SupplierTestDataUtils supplierTestDataUtils;

    @Override
    public void provide(TestRunContext testRunContext) {
        CustomerType customerType = customerTypeTestDataUtils.findOrCreateTestCustomerType();
        Supplier supplier = supplierTestDataUtils.findOrCreateTestSupplier();

        testRunContext.setBusinessPropertyWithMarshalling(CUSTOMER_TYPE_NAME, customerType.getObjectName());
        testRunContext.setBusinessPropertyWithMarshalling(SUPPLIER_NAME, supplier.getObjectName());
    }
}