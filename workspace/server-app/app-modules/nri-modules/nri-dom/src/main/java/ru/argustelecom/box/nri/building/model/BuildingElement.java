package ru.argustelecom.box.nri.building.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Элемент строения
 * Служит для описания структуры строения
 * Created by s.kolyada on 22.08.2017.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "nri", name = "building_element")
@Getter
@Setter
public class BuildingElement extends BusinessObject {

	private static final long serialVersionUID = 1L;

	/**
	 * Наименование эдемента
	 */
	@Column(name = "element_name", nullable = false)
	private String name;

	/**
	 * Адрес элемента строения
	 */
	@ManyToOne
	@JoinColumn(name = "location_id")
	private Location location;

	/**
	 * Тип элемента строения
	 */
	@ManyToOne
	@JoinColumn(name = "element_type_id", nullable = false)
	private BuildingElementType type;

	/**
	 * Родительский элемент
	 */
	@ManyToOne
	@JoinColumn(name = "parent_element_id")
	private BuildingElement parent;

	/**
	 * Дочерние элементы
	 */
	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
	private List<BuildingElement> children = new ArrayList<>();

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected BuildingElement() {
		super();
	}

	/**
	 * Конструктор
	 * @param id идентификатор
	 * @param name имя
	 * @param location расположение
	 * @param type тип
	 * @param parent родительский элемент
	 * @param children дочерние элементы
	 */
	@Builder
	public BuildingElement(Long id, String name, Location location, BuildingElementType type, BuildingElement parent,
						   List<BuildingElement> children) {
		this.id = id;
		this.name = name;
		this.location = location;
		this.type = type;
		this.parent = parent;
		this.children = children == null ? new ArrayList<>() : children;
	}

	/**
	 * Добавить дочерний элемент
	 * @param child дочерний элемент
	 * @return истина, если удалось, иначе ложь
	 */
	public Boolean addChild(BuildingElement child) {
		return this.children.add(child);
	}

	/**
	 * Удалить дочерний элемент
	 * @param child дочерний элемент
	 * @return истина, если удалось, иначе ложь
	 */
	public Boolean removeChild(BuildingElement child) {
		return this.children.remove(child);
	}

	public List<BuildingElement> getChildren() {
		return Collections.unmodifiableList(children);
	}

	/**
	 * Запрос к данному типу
	 */
	public static class BuildingElementQuery extends EntityQuery<BuildingElement> {

		EntityQueryEntityFilter<BuildingElement, Location> location = createEntityFilter(BuildingElement_.location);

		/**
		 * Контруктор запроса
		 */
		public BuildingElementQuery() {
			super(BuildingElement.class);
		}

		/**
		 * поиск по локации
		 * @return фильтр
		 */
		public EntityQueryEntityFilter<BuildingElement, Location> location() {
			return location;
		}
	}
}
