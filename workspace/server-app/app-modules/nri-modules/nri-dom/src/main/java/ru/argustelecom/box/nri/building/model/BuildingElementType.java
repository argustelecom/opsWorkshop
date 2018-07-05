package ru.argustelecom.box.nri.building.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.address.model.LocationLevel;
import ru.argustelecom.box.inf.modelbase.BusinessDirectory;
import ru.argustelecom.box.nri.building.BuildingElementTypeIcon;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Запись справочника типов элементов строений
 * Created by s.kolyada on 22.08.2017.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "nri", name = "building_element_type")
@Getter
@Setter
public class BuildingElementType extends BusinessDirectory {

    private static final long serialVersionUID = 1L;

    /**
     * Соответствие уровню из модели адресов
     */
    @ManyToOne
    @JoinColumn(name = "location_level_id")
    private LocationLevel locationLevel;

    /**
     * Название типа элемента строения
     */
    @Column(name = "name", nullable = false, unique = true)
    private String name;

	/**
	 * Иконка
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "icon")
    private BuildingElementTypeIcon icon;

    /**
     * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
     */
    protected BuildingElementType() {
        super();
    }

    /**
     * Конструктор для создания в репозитории
     *
     * @param id айди
	 * @param name имя
	 * @param locationLevel уровень
	 * @param icon иконка
     */
    @Builder
    public BuildingElementType(Long id, String name, LocationLevel locationLevel, BuildingElementTypeIcon icon) {
        this.id = id;
        this.name = name;
        this.locationLevel = locationLevel;
        this.icon = icon;
    }

    /**
     * Запрос к данному типу
     */
    public static class BuildingElementTypeQuery extends EntityQuery<BuildingElementType> {

		/**
		 * Конструктор запроса
		 */
		public BuildingElementTypeQuery() {
            super(BuildingElementType.class);
        }
    }
}
