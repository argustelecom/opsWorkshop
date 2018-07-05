package ru.argustelecom.box.env.product;

import java.util.List;

import ru.argustelecom.system.inf.dataloader.LazyTreeNodeLoader;

public class ProductTypeLazyTreeNodeLoader implements LazyTreeNodeLoader<AbstractProductUnit> {

	private ProductTypeRepository productTypeRepository;

	public ProductTypeLazyTreeNodeLoader(ProductTypeRepository productTypeRepository) {
		this.productTypeRepository = productTypeRepository;
	}

	@Override
	public List<AbstractProductUnit> loadChildren(AbstractProductUnit abstractProductUnit) {
		return abstractProductUnit.getChildren(productTypeRepository);
	}

}