package ru.argustelecom.box.nri.booking.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.nri.booking.model.nls.BookingOrderMessagesBundle;
import ru.argustelecom.box.nri.logicalresources.model.LogicalResource;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;
import ru.argustelecom.box.nri.schema.requirements.model.ResourceRequirement;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuerySimpleFilter;
import ru.argustelecom.system.inf.utils.CheckUtils;

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
 * Наряд на бронирование ресурса
 * Created by s.kolyada on 06.12.2017.
 */
@Entity
@Table(schema = "nri", name = "booking_order")
@Access(AccessType.FIELD)
@Getter
@Setter
public class BookingOrder extends BusinessObject implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * Время создания брони
	 */
	@Column(name = "created")
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationTime = new Date();

	@OneToMany(mappedBy = "bookingOrder")
	private Set<LogicalResource> bookedLogicalResource = new HashSet<>();

	/**
	 * Список забронированных ресурсов
	 */
	@OneToMany(mappedBy = "bookingOrder")
	private Set<ResourceInstance> bookedResource = new HashSet<>();

	@Column(name = "order_name", nullable = false)
	private String orderName;

	/**
	 * Экземпляр услуги
	 */
	@ManyToOne
	@JoinColumn(name = "service_id", nullable = false)
	private Service serviceInstance;

	/**
	 * Ссылка на требование, по которому был создан наряд на бронирование
	 */
	@ManyToOne
	@JoinColumn(name = "requirement_id")
	private ResourceRequirement requirement;

	/**
	 * Конструктор по умолчанию
	 */
	protected BookingOrder() {
	}

	/**
	 * Конструктор
	 * @param id идентификатор
	 * @param bookedLogicalResource забронированный ресурс
	 * @param serviceInstance услуга
	 * @param requirement требование на бронивароние
	 */
	@Builder
	public BookingOrder(Long id, Set<LogicalResource> bookedLogicalResource, Service serviceInstance,
						ResourceRequirement requirement) {


		CheckUtils.checkArgument(CollectionUtils.isNotEmpty(bookedLogicalResource),
				LocaleUtils.getMessages(BookingOrderMessagesBundle.class).unacceptableToCreateBookingOrderWithoutResources());
		this.id = id;
		this.creationTime = new Date();
		if (bookedLogicalResource.size() > 1) {
			StringBuilder builder = new StringBuilder(getDefaultOrderName());
			for (LogicalResource resource : bookedLogicalResource) {
				if (builder.length() + StringUtils.length(resource.getObjectName()) > 125) {
					break;
				}
				builder.append(resource.getObjectName());
			}
			builder.append("...");
			this.orderName = builder.toString();
		} else {
			this.orderName = getDefaultOrderName() + bookedLogicalResource.iterator().next().getObjectName();
		}
		this.bookedLogicalResource = bookedLogicalResource;
		this.serviceInstance = serviceInstance;
		this.requirement = requirement;
	}

	@Override
	public String getObjectName() {
		return "#" + id + " " + orderName;
	}

	/**
	 * Сделано так что бы при смене локали изменялось имя
	 * @return имя по умолчанию
	 */
	private String getDefaultOrderName(){
		return LocaleUtils.getMessages(BookingOrderMessagesBundle.class).defaultOrderName();
	}
	/**
	 * Запрос к данному типу
	 */
	public static class ResourceLoadingQuery extends EntityQuery<BookingOrder> {

		/**
		 * фильтр по службе
		 */
		private EntityQuerySimpleFilter<BookingOrder, Service> serviceInstance;
		/**
		 * фильтр по требованию
		 */
		private EntityQuerySimpleFilter<BookingOrder, ResourceRequirement> requirement;

		/**
		 * Конструктор запроса
		 */
		public ResourceLoadingQuery() {
			super(BookingOrder.class);
			serviceInstance = createFilter(BookingOrder_.serviceInstance);
			requirement = createFilter(BookingOrder_.requirement);
		}

		public EntityQuerySimpleFilter<BookingOrder, Service> getServiceInstance() {
			return serviceInstance;
		}
		public  EntityQuerySimpleFilter<BookingOrder, ResourceRequirement> getRequirement() {
			return requirement;
		}
	}
}
