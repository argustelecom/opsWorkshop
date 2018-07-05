package ru.argustelecom.box.env.contract.testdata;

import ru.argustelecom.box.env.contract.ContractRepository;
import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.contract.model.ContractType;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.testdata.CustomerTypeTestDataUtils;
import ru.argustelecom.box.env.party.testdata.PartyTestDataUtils;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;
import static ru.argustelecom.box.env.util.UITestUtils.uniqueId;

/**
 * Создаёт договор, который находится в статусе "Оформление"
 */
@ApplicationService
public class ContractProvider implements TestDataProvider, Serializable {

    public static final String CREATED_CONTRACT_PROP = "contract.contractProvider.contract";

    @Inject
    private ContractRepository contractRp;

    @Inject
    private CustomerTypeTestDataUtils customerTypeTestDataUtils;

    @Inject
    private ContractTypeTestDataUtils contractTypeTestDataUtils;

    @Inject
    private PartyTestDataUtils partyTestDataUtils;

    @Override
    public void provide(TestRunContext testRunContext) {
        CustomerType customerType = customerTypeTestDataUtils.findOrCreateTestCustomerType();
        ContractType contractType = contractTypeTestDataUtils.findOrCreateTestContractType(customerType);

        Customer customer = partyTestDataUtils.createTestIndividualCustomer(customerType);
        Contract contract = contractRp.createContract(
                contractType,
                customer,
                uniqueId("contract test"),
                new Date(),
                null,
                PaymentCondition.PREPAYMENT,
                null
        );

        checkNotNull(contract);

        testRunContext.setBusinessPropertyWithMarshalling(CREATED_CONTRACT_PROP, contract);
    }

    private static final long serialVersionUID = 6813023666193906989L;

}
