package ru.argustelecom.box.env.contract.testdata;

import ru.argustelecom.box.env.contract.model.ContractCategory;
import ru.argustelecom.box.env.contract.model.ContractType;
import ru.argustelecom.box.env.party.CustomerCategory;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.party.testdata.CustomerTypeTestDataUtils;
import ru.argustelecom.box.env.party.testdata.OwnerTestDataUtils;
import ru.argustelecom.box.env.party.testdata.PartyTestDataUtils;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import javax.inject.Inject;

/**
 * Предоставляет данные для возможности создания нового договора через диалог создания:
 * <p>
 * <ul>
 * <li>Тип договора (ContractSpec). Будет найден или создан тип договора соответствующий тестовому ФЛ.
 * <li>Клиент (Customer). Будет создан новый клиент с типом тестового ФЛ, соответствующий типу договора.
 * </ul>
 */
@ApplicationService
public class ContractCreationProvider implements TestDataProvider {

    public static final String CONTRACT_TYPE_NAME = "contract.creationProvider.contractSpec";
    public static final String CUSTOMER_LAST_NAME = "contract.creationProvider.customer";
    public static final String BROKER_NAME = "contract.creationProvider.broker";

    public static final String DESIRED_CONTRACT_CATEGORY = "contract.creationProvider.contract.category";

    @Inject
    private ContractTypeTestDataUtils contractTypeTestDataUtils;

    @Inject
    private CustomerTypeTestDataUtils customerTypeTestDataUtils;

    @Inject
    private PartyTestDataUtils partyTestDataUtils;
    
    @Inject
    private OwnerTestDataUtils ownerTestDataUtils;

    @Override
    public void provide(TestRunContext testRunContext) {

        ContractCategory contractCategory = testRunContext.getProviderParam(DESIRED_CONTRACT_CATEGORY, ContractCategory.class);

        CustomerType customerType = customerTypeTestDataUtils.findOrCreateTestCustomerType();
        Customer customer = partyTestDataUtils.createTestIndividualCustomer(customerType);
        ContractType contractType = contractTypeTestDataUtils.findOrCreateTestContractType(customerType, contractCategory);

        Owner broker = ownerTestDataUtils.findOrCreateTestOwner();
        
        if (contractCategory == ContractCategory.AGENCY) {
            testRunContext.setBusinessPropertyWithMarshalling(BROKER_NAME, broker.getObjectName());
        }

        testRunContext.setBusinessPropertyWithMarshalling(CONTRACT_TYPE_NAME, contractType.getName());
        testRunContext.setBusinessPropertyWithMarshalling(CUSTOMER_LAST_NAME, partyTestDataUtils.getIndividualLastName(customer));
    }
}