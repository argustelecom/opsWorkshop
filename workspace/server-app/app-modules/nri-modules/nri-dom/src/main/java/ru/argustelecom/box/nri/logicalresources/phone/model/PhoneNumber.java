package ru.argustelecom.box.nri.logicalresources.phone.model;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.activity.attachment.model.AttachmentContext;
import ru.argustelecom.box.env.activity.attachment.model.HasAttachments;
import ru.argustelecom.box.env.activity.comment.model.CommentContext;
import ru.argustelecom.box.env.activity.comment.model.HasComments;
import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.nri.booking.model.BookingOrder;
import ru.argustelecom.box.nri.inf.dataaccess.entityquery.ExtendedEntityQuery;
import ru.argustelecom.box.nri.inf.dataaccess.entityquery.ExtendedEntityQueryStringFilter;
import ru.argustelecom.box.nri.logicalresources.model.LogicalResource;
import ru.argustelecom.box.nri.logicalresources.model.LogicalResourceType;
import ru.argustelecom.box.nri.logicalresources.phone.lifecycle.PhoneNumberState;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Телефонный номер - логический ресурс
 * Created by s.kolyada on 27.10.2017.
 */
@Entity
@EntityListeners(PhoneNumberEntityListener.class)
@Table(schema = "nri", name = "phone_number")
@Access(AccessType.FIELD)
@Getter
@Setter
public class PhoneNumber extends LogicalResource implements LifecycleObject<PhoneNumberState>,
		HasComments, HasAttachments {

	private static final long serialVersionUID = 1L;

	/**
	 * Цифровое представление номера
	 * Служебный параметр. Явно не выставляется, заполняется автоматически при сохранении в БД
	 * Используется для ускорения поиска по номеру от внешних систем и ручного поиска без учёта форматирования
	 * Заполняется через {@link PhoneNumberEntityListener#recalculate(PhoneNumber)}
	 * см. BOX-2197
	 */
	@Column(name = "digits", nullable = false)
	private String digits;

	/**
	 * Статус телефонного номера
	 */
	@Column(name = "number_state", nullable = false)
	@Enumerated(EnumType.STRING)
	private PhoneNumberState state = PhoneNumberState.AVAILABLE;

	/**
	 * Время послденего изменения статуса
	 */
	@Column(name = "state_change_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date stateChangeDate = new Date();

	/**
	 * Пул номеров, к которому относится данный номер
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "phone_number_pool_id")
	private PhoneNumberPool pool;

	/**
	 * Спецификация телефонного номера
	 */
	@OneToOne(fetch = FetchType.EAGER, mappedBy = "phoneNumber", optional = false, cascade = {CascadeType.ALL})
	private PhoneNumberSpecificationInstance specInstance;

	/**
	 * Прикрепленные файлы
	 */
	@OneToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "attachment_context_id", insertable = true, updatable = false)
	private AttachmentContext attachmentContext;

	/**
	 * Комментарии
	 */
	@OneToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "comment_context_id", insertable = true, updatable = false)
	private CommentContext commentContext;

	/**
	 * Конструктор по умаолчанию
	 */
	public PhoneNumber() {
		super(LogicalResourceType.PHONE_NUMBER);
	}

	/**
	 * Конструктор по id
	 *
	 * @param id идентификатор
	 */
	public PhoneNumber(Long id) {
		super(LogicalResourceType.PHONE_NUMBER);
		commentContext = new CommentContext(id);
		attachmentContext = new AttachmentContext(id);
		this.id = id;
	}

	@Override
	public PhoneNumberState getState() {
		return state;
	}

	@Override
	public void setState(PhoneNumberState state) {
		this.state = state;
		this.stateChangeDate = new Date();
	}

	/**
	 * Класс для создания criteriaQuery c фильтрами по полям
	 */
	public static class PhoneNumberQuery extends ExtendedEntityQuery<PhoneNumber> {

		/**
		 * Фильтр имени
		 */
		private EntityQueryStringFilter<PhoneNumber> name;

		/**
		 * Фильтр по цифровому представлению номера
		 */
		private ExtendedEntityQueryStringFilter<PhoneNumber> digits;

		/**
		 * Фильтр статуса
		 */
		private EntityQuerySimpleFilter<PhoneNumber, PhoneNumberState> state;

		/**
		 * Фильтр пула
		 */
		private EntityQuerySimpleFilter<PhoneNumber, PhoneNumberPool> pool;

		/**
		 * Фильтр спецификации
		 */
		private EntityQuerySimpleFilter<PhoneNumber, PhoneNumberSpecification> specification;

		/**
		 * Фильтр по бронированию
		 */
		private EntityQuerySimpleFilter<PhoneNumber, BookingOrder> bookingOrder;

		/**
		 * Конструктор
		 */
		public PhoneNumberQuery() {
			super(PhoneNumber.class);
			name = createStringFilter(PhoneNumber_.name);
			state = createFilter(PhoneNumber_.state);
			pool = createFilter(PhoneNumber_.pool);
			digits = createExtendedStringFilter(PhoneNumber_.digits);
			specification = createFilter(this.root().get(PhoneNumber_.specInstance).get(PhoneNumberSpecificationInstance_.type), PhoneNumberSpecificationInstance_.type);
			bookingOrder = createFilter(PhoneNumber_.bookingOrder);
		}

		/**
		 * Получить фильтр имени
		 *
		 * @return фильтр имени
		 */
		public EntityQueryStringFilter<PhoneNumber> name() {
			return name;
		}

		/**
		 * Получить фильтр статуса
		 *
		 * @return фильтр статуса
		 */
		public EntityQuerySimpleFilter<PhoneNumber, PhoneNumberState> state() {
			return state;
		}

		/**
		 * Получить фильтр пула
		 *
		 * @return фильтр пула
		 */
		public EntityQuerySimpleFilter<PhoneNumber, PhoneNumberPool> pool() {
			return pool;
		}

		/**
		 * Получить фильтр спецификации
		 *
		 * @return фильтр спецификации
		 */
		public EntityQuerySimpleFilter<PhoneNumber, PhoneNumberSpecification> specification() {
			return specification;
		}

		/**
		 * Получить фильтр по цифровому представлению
		 *
		 * @return фильтр по цифровому представлению
		 */
		public ExtendedEntityQueryStringFilter<PhoneNumber> digits() {
			return digits;
		}

		/**
		 * Получить фильтр бронировани.
		 *
		 * @return фильтр по брони
		 */
		public EntityQuerySimpleFilter<PhoneNumber, BookingOrder> bookingOrder() {
			return bookingOrder;
		}
	}
}
