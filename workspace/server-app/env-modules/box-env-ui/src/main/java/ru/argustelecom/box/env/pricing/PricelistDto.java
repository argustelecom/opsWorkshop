package ru.argustelecom.box.env.pricing;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.system.inf.modelbase.NamedObject;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class PricelistDto extends ConvertibleDto implements NamedObject {

	private Long id;
	private String name;

	@Override
	public String getObjectName() {
		return name;
	}

	@Override
	public Class<AbstractPricelist> getEntityClass() {
		return AbstractPricelist.class;
	}

	@Override
	public Class<PricelistDtoTranslator> getTranslatorClass() {
		return PricelistDtoTranslator.class;
	}

}