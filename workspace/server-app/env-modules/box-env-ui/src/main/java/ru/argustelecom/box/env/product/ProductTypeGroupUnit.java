package ru.argustelecom.box.env.product;

import static ru.argustelecom.box.env.product.ProductTypeCategory.GROUP;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ru.argustelecom.box.env.product.model.AbstractProductType;
import ru.argustelecom.box.env.product.model.ProductType;
import ru.argustelecom.box.env.product.model.ProductTypeComposite;
import ru.argustelecom.box.env.product.model.ProductTypeGroup;
import ru.argustelecom.system.inf.modelbase.Identifiable;

public class ProductTypeGroupUnit extends AbstractProductUnit {

	private ProductTypeGroup productTypeGroup;

	public ProductTypeGroupUnit(ProductTypeGroup productTypeGroup) {
		this.productTypeGroup = productTypeGroup;
	}

	@Override
	public Long getId() {
		return productTypeGroup.getId();
	}

	@Override
	public String getClassName() {
		return productTypeGroup.getClass().getName();
	}

	@Override
	public String getNodeType() {
		return GROUP.getKeyword();
	}

	@Override
	public String getIcon() {
		return GROUP.getIcon();
	}

	@Override
	public String getCategoryName() {
		return GROUP.getTitle();
	}

	@Override
	public String getName() {
		return productTypeGroup.getObjectName();
	}

	@Override
	public boolean isGroup() {
		return true;
	}

	@Override
	public List<AbstractProductUnit> getChildren(ProductTypeRepository productTypeRepository) {
		return productTypeRepository.findAllChildren(productTypeGroup).stream().map(this::wrap)
				.collect(Collectors.toList());
	}

	@Override
	public Set<AbstractProductUnit> getParents() {
		return Collections.emptySet();
	}
	
	@Override
	public Identifiable getWrappedEntity() {
		return productTypeGroup;
	}
	
	public ProductTypeGroup getProductTypeGroup() {
		return productTypeGroup;
	}

	public static List<AbstractProductUnit> wrap(List<ProductTypeGroup> productTypeGroups) {
		return productTypeGroups.stream().map(ProductTypeGroupUnit::new).collect(Collectors.toList());
	}

	private AbstractProductUnit wrap(AbstractProductType productType) {
		AbstractProductUnit abstractProductUnit = null;
		if (productType instanceof ProductType)
			abstractProductUnit = new ProductTypeUnit(productType);
		if (productType instanceof ProductTypeComposite)
			abstractProductUnit = new ProductTypeCompositeUnit(productType);
		return abstractProductUnit;
	}
}