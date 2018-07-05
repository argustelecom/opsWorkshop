package ru.argustelecom.box.env.product.testdata;

import javax.inject.Inject;

import com.google.common.base.Preconditions;

import ru.argustelecom.box.env.product.model.ProductTypeGroup;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

/**
 * Создает(или находит уже существующую) группу продуктов ({@link ProductTypeGroup}) для тестов
 * <p>
 * 
 * @author kostd
 *
 */
public class ProductTypeGroupProvider implements TestDataProvider {
	
	@Inject
	private ProductTypeTestDataUtils productTypeTestDataUtils;

	public static final String CREATED_GROUP_PROPERTY_NAME = "product.spec.group.provider.product.spec.group";

	@Override
	public void provide(TestRunContext testRunContext) {
		Preconditions.checkState(testRunContext != null);

		ProductTypeGroup group = productTypeTestDataUtils.findOrCreateTestProductTypeGroup();
		Preconditions.checkState(group != null);
		testRunContext.setBusinessPropertyWithMarshalling(CREATED_GROUP_PROPERTY_NAME, group);
	}
}
