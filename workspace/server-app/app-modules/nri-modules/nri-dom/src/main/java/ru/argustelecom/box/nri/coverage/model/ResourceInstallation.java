package ru.argustelecom.box.nri.coverage.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.nri.building.model.BuildingElement;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Представление об установки ресурса относительно структуры здания
 * Так же включает в себя понятие зоны покрытия
 * Created by s.kolyada on 31.08.2017.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "nri", name = "resource_installation")
@Getter
@Setter
public class ResourceInstallation extends BusinessObject {

	private static final long serialVersionUID = 1L;

	/**
	 * Комментарий к установке
	 */
	@Column(name = "comment")
	private String comment;

	/**
	 * Место монтирования
	 */
	@ManyToOne
	@JoinColumn(name = "installation_building_element_id", nullable = false)
	public BuildingElement installedAt;

	/**
	 * Корневой узел дома, в котором находится установка
	 * нужен для того, что бы при поиске геокоординат строения по инсталляциям не требовалось
	 * переберать всю иерархию ресурсов + возможность быстро находить все установки по дому
	 * Т.к. мы не поддерживаем кейс переноса инсталляцие за пределы 1 строения,
	 * то это поле можно выставлять всего 1 раз при создании установки
	 * Сделано в рамках BOX-2533
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "installation_building_element_root_id", nullable = false)
	public BuildingElement installationRoot;

	/**
	 * Элементы строения, входящие в зону покрытия
	 */
	@ManyToMany
	@JoinTable(schema = "nri", name = "resource_installation_coverage",
			joinColumns = {@JoinColumn(name = "installation_id", nullable = false, updatable = false)},
			inverseJoinColumns = {@JoinColumn(name = "building_element_id", nullable = false, updatable = false)})
	public List<BuildingElement> cover = new ArrayList<>();

	/**
	 * Ресурс, к которому относится данная установка
	 */
	@OneToOne
	@PrimaryKeyJoinColumn
	private ResourceInstance resource;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected ResourceInstallation() {
		super();
	}

	/**
	 * Конструктор
	 * @param id идентификатор
	 * @param comment комментарий
	 * @param installedAt точка монтирования
	 * @param installationRoot корневая точка монтирования(строение)
	 * @param cover элемнты покрываемые
	 * @param resource ресурс
	 */
	@Builder
	public ResourceInstallation(Long id, String comment, BuildingElement installedAt, BuildingElement installationRoot,
								List<BuildingElement> cover, ResourceInstance resource) {
		super(id);
		this.comment = comment;
		this.installedAt = installedAt;
		if (cover == null) {
			this.cover = new ArrayList<>();
		} else {
			this.cover = cover;
		}
		this.resource = resource;
		this.installationRoot = installationRoot;
	}

	/**
	 * Запрос к данному типу
	 */
	public static class ResourceInstallationQuery extends EntityQuery<ResourceInstallation> {

		private EntityQueryEntityFilter<ResourceInstallation, BuildingElement> installedAt = createEntityFilter(ResourceInstallation_.installedAt);

		private EntityQueryEntityFilter<ResourceInstallation, BuildingElement> installationRoot = createEntityFilter(ResourceInstallation_.installationRoot);

		/**
		 * Конструкторв
		 */
		public ResourceInstallationQuery() {
			super(ResourceInstallation.class);
		}

		/**
		 * фильтрация по точке монтирования
		 * @return фильтр
		 */
		public EntityQueryEntityFilter<ResourceInstallation, BuildingElement> installedAt() {
			return installedAt;
		}

		/**
		 * фильтрация по корню точки монтирования
		 * @return фильтр
		 */
		public EntityQueryEntityFilter<ResourceInstallation, BuildingElement> installationRoot() {
			return installationRoot;
		}
	}
}
