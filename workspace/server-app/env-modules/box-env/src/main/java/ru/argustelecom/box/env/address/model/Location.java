package ru.argustelecom.box.env.address.model;

import ru.argustelecom.box.env.address.LocationClass;
import ru.argustelecom.box.env.address.map.model.LocationGeo;
import ru.argustelecom.box.env.report.api.Printable;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.publang.base.model.ILocation;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapperDef;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryNumericFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static ru.argustelecom.box.env.address.LocationClass.U;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;

/**
 * Базовый класс, вводит понятние <b>адресообразующего элемента</b> - отдельный элемент адреса, состоящий из имени и
 * типа элемента. Например:
 * <ul>
 * <li>Страны;
 * <li>Сегменты/Районы;
 * <li>Регионы;
 * <li>Улицы;
 * <li>Здания;
 * <li>Квартиры/Помещения
 * </ul>
 * <p/>
 * Иерархичность осознано вынесена на этот уровень, она позволит легко строить полную иерархическую структуру. Сеттер
 * родителя должен быть закрытым, чтобы исключить возможность создания некорректных данных.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "system")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dtype")
//@formatter:off
@NamedNativeQuery(name = Location.FIND_PARENTS, query =
		"WITH RECURSIVE child_region(name, id, parent_id) AS ( " +
				"  SELECT " +
				"    name, " +
				"    id, " +
				"    parent_id " +
				"  FROM system.location " +
				"  WHERE id = :location_id " +
				"  UNION ALL SELECT " +
				"              p.name, " +
				"              p.id, " +
				"              p.parent_id " +
				"            FROM system.location p JOIN child_region ON p.id = child_region.parent_id) " +
				"SELECT id " +
				"FROM child_region")
//@formatter:on
@EntityWrapperDef(name = ILocation.WRAPPER_NAME)
public class Location extends BusinessObject implements LocationContainer, Printable {

	private static final long serialVersionUID = -4582165636481204124L;

	public static final String FIND_PARENTS = "Location.findParents";
	public static final String PARENT_QUERY_PARAM = "parent";

	@Column(length = 128, nullable = false)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private Location parent;

	@ManyToOne(fetch = FetchType.LAZY)
	private District district;

	@OneToMany(mappedBy = "parent")
	private List<Location> children = new ArrayList<>();

	@Column(length = 512)
	private String landmark;

	@Column
	private Long oktmo;

	@Version
	@Temporal(TemporalType.TIMESTAMP)
	private Date version;

	protected Location() {
	}

	public Location(Long id) {
		super(id);
	}

	@Override
	public String getObjectName() {
		return name;
	}

	@Override
	public AddressRdo createReportData() {
		//@formatter:off
		return AddressRdo.builder()
					.id(getId())
					.fullName(getFullName())
				.build();
		//@formatter:on
	}

	// FIXME: разобраться как в большом аргусе получали структурное наименование и сделать аналогично
	public String getFullName() {
		return getNameBefore(U);
	}

	public String getNameBefore(LocationClass locationClass) {
		List<Location> parents = new ArrayList<>();
		Location location = this;
		while (location.getParent() != null
				&& !initializeAndUnproxy(location.getParent()).getClass().equals(locationClass.getClazz())) {
			parents.add(location.getParent());
			location = location.getParent();
		}
		StringBuilder fullNameBuilder = new StringBuilder();
		for (int i = parents.size() - 1; i >= 0; i--) {
			fullNameBuilder.append(parents.get(i).getObjectName()).append(", ");
		}
		fullNameBuilder.append(getObjectName());
		return fullNameBuilder.toString();
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	/**
	 * Метод должен быть <b>PROTECTED</b>. Добавляет дочерний адресообразующий элемент для текущего(this).
	 * 
	 * @param childLocation
	 *            добавляемый элемент.
	 */
	protected void addChild(Location childLocation) {
		children.add(childLocation);
		childLocation.setParent(this);
	}

	/**
	 * Метод должен быть <b>PROTECTED</b>. Удаляет дочерний адресообразующий элемент у текущего(this).
	 *
	 * @param childLocation
	 *            удаляемый элемент.
	 */
	protected void removeChild(Location childLocation) {
		children.remove(childLocation);
		childLocation.setParent(null);
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	/**
	 * @return Наименование адресообразующего элемента.
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Родитель для текущего адресообразующего элемента.
	 */
	public Location getParent() {
		return parent;
	}

	public void setParent(Location parent) {
		this.parent = parent;
	}

	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	/**
	 * @return Список всех дочерних адресообразующих элементов(неизменяемый).
	 */
	public List<Location> getChildren() {
		return Collections.unmodifiableList(children);
	}

	/**
	 * Комментарий к местоположению региона или улицы региона - дополнительная информация, уточняющая при необходимости
	 * местоположение объектов адресации относительно ориентиров на местности.
	 * 
	 * @return Ориетир для адресообразующего элемента.
	 */
	public String getLandmark() {
		return landmark;
	}

	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}

	/**
	 * Код муниципального образования по Общероссийскому классификатору территорий муниципальных образований, на
	 * территории которого расположен адресуемый объект.
	 * 
	 * @return Код ОКТМО.
	 */
	public Long getOktmo() {
		return oktmo;
	}

	public void setOktmo(Long oktmo) {
		this.oktmo = oktmo;
	}

	// *****************************************************************************************************************
	// Inner classes
	// *****************************************************************************************************************

	public static class LocationQuery<T extends Location> extends EntityQuery<T> {

		EntityQueryStringFilter<T> name = createStringFilter(Location_.name);
		EntityQueryEntityFilter<T, Location> parent = createEntityFilter(Location_.parent);
		EntityQueryEntityFilter<T, District> district = createEntityFilter(Location_.district);
		EntityQueryStringFilter<T> landmark = createStringFilter(Location_.landmark);
		EntityQueryNumericFilter<T, Long> oktmo = createNumericFilter(Location_.oktmo);

		public LocationQuery(Class<T> entityClass) {
			super(entityClass);
		}

		public EntityQueryStringFilter<T> name() {
			return name;
		}

		public EntityQueryEntityFilter<T, Location> parent() {
			return parent;
		}

		public EntityQueryEntityFilter<T, District> district() {
			return district;
		}

		EntityQueryStringFilter<T> landmark() {
			return landmark;
		}

		EntityQueryNumericFilter<T, Long> oktmo() {
			return oktmo;
		}

	}

}