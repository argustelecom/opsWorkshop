package ru.argustelecom.box.nri.logicalresources.ip.address.model;

import com.google.common.net.InetAddresses;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import ru.argustelecom.box.env.activity.attachment.model.AttachmentContext;
import ru.argustelecom.box.env.activity.attachment.model.HasAttachments;
import ru.argustelecom.box.env.activity.comment.model.CommentContext;
import ru.argustelecom.box.env.activity.comment.model.HasComments;
import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.nri.booking.model.BookingOrder;
import ru.argustelecom.box.nri.logicalresources.ip.address.lifecycle.IPAddressState;
import ru.argustelecom.box.nri.logicalresources.ip.subnet.model.IPSubnet;
import ru.argustelecom.box.nri.logicalresources.model.LogicalResource;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryNumericFilter;
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
import java.io.Serializable;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import static ru.argustelecom.box.nri.logicalresources.ip.address.model.IpTransferType.BROADCAST;
import static ru.argustelecom.box.nri.logicalresources.ip.address.model.IpTransferType.MULTICAST;
import static ru.argustelecom.box.nri.logicalresources.ip.address.model.IpTransferType.UNICAST;
import static ru.argustelecom.box.nri.logicalresources.model.LogicalResourceType.IP_ADDRESS;

/**
 * IP-адрес - логический ресурс
 *
 * @author d.khekk, s.kolyada
 * @since 08.12.2017
 */
@Entity
@EntityListeners(IPAddressEntityListener.class)
@Table(schema = "nri", name = "ip_address")
@Access(AccessType.FIELD)
@Getter
@Setter
public class IPAddress extends LogicalResource implements LifecycleObject<IPAddressState>, HasComments, HasAttachments {

	private static final long serialVersionUID = 1L;

	/**
	 * Вид IP-адреса
	 */
	@Column(name = "is_static", nullable = false)
	private Boolean isStatic;

	/**
	 * Тип IP-адреса
	 */
	@Column(name = "is_private", nullable = false)
	private Boolean isPrivate;

	/**
	 * Метод передачи данных
	 */
	@Column(name = "transfer_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private IpTransferType transferType;

	/**
	 * Статус IP-адреса
	 */
	@Column(name = "ip_address_state", nullable = false)
	@Enumerated(EnumType.STRING)
	private IPAddressState state = IPAddressState.defaultStatus();

	/**
	 * Время послденего изменения статуса
	 */
	@Column(name = "state_change_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date stateChangeDate = new Date();

	/**
	 * Коментарий к IP-адресу
	 */
	@Column(name = "comment")
	private String comment;

	/**
	 * Подсеть
	 */
	@ManyToOne // может отсутствовать, если номер перенесен в архив
	@JoinColumn(name = "parent_subnet_id")
	private IPSubnet subnet;

	/**
	 * Прикрепленные файлы
	 */
	@OneToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "attachment_context_id", updatable = false)
	private AttachmentContext attachmentContext;

	/**
	 * Комментарии
	 */
	@OneToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "comment_context_id", updatable = false)
	private CommentContext commentContext;

	/**
	 * Назначение IP-адреса
	 * см. BOX-2180
	 */
	@Column(name = "purpose", nullable = false)
	@Enumerated(EnumType.STRING)
	private IPAddressPurpose purpose = IPAddressPurpose.NOT_SPECIFIED;

	@Column(name = "hash", nullable = false)
	private Long ipHash;

	/**
	 * Конструктор
	 */
	public IPAddress() {
		super(IP_ADDRESS);
	}

	/**
	 * Конструктор по ID
	 *
	 * @param id идентификатор
	 */
	public IPAddress(Long id) {
		super(IP_ADDRESS);
		commentContext = new CommentContext(id);
		attachmentContext = new AttachmentContext(id);
		this.id = id;
	}

	/**
	 * Конструктор по всем параметрам
	 *
	 * @param id       ID
	 * @param name     адрес
	 * @param isStatic вид IP-адреса
	 * @param comment  комментарий
	 * @param subnet   подсеть
	 * @param purpose  назначение
	 */
	@Builder
	public IPAddress(Long id, String name, Boolean isStatic, String comment, IPSubnet subnet, IPAddressPurpose purpose) {
		super(IP_ADDRESS);
		this.id = id;
		this.name = name;
		this.isStatic = isStatic;
		this.comment = comment;
		this.subnet = subnet;
		this.isPrivate = countIsPrivate();
		this.state = IPAddressState.AVAILABLE;
		this.purpose = Optional.ofNullable(purpose).orElse(IPAddressPurpose.NOT_SPECIFIED);
		commentContext = new CommentContext(id);
		attachmentContext = new AttachmentContext(id);
		calculateState();
	}

	/**
	 * Указать имя
	 * Вызывает так же перерасчёт параметров адреса
	 * @param name
	 */
	@Override
	public void setName(String name) {
		this.name = name;
		calculateState();
	}

	/**
	 * Получить метод передачи
	 * @return тип транспорта
	 */
	public IpTransferType getTransferType() {
		if (transferType == null) {
			calculateState();
		}
		return transferType;
	}

	/**
	 * Рассчитать параметры адреса
	 */
	public void calculateState() {
		transferType = calculateTransferType(name, subnet);
		ipHash = ipToLong(name);
	}

