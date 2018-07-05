package ru.argustelecom.box.env.product;

import static ru.argustelecom.box.env.product.ProductTypeCategory.SIMPLE;

import ru.argustelecom.box.env.product.model.AbstractProductType;

public class ProductTypeUnit extends AbstractProductTypeUnit {

	public ProductTypeUnit(AbstractProductType abstractProductType) {
		super(abstractProductType);
	}

	@Override
	public String getNodeType() {
		return SIMPLE.getKeyword();
	}

	@Override
	public String getIcon() {
		return SIMPLE.getIcon();
	}

	@Override
	public String getCategoryName() {
		return SIMPLE.getTitle();
	}

}