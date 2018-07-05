package ru.argustelecom.box.env.document.type;

import java.io.Serializable;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.document.model.DocumentType;
import ru.argustelecom.box.env.document.type.tree.AbstractDocumentTypeUnit;
import ru.argustelecom.box.env.document.type.tree.DocumentTypeUnit;
import ru.argustelecom.box.env.type.CurrentType;
import ru.argustelecom.system.inf.page.PresentationState;

@Named(value = "documentTypeDirectoryVs")
@PresentationState
public class DocumentTypeDirectoryViewState implements Serializable {

	@Inject
	private CurrentType currentType;

	private AbstractDocumentTypeUnit<?> documentTypeUnit;

	public AbstractDocumentTypeUnit<?> getDocumentTypeUnit() {
		return documentTypeUnit;
	}

	public void setDocumentTypeUnit(AbstractDocumentTypeUnit<?> documentTypeUnit) {
		if (!Objects.equals(this.documentTypeUnit, documentTypeUnit)) {
			this.documentTypeUnit = documentTypeUnit;
			if (this.documentTypeUnit != null && this.documentTypeUnit instanceof DocumentTypeUnit) {
				currentType.setValue(
						(DocumentType) ((DocumentTypeUnit) this.documentTypeUnit).getDelegate().getIdentifiable());
			} else {
				currentType.setValue(null);
			}
		}
	}

	public void resetCurrentType() {
		currentType.setValue(null);
	}

	private static final long serialVersionUID = -7022322455346349870L;

}