	/**
	 * Получить метод передачи
	 * @param ipAddress адрес
	 * @param subnet подсеть
	 * @return метод передачи
	 */
	private static IpTransferType calculateTransferType(String ipAddress, IPSubnet subnet) {
		// проерить необходимые параметры
		if (StringUtils.isBlank(ipAddress) || Objects.isNull(subnet) || StringUtils.isBlank(subnet.getName())) {
			return null;
		}
		// проверить мультикаст
		Inet4Address ip = (Inet4Address) InetAddresses.forString(ipAddress);
		if (ip.isMulticastAddress()) {
			return MULTICAST;
		}

		// проверить не совпадает ли адрес с широковещательным адресом подсети, в которую он входит
		if (StringUtils.equalsIgnoreCase(subnet.getBroadcastAddress(), ipAddress)) {
			return BROADCAST;
		}

		// если ни один из вышеперечисленных, то юникаст
		return UNICAST;
	}

	/**
	 * Расчитать тип адреса
	 *
	 * @return истина, если внутренний, иначе внешний
	 */
	private boolean countIsPrivate() {
		if (StringUtils.isBlank(name)) {
			return false;
		}
		InetAddress ip = InetAddresses.forString(name);
		return ip.isSiteLocalAddress();
	}

	/**
	 * Перевод адреса в числовую форму
	 * @param ipAddress адрес
	 * @return числовая ворма адреса
	 */
	public static long ipToLong(String ipAddress) {
		String[] ipAddressInArray = ipAddress.split("\\.");
		long result = 0;
		for (int i = 0; i < ipAddressInArray.length; i++) {
			int power = 3 - i;
			int ip = Integer.parseInt(ipAddressInArray[i]);
			result += ip * Math.pow(256, power);
		}
		return result;
	}

	/**
	 * Запрос
	 */
	public static class IPAddressQuery extends EntityQuery<IPAddress> {

		/**
		 * Фильтр имени
		 */
		private EntityQueryStringFilter<IPAddress> name;

		/**
		 * Фильтр статуса
		 */
		private EntityQuerySimpleFilter<IPAddress, IPAddressState> state;

		/**
		 * Фильтр назначения
		 */
		private EntityQuerySimpleFilter<IPAddress, IPAddressPurpose> purpose;

		/**
		 * Фильтр вида
		 */
		private EntityQuerySimpleFilter<IPAddress, Boolean> isStatic;

		/**
		 * Фильтр типа
		 */
		private EntityQuerySimpleFilter<IPAddress, Boolean> isPrivate;

		/**
		 * Фильтр метода передачи данных
		 */
		private EntityQuerySimpleFilter<IPAddress, IpTransferType> transferType;

		/**
		 * Фильтр подсети
		 */
		private EntityQuerySimpleFilter<IPAddress, IPSubnet> subnet;

		/**
		 * Фильтр по бронированию
		 */
		private EntityQuerySimpleFilter<IPAddress, BookingOrder> bookingOrder;

		/**
		 * Фильтр по хэшу
		 */
		private EntityQueryNumericFilter<IPAddress, Long> ipHash;

		/**
		 * Конструктор
		 */
		public IPAddressQuery() {
			super(IPAddress.class);
			name = createStringFilter(IPAddress_.name);
			state = createFilter(IPAddress_.state);
			purpose = createFilter(IPAddress_.purpose);
			isStatic = createFilter(IPAddress_.isStatic);
			isPrivate = createFilter(IPAddress_.isPrivate);
			transferType = createFilter(IPAddress_.transferType);
			subnet = createFilter(IPAddress_.subnet);
			bookingOrder = createFilter(IPAddress_.bookingOrder);
			ipHash = createNumericFilter(IPAddress_.ipHash);
		}

		/**
		 * Получить фильтр имени
		 *
		 * @return фильтр имени
		 */
		public EntityQueryStringFilter<IPAddress> name() {
			return name;
		}

		/**
		 * Получить фильтр статуса
		 *
		 * @return фильтр статуса
		 */
		public EntityQuerySimpleFilter<IPAddress, IPAddressState> state() {
			return state;
		}

		/**
		 * Получить фильтр назначения
		 *
		 * @return фильтр назначения
		 */
		public EntityQuerySimpleFilter<IPAddress, IPAddressPurpose> purpose() {
			return purpose;
		}

		/**
		 * Получить фильтр вида IP-адреса
		 *
		 * @return фильтр вида
		 */
		public EntityQuerySimpleFilter<IPAddress, Boolean> isStatic() {
			return isStatic;
		}

		/**
		 * Получить фильтр типа IP-адреса
		 *
		 * @return фильтр типа
		 */
		public EntityQuerySimpleFilter<IPAddress, Boolean> isPrivate() {
			return isPrivate;
		}

		/**
		 * Получить фильтр метода передачи данных
		 *
		 * @return фильтр метода передачи данных
		 */
		public EntityQuerySimpleFilter<IPAddress, IpTransferType> transferType() {
			return transferType;
		}

		/**
		 * Получить фильтр подсети
		 *
		 * @return фильтр подсети
		 */
		public EntityQuerySimpleFilter<IPAddress, IPSubnet> subnet() {
			return subnet;
		}

		/**
		 * Получить фильтр бронировани.
		 *
		 * @return фильтр по брони
		 */
		public EntityQuerySimpleFilter<IPAddress, BookingOrder> bookingOrder() {
			return bookingOrder;
		}

		public EntityQueryNumericFilter<IPAddress, Long> ipHash() {
			return ipHash;
		}
	}
}
