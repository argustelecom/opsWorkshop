package ru.argustelecom.box.env.pricing.testdata;

import com.beust.jcommander.internal.Lists;
import ru.argustelecom.box.env.party.model.CustomerSegment;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.testdata.OwnerTestDataUtils;
import ru.argustelecom.box.env.pricing.PricelistRepository;
import ru.argustelecom.box.env.pricing.model.CommonPricelist;
import ru.argustelecom.box.env.pricing.model.PricelistState;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;

public class PricelistTestDataUtils implements Serializable {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private PricelistRepository pricelistRepository;

    @Inject
    private OwnerTestDataUtils ownerTestDataUtils;

    private static final long serialVersionUID = 8023425774693358863L;

    /**
     * Сложно, да и не нужно искать нужный прайс, с нужными сегментами, типом клиента, статусом, в перспективе -
     * поставщиком, поэтому просто создаем такой, какой нужен
     */
    public CommonPricelist createTestCommonPricelist(
            CustomerType customerType, CustomerSegment customerSegment, PricelistState state
    ) {
        checkArgument(state == PricelistState.CREATED || state == PricelistState.INFORCE);

        CommonPricelist commonPricelist = pricelistRepository.createCommonPricelist(
                "Тестовый прайс",
                new Date(),
                /* validTo = */ null,
                Lists.newArrayList(customerSegment),
                ownerTestDataUtils.findOrCreateTestOwner()
        );

        if (state == PricelistState.INFORCE) {
            commonPricelist.setState(PricelistState.INFORCE);
            em.merge(commonPricelist);
        }

        return commonPricelist;
    }
}
