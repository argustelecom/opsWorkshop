package ru.argustelecom.box.env.product;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import ru.argustelecom.system.inf.modelbase.Identifiable;

public abstract class AbstractProductUnit {

	public abstract Long getId();

	public abstract String getClassName();

	public abstract String getNodeType();

	public abstract String getIcon();

	public abstract String getCategoryName();

	public abstract String getName();

	public abstract Identifiable getWrappedEntity();

	public boolean isGroup() {
		return false;
	}

	public abstract List<AbstractProductUnit> getChildren(ProductTypeRepository productTypeRepository);

	public abstract Set<AbstractProductUnit> getParents();

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getId()).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		AbstractProductUnit other = (AbstractProductUnit) obj;
		return new EqualsBuilder().append(this.getId(), other.getId()).isEquals();
	}

}