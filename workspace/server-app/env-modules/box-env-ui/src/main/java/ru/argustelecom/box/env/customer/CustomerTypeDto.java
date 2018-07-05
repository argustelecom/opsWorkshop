package ru.argustelecom.box.env.customer;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.CustomerCategory;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@EqualsAndHashCode(of = { "id" }, callSuper = false)
@NoArgsConstructor
public class CustomerTypeDto extends ConvertibleDto {
	private Long id;
	private String name;
	private CustomerCategory customerCategory;

	public CustomerTypeDto(Long id, String name, CustomerCategory customerCategory) {
		this.id = id;
		this.name = name;
		this.customerCategory = customerCategory;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return CustomerTypeDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return CustomerType.class;
	}
}
