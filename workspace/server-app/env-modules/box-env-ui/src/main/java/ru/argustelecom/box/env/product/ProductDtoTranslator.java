package ru.argustelecom.box.env.product;

import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.product.model.AbstractProductType;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class ProductDtoTranslator implements DefaultDtoTranslator<ProductDto, AbstractProductType> {

	@Override
	public ProductDto translate(AbstractProductType productType) {
		return new ProductDto(productType.getId(), productType.getObjectName());
	}

}