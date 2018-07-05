package ru.argustelecom.box.env.product;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.box.env.product.nls.ProductMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

@AllArgsConstructor(access = AccessLevel.MODULE)
public enum ProductTypeCategory {

	//@formatter:off
	GROUP     ("group", "fa fa-folder-open"),
	SIMPLE    ("simple", "fa fa-cube"),
	COMPOSITE ("composite", "fa fa-cubes");
	//@formatter:on

	@Getter
	private String keyword;
	@Getter
	private String icon;

	public String getName() {
		ProductMessagesBundle messages = LocaleUtils.getMessages(ProductMessagesBundle.class);

		switch (this) {
		case GROUP:
			return messages.groupProduct();
		case SIMPLE:
			return messages.plainProduct();
		case COMPOSITE:
			return messages.compositeProduct();
		default:
			throw new SystemException("Unsupported RoundingPolicy");
		}
	}

	public String getTitle() {
		return getName();
	}

}