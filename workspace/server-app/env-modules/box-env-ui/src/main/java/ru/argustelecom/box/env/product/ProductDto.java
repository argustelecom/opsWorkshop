package ru.argustelecom.box.env.product;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.product.model.AbstractProductType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class ProductDto extends ConvertibleDto {

	private Long id;
	private String name;

	@Override
	public Class<AbstractProductType> getEntityClass() {
		return AbstractProductType.class;
	}

	@Override
	public Class<ProductDtoTranslator> getTranslatorClass() {
		return ProductDtoTranslator.class;
	}

}