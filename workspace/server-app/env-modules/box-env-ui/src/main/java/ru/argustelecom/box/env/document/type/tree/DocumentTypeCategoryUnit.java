package ru.argustelecom.box.env.document.type.tree;

import java.io.Serializable;

import ru.argustelecom.box.env.document.type.DocumentTypeCategory;
import ru.argustelecom.box.env.document.type.DocumentTypeDto;

public class DocumentTypeCategoryUnit extends AbstractDocumentTypeUnit<DocumentTypeCategory> {

	public DocumentTypeCategoryUnit(DocumentTypeCategory delegate) {
		super(delegate);
	}

	@Override
	public Serializable getId() {
		return getDelegate();
	}

	@Override
	public String getObjectName() {
		return getDelegate().getName();
	}

	@Override
	public DocumentTypeDto getDocumentTypeDto() {
		return null;
	}

	@Override
	public DocumentTypeCategory getCategory() {
		return null;
	}

}
