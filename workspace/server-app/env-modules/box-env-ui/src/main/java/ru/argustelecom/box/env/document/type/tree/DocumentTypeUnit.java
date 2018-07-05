package ru.argustelecom.box.env.document.type.tree;

import java.io.Serializable;

import ru.argustelecom.box.env.document.type.DocumentTypeCategory;
import ru.argustelecom.box.env.document.type.DocumentTypeDto;

public class DocumentTypeUnit extends AbstractDocumentTypeUnit<DocumentTypeDto> {

	public DocumentTypeUnit(DocumentTypeDto delegate) {
		super(delegate);
	}

	@Override
	public Serializable getId() {
		return getDelegate().getId();
	}

	@Override
	public String getObjectName() {
		return getDelegate().getName();
	}

	@Override
	public DocumentTypeDto getDocumentTypeDto() {
		return getDelegate();
	}

	@Override
	public DocumentTypeCategory getCategory() {
		return DocumentTypeCategory.of(getDelegate());
	}

}
