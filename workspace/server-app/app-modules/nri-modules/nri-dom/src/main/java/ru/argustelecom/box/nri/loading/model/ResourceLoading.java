package ru.argustelecom.box.nri.loading.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.nri.loading.model.nls.ResourceLoadingMessagesBundle;
import ru.argustelecom.box.nri.logicalresources.model.LogicalResource;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuerySimpleFilter;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Занятие ресурса
 * Created by s.kolyada on 19.12.2017.
 */
@Entity
@Table(schema = "nri", name = "resource_loading")
@Access(AccessType.FIELD)
@Getter
@Setter
public class ResourceLoading extends BusinessObject implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Время создания нагрузки
	 */
	@Column(name = "created")
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationTime = new Date();

	/**
	 * Список логических ресурсов
	 */
	@OneToMany(mappedBy = "resourceLoading")
	private Set<LogicalResource> loadedLogicalResource = new HashSet<>();

	/**
	 * Список ресурсов
	 */
	@OneToMany(mappedBy = "resourceLoading")
	private Set<ResourceInstance> loadedResource = new HashSet<>();

	/**
	 * Имя нагрузки
	 */
	@Column(name = "loading_name", nullable = false)
	private String loadingName;

	/**
	 * Экземпляр услуги
	 */
	@ManyToOne
	@JoinColumn(name = "service_id", nullable = false)
	private Service serviceInstance;

	/**
	 * Конструктор по умолчанию
	 */
	protected ResourceLoading() {
	}

	/**
	 * Конструктор
	 * @param id идентификатор
	 * @param loadedLogicalResource нагруженный ресурс
	 * @param serviceInstance услуга
	 */
	@Builder
	public ResourceLoading(Long id, Set<LogicalResource> loadedLogicalResource, Service serviceInstance) {
		this.id = id;
		this.creationTime = new Date();
		ResourceLoadingMessagesBundle messages = LocaleUtils.getMessages(ResourceLoadingMessagesBundle.class);
		this.loadingName = messages.loadingForResources() + StringUtils.join(loadedLogicalResource.iterator(),',');
		this.loadedLogicalResource = loadedLogicalResource;
		this.serviceInstance = serviceInstance;
	}

	@Override
	public String getObjectName() {
		return "#" + id + " " + loadingName;
	}

	/**
	 * Запрос к данному типу
	 */
	public static class ResourceLoadingQuery extends EntityQuery<ResourceLoading> {

		/**
		 * фильтр по службе
		 */
		private EntityQuerySimpleFilter<ResourceLoading, Service> serviceInstance;

		/**
		 * Конструктор запроса
		 */
		public ResourceLoadingQuery() {
			super(ResourceLoading.class);
			serviceInstance = createFilter(ResourceLoading_.serviceInstance);
		}

		public EntityQuerySimpleFilter<ResourceLoading, Service> getServiceInstance() {
			return serviceInstance;
		}
	}


}

