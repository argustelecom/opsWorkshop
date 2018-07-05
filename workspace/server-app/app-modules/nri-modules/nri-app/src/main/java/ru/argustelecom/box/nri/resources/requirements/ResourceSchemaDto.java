package ru.argustelecom.box.nri.resources.requirements;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.dto2.ConvertibleDto;
import ru.argustelecom.box.nri.schema.model.ResourceSchema;
import ru.argustelecom.system.inf.modelbase.NamedObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ДТО требования к ресурсам
 * Created by s.kolyada on 19.09.2017.
 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
public class ResourceSchemaDto extends ConvertibleDto implements Serializable, NamedObject {

	private static final long serialVersionUID = 1L;

	/**
	 * Идентификтор
	 */
	private Long id;

	/**
	 * Имя
	 */
	@Setter
	private String name;

	/**
	 * Требования к конкретным ресурсам и/или их параметрам
	 */
	private List<RequiredItemDto> requirements = new ArrayList<>();

	/**
	 * Конструктор
	 *
	 * @param id           идентификатор
	 * @param requirements требования
	 * @param name         имя схемы
	 */
	@Builder
	public ResourceSchemaDto(Long id, List<RequiredItemDto> requirements, String name) {
		this.id = id;
		if (requirements == null) {
			this.requirements = new ArrayList<>();
		} else {
			this.requirements = requirements;
		}
		this.name = name;
	}

	@Override
	public Class<ResourceSchemaDtoTranslator> getTranslatorClass() {
		return ResourceSchemaDtoTranslator.class;
	}

	@Override
	public Class<ResourceSchema> getEntityClass() {
		return ResourceSchema.class;
	}

	@Override
	public String getObjectName() {
		return name;
	}
}
