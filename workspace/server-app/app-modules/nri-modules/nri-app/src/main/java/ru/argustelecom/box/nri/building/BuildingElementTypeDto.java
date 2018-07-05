package ru.argustelecom.box.nri.building;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.address.model.LocationLevel;
import ru.argustelecom.box.env.dto2.ConvertibleDto;
import ru.argustelecom.box.nri.building.model.BuildingElementType;
import ru.argustelecom.system.inf.modelbase.NamedObject;

import java.io.Serializable;

/**
 * ДТО типа элемента здания
 * Created by s.kolyada on 23.08.2017.
 */
@Getter
@Setter
@EqualsAndHashCode(of = "id", callSuper = false)
public class BuildingElementTypeDto extends ConvertibleDto implements NamedObject, Serializable {

	private static final long serialVersionUID = 1L;

    /**
     * Идентификтор
     */
    private Long id;

    /**
     * Имя типа
     */
    private String name;

    /**
     * Соответствие уровню из модели адресов
     */
    private LocationLevel level;

	/**
	 * Иконка
	 */
	private BuildingElementTypeIcon icon;

    /**
     * Дефолтный конструктор для primefaces
     */
    public BuildingElementTypeDto() {
        //дефолтный конструктор
    }

	/**
	 * Конструктор
	 * @param id идентификатор
	 * @param name имя
	 * @param level уровень адресного элемента
	 * @param icon иконка
	 */
    @Builder
    public BuildingElementTypeDto(Long id, String name, LocationLevel level, BuildingElementTypeIcon icon) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.icon = icon;
    }

    @Override
    public Class<BuildingElementType> getEntityClass() {
        return BuildingElementType.class;
    }

    @Override
    public Class<BuildingElementTypeDtoTranslator> getTranslatorClass() {
        return BuildingElementTypeDtoTranslator.class;
    }

    @Override
    public String getObjectName() {
        return name;
    }
}
