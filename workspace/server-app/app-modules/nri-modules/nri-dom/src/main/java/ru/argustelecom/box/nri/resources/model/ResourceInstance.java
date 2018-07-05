package ru.argustelecom.box.nri.resources.model;

import com.google.common.base.Verify;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.nri.booking.model.BookingOrder;
import ru.argustelecom.box.nri.coverage.model.ResourceInstallation;
import ru.argustelecom.box.nri.loading.model.ResourceLoading;
import ru.argustelecom.box.nri.logicalresources.model.LogicalResource;
import ru.argustelecom.box.nri.ports.model.Port;
import ru.argustelecom.box.nri.resources.lifecycle.model.ResourceLifecyclePhase;
import ru.argustelecom.box.nri.resources.spec.model.ResourceSpecification;
import ru.argustelecom.box.nri.resources.spec.model.ResourceSpecification_;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryLogicalFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuerySimpleFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Экземпляр ресруса
 * Created by s.kolyada on 18.09.2017.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "nri", name = "resource_instance")
@Getter
@Setter
@EntityListeners(ResourceInstanceLifecycleInitializer.class)
public class ResourceInstance extends LogicalResourceHolder {

	private static final long serialVersionUID = 1L;

	/**
	 * Имя ресурса
	 */
	@Column(name = "name", nullable = false)
	private String name;

	/**
	 * Параметры ресурса
	 */
	@OneToMany(mappedBy = "resource", cascade = CascadeType.ALL)
	private List<ParameterValue> parameterValues = new ArrayList<>();

