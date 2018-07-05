package ru.argustelecom.box.env.document.type.tree;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Objects;

import ru.argustelecom.box.env.document.type.DocumentTypeCategory;
import ru.argustelecom.box.env.document.type.DocumentTypeDto;
import ru.argustelecom.system.inf.modelbase.NamedObject;

public abstract class AbstractDocumentTypeUnit<T> implements NamedObject {

	private T delegate;

	public AbstractDocumentTypeUnit(T delegate) {
		this.delegate = checkNotNull(delegate);
	}

	public T getDelegate() {
		return delegate;
	}

	public abstract Serializable getId();

	@Override
	public abstract String getObjectName();

	public abstract DocumentTypeDto getDocumentTypeDto();

	public abstract DocumentTypeCategory getCategory();

	@Override
	public int hashCode() {
		return Objects.hashCode(getId());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		AbstractDocumentTypeUnit<?> other = (AbstractDocumentTypeUnit<?>) obj;
		return Objects.equals(this.getId(), other.getId());
	}
}
