package ru.argustelecom.box.nri.resources;

import lombok.Builder;
import lombok.Getter;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;
import ru.argustelecom.box.nri.resources.model.ResourceStatus;
import ru.argustelecom.system.inf.modelbase.Identifiable;

/**
 * Дто ресурса для списков
 * @author a.wisniewski
 * @since 11.10.2017
 */
@Getter
public class ResourceInstanceListDto extends ConvertibleDto {
	private Long id;
	private String name;
	private String specification;
	private ResourceStatus status;

	/**
	 * конструктор
	 * @param id id
	 * @param name имя
	 * @param specification спецификация
	 * @param status статус
	 */
	@Builder
	public ResourceInstanceListDto(Long id, String name, String specification, ResourceStatus status) {
		this.id = id;
		this.name = name;
		this.specification = specification;
		this.status = status;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return ResourceInstanceListDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return ResourceInstance.class;
	}
}
