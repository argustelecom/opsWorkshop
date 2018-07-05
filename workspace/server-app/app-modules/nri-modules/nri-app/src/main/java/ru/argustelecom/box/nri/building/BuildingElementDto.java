package ru.argustelecom.box.nri.building;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.dto2.ConvertibleDto;
import ru.argustelecom.box.nri.building.model.BuildingElement;
import ru.argustelecom.system.inf.modelbase.NamedObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ДТО элемента здания
 * Created by s.kolyada on 25.08.2017.
 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
public class BuildingElementDto extends ConvertibleDto implements Serializable, NamedObject {

	private static final long serialVersionUID = 1L;

	/**
	 * Идентификтор
	 */
	private Long id;

	/**
	 * Наименование элемента
	 */
	@Setter
	private String name;

	/**
	 * Расположение
	 */
	@Setter
	private Location location;

	/**
	 * Тип элемента
	 */
	@Setter
	private BuildingElementTypeDto type;

	/**
	 * Является ли элемент рутом
	 */
	private Boolean isRoot;

	/**
	 * дочерние элементы
	 */
	private List<BuildingElementDto> childElements = new ArrayList<BuildingElementDto>();

	/**
	 * Констукртор
	 */
	public BuildingElementDto() {
		// конструктор по умолчанию
	}

	/**
	 * Конструктор
	 *
	 * @param id            идентификатор
	 * @param location      адрес
	 * @param type          тип
	 * @param childElements дочерние элементы
	 * @param name          имя
	 * @param isRoot        является ли элемент рутом
	 */
	@Builder
	public BuildingElementDto(Long id, Location location, BuildingElementTypeDto type,
							  List<BuildingElementDto> childElements, String name, Boolean isRoot) {
		this.id = id;
		this.location = location;
		this.type = type;
		this.childElements = childElements == null ? new ArrayList<>() : childElements;
		this.name = name;
		this.isRoot = isRoot;
	}

	@Override
	public Class<BuildingElement> getEntityClass() {
		return BuildingElement.class;
	}

	@Override
	public Class<BuildingElementDtoTranslator> getTranslatorClass() {
		return BuildingElementDtoTranslator.class;
	}

	@Override
	public String getObjectName() {
		return name;
	}
}
