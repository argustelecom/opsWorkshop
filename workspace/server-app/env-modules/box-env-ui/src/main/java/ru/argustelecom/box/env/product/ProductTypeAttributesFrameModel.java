package ru.argustelecom.box.env.product;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.product.model.ProductTypeGroup;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "productTypeAttributesFm")
@PresentationModel
public class ProductTypeAttributesFrameModel implements Serializable {

	private static final long serialVersionUID = 1970759167888765807L;

	private static final String EMPTY_ICON = "fa fa-question";

	@Inject
	private ProductTypeRepository productTypeRepository;

	@Inject
	private ProductTypeDirectoryViewState productTypeDirectoryViewState;

	private AbstractProductUnit productTypeUnit;

	private List<ProductTypeGroup> productTypeGroups;

	public void preRender() {
		refresh();
	}

	public String getSelectedLocationIcon() {
		return productTypeUnit != null ? productTypeUnit.getIcon() : EMPTY_ICON;
	}

	public List<ProductTypeGroup> getProductTypeGroups() {
		if (productTypeGroups == null)
			productTypeGroups = productTypeRepository.getAllProductTypeGroups();
		return productTypeGroups;
	}

	public boolean isProductTypeGroup() {
		return productTypeUnit != null && productTypeUnit.isGroup();
	}

	public boolean isProductType() {
		return productTypeUnit != null && productTypeUnit instanceof ProductTypeUnit;
	}

	public boolean isProductTypeComposite() {
		return productTypeUnit != null && productTypeUnit instanceof ProductTypeCompositeUnit;
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private void refresh() {
		productTypeUnit = productTypeDirectoryViewState.getProductTypeUnit();
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public AbstractProductUnit getProductTypeUnit() {
		return productTypeUnit;
	}

}