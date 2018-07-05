package ru.argustelecom.box.env.billing.bill.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.inf.modelbase.BusinessDirectory;
import ru.argustelecom.box.inf.nls.LocaleUtils;

@Entity
@Table(name = "analytic_type", schema = "system")
@Access(AccessType.FIELD)
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "ru.argustelecom.readonly-cache-region")
@Getter
@Setter
public abstract class AbstractBillAnalyticType extends BusinessDirectory {

	private String name;

	private String keyword;

	private String serviceName;

	private Boolean availableForCustomPeriod;

	private String description;

	protected AbstractBillAnalyticType() {
		super();
	}

	public AbstractBillAnalyticType(long id) {
		super(id);
	}

	public String getDescription() {
		return description != null ? LocaleUtils.getLocalizedMessage(description, this.getClass()) : null;
	}

	public String getName() {
		return LocaleUtils.getLocalizedMessage(name, this.getClass());
	}

	private static final long serialVersionUID = -253503347137553933L;
}
