package ru.argustelecom.box.env.contract.testdata;

import ru.argustelecom.box.env.contract.ContractTypeRepository;
import ru.argustelecom.box.env.contract.model.ContractCategory;
import ru.argustelecom.box.env.contract.model.ContractExtensionType;
import ru.argustelecom.box.env.contract.model.ContractType;
import ru.argustelecom.box.env.contract.model.ContractType.ContractTypeQuery;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.testdata.PartyTestDataUtils;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;

import static ru.argustelecom.box.env.util.UITestUtils.getOrElse;

public class ContractTypeTestDataUtils implements Serializable {

    private static final long serialVersionUID = 722449224893454129L;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private ContractTypeRepository contractTypeRp;

    @Inject
    private PartyTestDataUtils partyTestDataUtils;

    /**
     * Безусловно создает новый тестовый тип договора для тестового типа клиента. Гарантирует, что либо будет создан
     * новый тип договора, либо будет брошено исключение. Никогда не вернет null
     */
    public ContractType createTestContractType(CustomerType customerType, ContractCategory contractCategory) {
        return contractTypeRp.createContractType(
                customerType,
                "Тестовый тип договора",
                "Тестовое описание",
                null,
                contractCategory,
                partyTestDataUtils.findOrCreateTestProviderForContract(contractCategory)
        );
    }

    public ContractType findOrCreateTestContractType(CustomerType customerType, ContractCategory contractCategory) {

        ContractTypeQuery<ContractType> query = new ContractTypeQuery<>(ContractType.class);
        List<ContractType> contractTypes = query.and(
                query.contractCategory().equal(contractCategory),
                query.customerType().equal(customerType)
        ).getResultList(em);

        return getOrElse(contractTypes, () -> createTestContractType(customerType, contractCategory));
    }

    public ContractType findOrCreateTestContractType(CustomerType customerType) {
        return findOrCreateTestContractType(customerType, ContractCategory.BILATERAL);
    }

    public ContractExtensionType findOrCreateDemoContractExtensionType(CustomerType customerType) {
        return getOrElse(contractTypeRp.findExtensionTypes(customerType), () ->
                contractTypeRp.createExtensionType(customerType, "Тестовый тип допсоглашения", "Тестовое описание", null)
        );
    }
}