package ru.argustelecom.box.env.commodity.testdata;

import ru.argustelecom.box.env.commodity.CommoditySpecRepository;
import ru.argustelecom.box.env.commodity.model.CommodityType;
import ru.argustelecom.box.env.commodity.model.ServiceSpec;
import ru.argustelecom.box.env.commodity.model.ServiceSpec.ServiceSpecQuery;
import ru.argustelecom.box.env.commodity.model.ServiceType;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;

import static ru.argustelecom.box.env.util.UITestUtils.getOrElse;

public class CommoditySpecTestDataUtils implements Serializable {

    private static final long serialVersionUID = 3417149256160580628L;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private CommoditySpecRepository commoditySpecRp;

    @Inject
    private CommodityTypeTestDataUtils commodityTypeTestDataUtils;

    public ServiceSpec findOrCreateTestCommoditySpec(ServiceType serviceType) {
        return getOrElse(findAllServiceSpec(serviceType), () -> commoditySpecRp.createServiceSpec(serviceType));
    }

    public ServiceSpec findOrCreateTestCommoditySpec() {
        ServiceType serviceType = commodityTypeTestDataUtils.findOrCreateTestServiceType();
        return findOrCreateTestCommoditySpec(serviceType);
    }

    private List<ServiceSpec> findAllServiceSpec(CommodityType commodityType) {
        ServiceSpecQuery<ServiceSpec> query = new ServiceSpecQuery<>(ServiceSpec.class);
        return query.and(
                query.type().equal((ServiceType) commodityType)
        ).getResultList(em);
    }
}