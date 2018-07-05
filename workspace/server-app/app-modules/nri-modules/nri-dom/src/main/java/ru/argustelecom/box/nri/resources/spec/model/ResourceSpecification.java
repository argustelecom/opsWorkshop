package ru.argustelecom.box.nri.resources.spec.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.nri.logicalresources.model.LogicalResourceType;
import ru.argustelecom.box.nri.ports.PortTypeEnumListConverter;
import ru.argustelecom.box.nri.ports.model.PortType;
import ru.argustelecom.box.nri.resources.lifecycle.model.ResourceLifecycle;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Спецификация ресурса
 * Created by s.kolyada on 18.09.2017.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "nri", name = "resource_specification")
@Getter
@Setter
public class ResourceSpecification extends BusinessObject {

	private static final long serialVersionUID = 1L;

	/**
	 * Имя спецификации
	 */
	@Column(name = "name", nullable = false)
	private String name;

	/**
	 * Класс иконки спецификации
	 */
	@Column(name = "image")
	private String image;

	/**
	 * Является ли данный ресурс самостоятельным,
	 * т.е. ресурсом, который не обязательно должен входить в другие ресурсы,
	 * а может выражаться в качестве независимого ресурса в техническом учёте.
	 *
	 * Например: коммутатор является самостоятельным ресурсом, а порт нет, тк порт обязатльно
	 * должен входить в какой-либо ресурс, который бы по отношению к нему был в роли контейнера
	 */
	@Column(name = "independent", nullable = false)
	private Boolean isIndependent = true;

	/**
	 * Параметры ресурса
	 */
	@OneToMany(mappedBy = "resourceSpecification")
	private List<ParameterSpecification> parameters = new ArrayList<>();

	/**
	 * Дочерние спецификации, для которых эта спецификация может являться контейнером
	 */
	@ManyToMany
	@JoinTable(schema = "nri", name = "resource_specification_children",
		joinColumns        = {@JoinColumn(name = "parent_spec_id", nullable = false, updatable = false)},
		inverseJoinColumns = {@JoinColumn(name = "child_spec_id",  nullable = false, updatable = false)})
	private List<ResourceSpecification> childSpecifications = new ArrayList<>();

	/**
	 * Поддерживаемые типы подключаемых логических ресурсов
	 */
	@ElementCollection
	@CollectionTable(schema="nri", name = "resource_specification_supported_logical_types",
			joinColumns = @JoinColumn( name = "resource_specification_id"))
	@Column(name = "supported_type")
	@Enumerated(EnumType.STRING)
	private Set<LogicalResourceType> supportedLogicalResources = new HashSet<>();

	/**
	 * Жизненный цикл спецификации
	 */
	@ManyToOne
	@JoinColumn(name = "resource_lifecycle_id")
	private ResourceLifecycle lifecycle;

	/**
	 * Поддерживаемые спецификацией типы портов
	 */
	@Column(name = "supported_port_types")
	@Convert(converter = PortTypeEnumListConverter.class)
	private Set<PortType> supportedPortTypes;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected ResourceSpecification() {
		super();
	}

	/**
	 * Конструктор
	 * @param id идентификатор
	 * @param name имя
	 * @param parameters параметры
	 * @param childSpecifications дочерние спецификации
	 * @param image класс иконки спецификации
	 * @param isIndependent самостоятельность ресурса
	 * @param lifecycle жизненный цикл ресурса
	 * @param supportedLogicalResources поддерживаемые логические ресурсы
	 */
	@Builder
	public ResourceSpecification(Long id, String name, List<ParameterSpecification> parameters,
								 List<ResourceSpecification> childSpecifications, String image,
								 Boolean isIndependent, ResourceLifecycle lifecycle, Set<LogicalResourceType> supportedLogicalResources,
								 Set<PortType> supportedPortTypes) {
		super(id);
		this.name = name;
		this.isIndependent = isIndependent;
		this.parameters = Optional.ofNullable(parameters).orElse(new ArrayList<>());
		this.childSpecifications = Optional.ofNullable(childSpecifications).orElse(new ArrayList<>());
		this.supportedLogicalResources = Optional.ofNullable(supportedLogicalResources).orElse(Collections.emptySet());
		this.supportedPortTypes = Optional.ofNullable(supportedPortTypes).orElse(Collections.emptySet());
		this.image = image;
		this.lifecycle = lifecycle;
	}

	/**
	 * Проверяет, поддерживается ли логический ресурс данной спецификацией
	 * @param logicalResourceType тип логичческого ресурса
	 * @return истина если поддерживается, иначе ложь
	 */
	public boolean supportsLogicalResource(LogicalResourceType logicalResourceType) {
		return !CollectionUtils.isEmpty(supportedLogicalResources)
				&& supportedLogicalResources.contains(logicalResourceType);
	}

	/**
	 * Проверяет, поддерживается ли тип порта данной спецификацией
	 * @param portType тип порта
	 * @return истина если поддерживается, иначе ложь
	 */
	public boolean supportsPortType(PortType portType) {
		return !CollectionUtils.isEmpty(supportedPortTypes)
				&& supportedPortTypes.contains(portType);
	}

	public List<ParameterSpecification> getParameters() {
		return Collections.unmodifiableList(parameters);
	}

	public List<ResourceSpecification> getChildSpecifications() {
		return Collections.unmodifiableList(childSpecifications);
	}

	public Set<LogicalResourceType> getSupportedLogicalResources() {
		return Collections.unmodifiableSet(supportedLogicalResources);
	}

	public Set<PortType> getSupportedPortTypes() {
		return Collections.unmodifiableSet(supportedPortTypes);
	}
}
