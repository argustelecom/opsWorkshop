package ru.argustelecom.box.env.pricing;

import lombok.Getter;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
public class CustomerPricelistListDto extends ConvertibleDto {
	private Long id;
	private String name;

	public CustomerPricelistListDto(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return CustomerPricelistListDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return Customer.class;
	}
}
