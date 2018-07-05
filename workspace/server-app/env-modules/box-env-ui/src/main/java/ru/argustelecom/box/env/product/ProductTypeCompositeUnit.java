package ru.argustelecom.box.env.product;

import static ru.argustelecom.box.env.product.ProductTypeCategory.COMPOSITE;

import ru.argustelecom.box.env.product.model.AbstractProductType;

public class ProductTypeCompositeUnit extends AbstractProductTypeUnit {

	public ProductTypeCompositeUnit(AbstractProductType abstractProductType) {
		super(abstractProductType);
	}

	@Override
	public String getNodeType() {
		return COMPOSITE.getKeyword();
	}

	@Override
	public String getIcon() {
		return COMPOSITE.getIcon();
	}

	@Override
	public String getCategoryName() {
		return COMPOSITE.getTitle();
	}

}
