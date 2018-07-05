package ru.argustelecom.box.env.document.type;

import java.util.Arrays;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.box.env.billing.bill.model.BillType;
import ru.argustelecom.box.env.contract.model.ContractExtensionType;
import ru.argustelecom.box.env.contract.model.ContractType;
import ru.argustelecom.box.env.document.model.DocumentType;
import ru.argustelecom.box.inf.nls.LocaleUtils;

// TODO Лютый говнокод, нужно будет переписать в более спокойной обстановке
@AllArgsConstructor(access = AccessLevel.MODULE)
public enum DocumentTypeCategory {

	//@formatter:off
	CONTRACT_TYPE          ("{DocumentTypeBundle:box.doc.type.contract}", ContractType.class, "contract", "fa fa-file-o"),
	CONTRACT_EXTENSION_TYPE("{DocumentTypeBundle:box.doc.type.contract.extension}", ContractExtensionType.class, "contractExtension", "fa fa-files-o"),
	BILL_TYPE              ("{DocumentTypeBundle:box.doc.type.bill}", BillType.class, "bill", "fa fa-file-o");
	//@formatter:on

	private String propertyBundle;
	@Getter
	private Class<? extends DocumentType> entityClass;
	@Getter
	private String keyword;
	@Getter
	private String icon;

	public String getName() {
		return LocaleUtils.getLocalizedMessage(propertyBundle, getClass());
	}

	public static DocumentTypeCategory of(String value) {
		for (DocumentTypeCategory category : values()) {
			if (category.name().equalsIgnoreCase(value)) {
				return category;
			}
		}
		return null;
	}

	public static <T extends DocumentTypeDto> DocumentTypeCategory of(T documentTypeDto) {
		if (documentTypeDto != null) {
			for (DocumentTypeCategory category : values()) {
				if (category.getEntityClass().equals(documentTypeDto.getEntityClass())) {
					return category;
				}
			}
		}
		return null;
	}

	public static DocumentTypeCategory getDocumentTypeCategory(Class<?> documentTypeClass) {
		return Arrays.stream(values())
				.filter(documentTypeCategory -> documentTypeCategory.entityClass.equals(documentTypeClass)).findFirst()
				.orElse(null);
	}

}