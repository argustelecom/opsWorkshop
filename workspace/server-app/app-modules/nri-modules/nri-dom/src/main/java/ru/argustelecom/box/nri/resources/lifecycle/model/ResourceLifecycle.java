package ru.argustelecom.box.nri.resources.lifecycle.model;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.nri.resources.spec.model.ResourceSpecification;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.modelbase.NamedObject;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Жизненный цикл ресурса
 * Created by s.kolyada on 02.11.2017.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "nri", name = "resource_lifecycle")
@Getter
@Setter
public class ResourceLifecycle extends BusinessObject implements Serializable, NamedObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "name", nullable = false)
	private String name;

	/**
	 * Все фазы жизненного цикла
	 */
	@OneToMany(mappedBy = "currentLifecycle", cascade = CascadeType.ALL)
	private Set<ResourceLifecyclePhase> phases = new HashSet<>();

	/**
	 * Начальная фаза ЖЦ
	 */
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "initial_lifecycle_phase_id")
	private ResourceLifecyclePhase initialPhase;

	/**
	 * Список спецификаций ресурсов, которые имеют этот ЖЦ
	 */
	@OneToMany(mappedBy = "lifecycle")
	private List<ResourceSpecification> supportingSpecifications = new ArrayList<>();

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected ResourceLifecycle() {
		super();
	}

	/**
	 * Конструктор
	 * @param id идентификатор
	 */
	public ResourceLifecycle(Long id) {
		super(id);
	}

	@Override
	public String getObjectName() {
		return name;
	}

	/**
	 * Запрос
	 */
	public static class ResourceLifecycleQuery extends EntityQuery<ResourceLifecycle> {
		/**
		 * конструктор
		 */
		public ResourceLifecycleQuery() {
			super(ResourceLifecycle.class);
		}
	}
}
