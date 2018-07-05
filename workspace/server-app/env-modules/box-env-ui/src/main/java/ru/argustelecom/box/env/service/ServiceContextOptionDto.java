package ru.argustelecom.box.env.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

import ru.argustelecom.box.env.commodity.model.OptionType;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = { "id" }, callSuper = false)
public class ServiceContextOptionDto extends ConvertibleDto {

	private Long id;
	private Long specId;
	private Long contractId;
	private Long entryId;
	private Long customerId;
	private Long customerTypeId;
	private List<? extends BusinessObjectDto<? extends OptionType>> optionTypes;

	@Builder
	public ServiceContextOptionDto(Long id, Long specId, Long contractId, Long entryId, Long customerId,
			List<? extends BusinessObjectDto<? extends OptionType>> optionTypes,
			Long customerTypeId) {
		this.id = id;
		this.specId = specId;
		this.contractId = contractId;
		this.entryId = entryId;
		this.customerId = customerId;
		this.optionTypes = optionTypes;
		this.customerTypeId = customerTypeId;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return ServiceContextOptionDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return Service.class;
	}

}