	/**
	 * Стутус
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private ResourceStatus status = ResourceStatus.defaultStatus();

	/**
	 * Точка монтирования
	 */
	@OneToOne(mappedBy = "resource", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private ResourceInstallation installation;

	/**
	 * Родительский элемент
	 */
	@ManyToOne
	@JoinColumn(name = "parent_res_id", referencedColumnName="id")
	private ResourceInstance parent;

	/**
	 * Дочерние ресурсы
	 */
	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ResourceInstance> children = new ArrayList<>();

	/**
	 * Текущая фаза ЖЦ ресурса
	 */
	@ManyToOne
	@JoinColumn(name = "lifecycle_phase_id")
	private ResourceLifecyclePhase currentLifecyclePhase;

	/**
	 * Бронь на логический ресурс
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "booking_order_id")
	protected BookingOrder bookingOrder;

	/**
	 * Нагрузка на логический ресурс
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "loading_id")
	protected ResourceLoading resourceLoading;

	/**
	 * Порты на данном ресурсе
	 */
	@Setter(value = AccessLevel.NONE)
	@OneToMany(mappedBy = "resource", fetch = FetchType.LAZY)
	private List<Port> ports = new ArrayList<>();

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected ResourceInstance() {
		super();
	}

	/**
	 * Конструктор для ленивой инициализации ресурса.
	 * Осторожно! используется в criteriaQuery неявно через multiselect()
	 * (смотри ResoruceInstanceAppService.showSearchResult())
	 * @param id id
	 * @param name имя
	 * @param specification спецификация
	 * @param status статус
	 */
	public ResourceInstance(Long id, String name, ResourceSpecification specification, ResourceStatus status) {
		super(id);
		this.name = name;
		this.specification = specification;
		this.status = status;
	}

	/**
	 * Конструктор
	 * @param id id
	 * @param name имя
	 * @param specification спецификация
	 * @param parameterValues параметры
	 * @param status статус
	 * @param children дочерние ресурсы
	 * @param services связанные услуги
	 * @param logicalResources логические ресурсы
	 */
	@Builder
	public ResourceInstance(Long id, String name, ResourceSpecification specification,
			List<ParameterValue> parameterValues, ResourceStatus status, List<ResourceInstance> children,
			List<Service> services, ResourceLifecyclePhase currentLifecyclePhase, List<LogicalResource> logicalResources) {
		super(id);
		this.specification = specification;
		this.parameterValues = Optional.ofNullable(parameterValues).orElse(new ArrayList<>());
		this.status = status;
		this.children = Optional.ofNullable(children).orElse(new ArrayList<>());
		this.name = name;
		this.currentLifecyclePhase = currentLifecyclePhase;
		this.setLogicalResources(Optional.ofNullable(logicalResources).orElse(new ArrayList<>()));
	}

	public List<ParameterValue> getParameterValues() {
		return Collections.unmodifiableList(parameterValues);
	}

	public List<ResourceInstance> getChildren() {
		return Collections.unmodifiableList(children);
	}

	/**
	 * Добавить дочерний ресурс
	 * @param child дочерний ресурс
	 * @return итсина в случае успеха, иначе ложь
	 */
	public Boolean addChild(ResourceInstance child) {
		child.setParent(this);
		return this.children.add(child);
	}

	/**
	 * Удалить дочерний ресурс
	 * @param child дочерний ресурс
	 * @return итсина в случае успеха, иначе ложь
	 */
	public Boolean removeChild(ResourceInstance child) {
		return this.children.remove(child);
	}

	/**
	 * Сплюснуть дерево ресурсов в стрим
	 * @return стрим ресурсов
	 */
	public Stream<ResourceInstance> flattened() {
		return Stream.concat(
				Stream.of(this),
				children.stream().flatMap(ResourceInstance::flattened));
	}

	/**
	 * Получить список портов
	 * @return список портов
	 */
	public List<Port> getPorts() {
		return Collections.unmodifiableList(ports);
	}

	/**
	 * Добавить порт
	 * Проверяет, поддерживает ли спецификация данного оборудование порты такого типа
	 * @param port порт
	 * @return истина, если порт добавлен, иначе ложь
	 */
	public boolean addPort(Port port) {
		Verify.verifyNotNull(port);
		if (!specification.supportsPortType(port.getType())) {
			return false;
		}
		return ports.add(port);
	}

	/**
	 * Класс для создания criteriaQuery c фильтрами по полям
	 */
	public static class ResourceInstanceQuery extends EntityQuery<ResourceInstance> {

		/**
		 * Фильтр имени
		 */
		private EntityQueryStringFilter<ResourceInstance> name;

		/**
		 * фильтр спецификации
		 */
		private EntityQueryStringFilter<ResourceInstance> spec;

		/**
		 * фильтр статуса
		 */
		private EntityQuerySimpleFilter<ResourceInstance, ResourceStatus> status;

		/**
		 * Фильтр флага независимости
		 */
		private EntityQueryLogicalFilter<ResourceInstance> independent;

		/**
		 * конструктор
		 */
		public ResourceInstanceQuery() {
			super(ResourceInstance.class);
			name = createStringFilter(ResourceInstance_.name);
			spec = createStringFilter(this.root().get(ResourceInstance_.specification.getName())
							.get(ResourceSpecification_.name.getName()),
					ResourceSpecification_.name);
			status = createFilter(ResourceInstance_.status);
			independent = createLogicalFilter(
					this.root().get(ResourceInstance_.specification.getName())
							.get(ResourceSpecification_.isIndependent.getName()),
					ResourceSpecification_.isIndependent);

		}

		/**
		 * получить фильтр имени
		 * @return фильтр имени
		 */
		public EntityQueryStringFilter<ResourceInstance> name() {
			return name;
		}

		/**
		 * получить фильтр спецификации
		 * @return фильтр спецификации
		 */
		public EntityQueryStringFilter<ResourceInstance> spec() {
			return spec;
		}

		/**
		 * получить фильтр статуса
		 * @return фильтр статуса
		 */
		public EntityQuerySimpleFilter<ResourceInstance, ResourceStatus> status() {
			return status;
		}

		/**
		 * получить фильтр независимости
		 * @return фильтр независимости
		 */
		public EntityQueryLogicalFilter<ResourceInstance> independent() {
			return independent;
		}
	}
}
