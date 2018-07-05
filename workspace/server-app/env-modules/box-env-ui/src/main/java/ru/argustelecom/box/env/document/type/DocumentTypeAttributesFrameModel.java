package ru.argustelecom.box.env.document.type;

import java.io.Serializable;

public abstract class DocumentTypeAttributesFrameModel<T extends DocumentTypeDto> implements Serializable {

	private T documentTypeDto;

	public void preRender(T documentType) {
		checkDocumentType(documentType);
		this.documentTypeDto = documentType;
	}

	protected void checkDocumentType(T documentType) {
	}

	public T getDocumentTypeDto() {
		return documentTypeDto;
	}

	public DocumentTypeCategory getCategory() {
		return DocumentTypeCategory.of(documentTypeDto);
	}

	private static final long serialVersionUID = -7805938860276324435L;

}