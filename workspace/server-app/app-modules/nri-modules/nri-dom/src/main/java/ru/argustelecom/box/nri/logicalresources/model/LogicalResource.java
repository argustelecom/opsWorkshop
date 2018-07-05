package ru.argustelecom.box.nri.logicalresources.model;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.nri.booking.model.BookingOrder;
import ru.argustelecom.box.nri.loading.model.ResourceLoading;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;
import ru.argustelecom.system.inf.modelbase.NamedObject;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.io.Serializable;

/**
 * Общее представление логического ресурса
 * Created by s.kolyada on 27.10.2017.
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Access(AccessType.FIELD)
@Getter
@Setter
public abstract class LogicalResource extends BusinessObject implements Serializable, NamedObject {

	/**
	 * Тип логического ресурса
	 */
	@Transient
	protected LogicalResourceType type;

	/**
	 * Имя логического ресурса
	 */
	@Column(length = 128, nullable = false)
	protected String name;

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
	 * Ресурс, к которому относится данный номер
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "resource_id")
	private ResourceInstance resource;

	/**
	 * Поле с версиией нужно для конкурентного доступа
	 */
	@Version
	private Long version;

	/**
	 * Конструктор
	 * @param type тип логического ресурса
	 */
	public LogicalResource(LogicalResourceType type) {
		super();
		this.type = type;
	}

	@Override
	public String getObjectName() {
		return name;
	}
}
