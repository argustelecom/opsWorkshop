package ru.argustelecom.box.env.telephony.tariff.model;

import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.inf.modelbase.BusinessDirectory;

/**
 * Зона телефонной нумерации
 *
 */
@Getter
@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", uniqueConstraints = {
		@UniqueConstraint(name = "uc_telephony_zone_name", columnNames = { "name" }) })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = TelephonyZone.CACHE_REGION_NAME)
public class TelephonyZone extends BusinessDirectory {

	public static final String CACHE_REGION_NAME = "ru.argustelecom.box.env-rw-cache-region";

	private static final long serialVersionUID = -5744481740516276914L;

	@Column(length = 128, nullable = false)
	private String name;

	@Setter
	@Column(length = 256)
	private String description;

	protected TelephonyZone() {
		super();
	}

	public TelephonyZone(Long id) {
		super(id);
	}

	public void setName(String name) {
		checkRequiredArgument(name, "Name");
		this.name = name;
	}

	@Override
	public String getObjectName() {
		return name;
	}

}