package ru.argustelecom.box.nri.booking;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.dto2.ConvertibleDto;
import ru.argustelecom.box.nri.resources.requirements.ResourceSchemaDto;
import ru.argustelecom.box.nri.schema.requirements.model.RequirementType;
import ru.argustelecom.box.nri.schema.requirements.model.ResourceRequirement;
import ru.argustelecom.system.inf.modelbase.NamedObject;

import java.io.Serializable;

/**
 * Created by s.kolyada on 21.12.2017.
 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
public class BookingRequirementDto extends ConvertibleDto implements Serializable, NamedObject {

	private static final long serialVersionUID = 1L;

	/**
	 * Идентификтор
	 */
	private Long id;

	/**
	 * Имя
	 */
	@Setter
	protected String name;

	/**
	 * Тип бронирования
	 */
	private RequirementType bookingType;

	/**
	 * Схема
	 */
	private ResourceSchemaDto schema;

	/**
	 * Конструктор
	 * @param id идентификатор
	 * @param name имя
	 * @param bookingType тип брони
	 * @param schema схема подключения
	 */
	public BookingRequirementDto(Long id, String name, RequirementType bookingType, ResourceSchemaDto schema) {
		this.id = id;
		this.name = name;
		this.bookingType = bookingType;
		this.schema = schema;
	}

	@Override
	public Class<BookingRequirementDtoTranslator> getTranslatorClass() {
		return BookingRequirementDtoTranslator.class;
	}

	@Override
	public Class<ResourceRequirement> getEntityClass() {
		return ResourceRequirement.class;
	}

	@Override
	public String getObjectName() {
		return name;
	}

}
