package ru.argustelecom.box.env.product;

import java.io.Serializable;

import javax.inject.Named;

import ru.argustelecom.system.inf.page.PresentationState;

@Named(value = "productTypeDirectoryVs")
@PresentationState
public class ProductTypeDirectoryViewState implements Serializable {

	private static final long serialVersionUID = -6276372048810680493L;

	private AbstractProductUnit productTypeUnit;

	public AbstractProductUnit getProductTypeUnit() {
		return productTypeUnit;
	}

	public void setProductTypeUnit(AbstractProductUnit productTypeUnit) {
		this.productTypeUnit = productTypeUnit;
	}

}