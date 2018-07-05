package ru.argustelecom.box.nri.logicalresources.ip.subnet.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.net.util.SubnetUtils;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddress;
import ru.argustelecom.box.nri.logicalresources.model.LogicalResource;
import ru.argustelecom.box.nri.logicalresources.model.LogicalResourceType;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * Подсеть
 * @author a.wisniewski, s.kolyada
 * @since 11.12.2017
 */
@Entity
@Table(schema = "nri", name = "ip_subnet")
@Access(AccessType.FIELD)
@Getter
@Setter
public class IPSubnet extends LogicalResource {

	private static final long serialVersionUID = 1L;

	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private SubnetType subnetType;

	/**
	 * Коментарий
	 */
	@Column(name = "comment")
	private String comment;

	/**
	 * Дочерние подсети
	 */
	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
	private List<IPSubnet> childSubnets = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "parent_subnet_id")
	private IPSubnet parent;

	/**
	 * IP-адреса, входящие в эту подсеть (но не входящие в дочерние)
	 */
	@OneToMany(mappedBy = "subnet", cascade = CascadeType.ALL)
	private List<IPAddress> ipAddresses = new ArrayList<>();

	/**
	 * Широковещательный адрес подсети
	 */
	@Transient
	private String broadcastAddress;

	/**
	 * Конструктор
	 */
	public IPSubnet() {
		super(LogicalResourceType.IP_SUBNET);
	}

	/**
	 * Конструктор
	 *
	 * @param id id
	 */
	public IPSubnet(Long id) {
		super(LogicalResourceType.IP_SUBNET);
		this.id = id;
	}

	/**
	 * Указать имя подсети
	 * Вызывает так же перерасчёт вычисляемых параметров
	 * @param name имя
	 */
	@Override
	public void setName(String name) {
		this.name = name;
		SubnetUtils utils = new SubnetUtils(name);
		SubnetUtils.SubnetInfo info = utils.getInfo();
		this.broadcastAddress = info.getBroadcastAddress();
	}

	/**
	 * Тип подсети
	 */
	public enum SubnetType {
		IPv4, IPv6
	}
}
