package ru.argustelecom.box.nri.schema.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.commodity.model.ServiceSpec;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.nri.schema.requirements.model.ResourceRequirement;
import ru.argustelecom.box.nri.schema.requirements.resources.model.RequiredItem;
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
import java.util.Optional;

/**
 * Требование к ресурсам
 * Описывает то, какими ресурсами может быть предоставлена услуга
 * Created by s.kolyada on 18.09.2017.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "nri", name = "resource_schema")
@Getter
@Setter
public class ResourceSchema extends BusinessObject {

	private static final long serialVersionUID = 1L;

	/**
	 * Имя
	 */
	@Column(name = "name", nullable = false)
	private String name;

	/**
	 * Спецификация услуги, для которой эти требования
	 */
	@ManyToOne
	@JoinColumn(name = "specification_id", nullable = false)
	private ServiceSpec serviceSpecification;

	/**
	 * Требования к конкретным ресурсам и/или их параметрам
	 */
	@OneToMany(mappedBy = "tpSchema", cascade = CascadeType.ALL)
	private List<RequiredItem> requirements = new ArrayList<>();

	/**
	 * Требования по бронированию ресурсов
	 */
	@OneToMany(mappedBy = "bookSchema", cascade = CascadeType.ALL)
	private List<ResourceRequirement> bookings = new ArrayList<>();

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected ResourceSchema() {
		super();
	}

	/**
	 * Конструктор
	 *
	 * @param id                   идентификатор
	 * @param serviceSpecification спецификация услуги
	 * @param requirements         требования
	 * @param name                 имя схемы
	 */
	@Builder
	public ResourceSchema(Long id, ServiceSpec serviceSpecification, List<RequiredItem> requirements, String name) {
		super(id);
		this.name = name;
		this.serviceSpecification = serviceSpecification;
		this.requirements = Optional.ofNullable(requirements).orElse(new ArrayList<>());
	}

	/**
	 * Добавить требуемый ресурс
	 *
	 * @param requiredItem требуемый ресурс
	 * @return истина, если удалось, иначе ложь
	 */
	public boolean addRequirement(RequiredItem requiredItem) {
		return this.requirements.add(requiredItem);
	}

	/**
	 * Удалить требуемый ресурс
	 *
	 * @param requiredItem требуемый ресурс
	 * @return истина, если удалось, иначе ложь
	 */
	public boolean removeRequirement(RequiredItem requiredItem) {
		return this.requirements.remove(requiredItem);
	}

	public List<RequiredItem> getRequirements() {
		return Collections.unmodifiableList(requirements);
	}

	@Override
	public String getObjectName() {
		return name;
	}

	/**
	 * Запрос на получение схемы
	 */
	public static class ResourceSchemaQuery extends EntityQuery<ResourceSchema> {
		private EntityQueryEntityFilter<ResourceSchema, ServiceSpec> serviceSpecification
				= createEntityFilter(ResourceSchema_.serviceSpecification);

		/**
		 * Конструкторв
		 */
		public ResourceSchemaQuery() {
			super(ResourceSchema.class);
		}

		/**
		 * фильтрация по точке монтирования
		 *
		 * @return фильтр
		 */
		public EntityQueryEntityFilter<ResourceSchema, ServiceSpec> serviceSpecification() {
			return serviceSpecification;
		}

	}
}
