package ru.argustelecom.box.env.pricing.testdata;

import javax.inject.Inject;

import com.google.common.base.Preconditions;

import ru.argustelecom.box.env.billing.provision.model.RecurrentTerms;
import ru.argustelecom.box.env.billing.provision.model.RecurrentTermsState;
import ru.argustelecom.box.env.billing.provision.testdata.ProvisionTermsTestDataUtils;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionLifecycleQualifier;
import ru.argustelecom.box.env.party.CustomerCategory;
import ru.argustelecom.box.env.party.model.CustomerSegment;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.testdata.CustomerSegmentTestDataUtils;
import ru.argustelecom.box.env.party.testdata.CustomerTypeTestDataUtils;
import ru.argustelecom.box.env.pricing.model.CommonPricelist;
import ru.argustelecom.box.env.pricing.model.PricelistState;
import ru.argustelecom.box.env.product.model.ProductType;
import ru.argustelecom.box.env.product.testdata.ProductTypeTestDataUtils;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

/**
 * Предоставляет:
 * 
 * <li>Тип товара (ProductSpecGroup), находит существующий или создает новый.
 * <li>Продукт (ProductSpec), входящий в созданый тип товаров (ProductSpecGroup). находит существующий или создает новый.
 * <li>Периодичекое условие предоставления (RecurrentTerms), находит существующий или создает новый.
 * <li>Спецификацию клиента (customerSpec), находит существующий или создает новый.
 * <li>Тип клиента (customerSegment), находит существующий или создает новый.
 * <li>Публичный прай-лист (CommonPricelist), находит существующий или создает новый.
 * 
 * @author v.semchenko
 */
public class CommonPricelistProvider implements TestDataProvider {

	public static final String COMMON_PRICELIST_PROP_NAME = "common.pricelist.provider.common.pricelist";
	public static final String PRODUCT_TYPE = "common.pricelist.provider.product.type";
	public static final String PROVISION_TERMS = "common.pricelist.provider.provision.terms";

	@Inject
	private ProvisionTermsTestDataUtils provisionTermsTestDataUtils;

	@Inject
	private PricelistTestDataUtils pricelistTestDataUtils;

	@Inject
	private ProductTypeTestDataUtils productTypeTestDataUtils;

	@Inject
	private CustomerTypeTestDataUtils customerTypeTestDataUtils;

	@Inject
	private CustomerSegmentTestDataUtils customerSegmentTestDataUtils;

	@Override
	public void provide(TestRunContext testRunContext) {

		// Создание типа товара
		ProductType productType = productTypeTestDataUtils.findOrCreateTestProductType();

		// Создание периодического условия предоставления
		RecurrentTerms recurrentTerms = provisionTermsTestDataUtils.findOrCreateTestRecurrentTerms(
				SubscriptionLifecycleQualifier.FULL, false);

		// Для периодического условия предоставления необходимо активное состояние, чтобы он отображался в диалоге
		// создания предложения продукта
		if (!recurrentTerms.inState(RecurrentTermsState.ACTIVE))
			recurrentTerms.setState(RecurrentTermsState.ACTIVE);
		
		// Создание публичного прай-листа
		CustomerType customerType = customerTypeTestDataUtils.findOrCreateTestCustomerType();
		Preconditions.checkState(customerType != null);
		CustomerSegment customerSegment = customerSegmentTestDataUtils.findOrCreateTestCustomerSegment(customerType);


		CommonPricelist commonPricelist = pricelistTestDataUtils.createTestCommonPricelist(
				customerType, customerSegment, PricelistState.CREATED
		);
		
		testRunContext.setBusinessPropertyWithMarshalling(COMMON_PRICELIST_PROP_NAME, commonPricelist);
		testRunContext.setBusinessPropertyWithMarshalling(PRODUCT_TYPE, productType.getName());
		testRunContext.setBusinessPropertyWithMarshalling(PROVISION_TERMS, recurrentTerms.getObjectName());

	}
}