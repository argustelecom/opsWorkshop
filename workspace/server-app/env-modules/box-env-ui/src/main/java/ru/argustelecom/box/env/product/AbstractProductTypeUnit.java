package ru.argustelecom.box.env.product;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import ru.argustelecom.box.env.product.model.AbstractProductType;
import ru.argustelecom.system.inf.modelbase.Identifiable;

public abstract class AbstractProductTypeUnit extends AbstractProductUnit {

	private AbstractProductType abstractProductType;

	public AbstractProductTypeUnit(AbstractProductType abstractProductType) {
		this.abstractProductType = abstractProductType;
	}

	@Override
	public Long getId() {
		return abstractProductType.getId();
	}

	@Override
	public String getClassName() {
		return abstractProductType.getClass().getName();
	}

	@Override
	public String getName() {
		return abstractProductType.getName();
	}

	@Override
	public List<AbstractProductUnit> getChildren(ProductTypeRepository productTypeRepository) {
		return Collections.emptyList();
	}

	@Override
	public Set<AbstractProductUnit> getParents() {
		return Sets.newHashSet(new ProductTypeGroupUnit(abstractProductType.getGroup()));
	}

	@Override
	public Identifiable getWrappedEntity() {
		return abstractProductType;
	}

	public AbstractProductType getAbstractProductType() {
		return abstractProductType;
	}

}