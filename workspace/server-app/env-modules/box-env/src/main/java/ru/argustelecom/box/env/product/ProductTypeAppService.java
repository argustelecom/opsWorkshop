package ru.argustelecom.box.env.product;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import ru.argustelecom.box.env.product.model.AbstractProductType;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class ProductTypeAppService implements Serializable {

	private static final long serialVersionUID = -8873316614327264615L;

	@Inject
	private ProductTypeRepository productTypeRp;

	public List<AbstractProductType> findProductTypes() {
		return productTypeRp.getAllProductTypes();
	}

	public List<AbstractProductType> findProductTypesBy(String name) {
		return productTypeRp.findProductTypeByName(name);
	}

}