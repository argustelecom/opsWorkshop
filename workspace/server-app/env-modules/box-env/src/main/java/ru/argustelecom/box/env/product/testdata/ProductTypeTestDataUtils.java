package ru.argustelecom.box.env.product.testdata;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.commodity.model.CommoditySpec;
import ru.argustelecom.box.env.commodity.testdata.CommoditySpecTestDataUtils;
import ru.argustelecom.box.env.product.ProductTypeRepository;
import ru.argustelecom.box.env.product.model.ProductType;
import ru.argustelecom.box.env.product.model.ProductType.ProductTypeQuery;
import ru.argustelecom.box.env.product.model.ProductTypeGroup;
import ru.argustelecom.box.env.product.model.ProductTypeGroup.ProductTypeGroupQuery;

public class ProductTypeTestDataUtils implements Serializable {

	private static final long serialVersionUID = 477714695154250701L;

	private static final String TEST_PRODUCT_TYPE = "TEST-PRODUCT-Type";

	@PersistenceContext
	private EntityManager em;

	@Inject
	private CommoditySpecTestDataUtils commoditySpecTestDataUtils;

	@Inject
	private ProductTypeRepository productTypeRp;

	public ProductType findOrCreateTestProductType(ProductTypeGroup group) {
		ProductTypeQuery<ProductType> query = new ProductTypeQuery<>(ProductType.class);
		query.and(query.group().equal(group), query.keyword().equal(TEST_PRODUCT_TYPE));
		ProductType product = query.getSingleResult(em, false);

		if (product != null) {
			return product;
		}

		CommoditySpec<?> commoditySpec = commoditySpecTestDataUtils.findOrCreateTestCommoditySpec();
		product = productTypeRp.createProductType("Тестовый продукт", TEST_PRODUCT_TYPE, "Тестовое описание", group);
		product.addEntry(commoditySpec);

		return product;
	}

	public ProductType findOrCreateTestProductType() {
		return findOrCreateTestProductType(findOrCreateTestProductTypeGroup());
	}

	public ProductTypeGroup findOrCreateTestProductTypeGroup() {
		ProductTypeGroupQuery<ProductTypeGroup> query = new ProductTypeGroupQuery<>(ProductTypeGroup.class);
		query.and(query.objectName().equal("Тестовая группа продуктов"));
		ProductTypeGroup group = query.getSingleResult(em, false);

		if (group != null) {
			return group;
		}

		return productTypeRp.createProductTypeGroup("Тестовая группа продуктов", "Тестовое описание");
	}
}
