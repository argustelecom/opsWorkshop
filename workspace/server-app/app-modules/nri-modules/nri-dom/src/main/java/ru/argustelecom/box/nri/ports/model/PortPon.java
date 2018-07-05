package ru.argustelecom.box.nri.ports.model;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(schema = "nri", name = "port_pon")
@Access(AccessType.FIELD)
@Getter
@Setter
public class PortPon extends Port {

	public static final Set<Integer> AVAILABLE_MAX_SUBSCRIBER_NUM_VALUES = Sets.newHashSet(32,64,128);

	/**
	 * Разъём
	 */
	@Column(name = "connector_type")
	@Enumerated(EnumType.STRING)
	private PonConnectorType connectorType;

	/**
	 * Макс. кол-во абонентов
	 */
	@Column(name = "max_subscriber_num", nullable = false)
	private Integer maxSubscriberNum = 64;

	/**
	 * Дефолтный конструктор
	 */
	protected PortPon() {
		super(PortType.PON_PORT, PortPurpose.TECHNOLOGICAL);
	}

	/**
	 * Конструктор с ид
	 * @param id ид
	 */
	public PortPon(Long id){
		this();
		this.id = id;
	}

	public void setMaxSubscriberNum(Integer maxSubscriberNum) {
		if (!AVAILABLE_MAX_SUBSCRIBER_NUM_VALUES.contains(maxSubscriberNum)) {
			throw new IllegalStateException();
		}
	}
}
