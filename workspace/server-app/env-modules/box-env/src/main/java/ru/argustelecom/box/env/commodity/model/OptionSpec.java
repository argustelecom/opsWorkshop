package ru.argustelecom.box.env.commodity.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;

/**
 * Сущность уточняющая {@linkplain CommoditySpec спецификацию услуги/товара/опции} до спецификации опции.
 */
@Entity
@Access(AccessType.FIELD)
public abstract class OptionSpec<T extends OptionType> extends CommoditySpec<T> {

	private static final long serialVersionUID = -4707030688550203305L;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "service_spec_id", nullable = false)
	private ServiceSpec serviceSpec;

	protected OptionSpec() {
		super();
	}

	protected OptionSpec(Long id) {
		super(id);
	}

	public abstract static class OptionSpecQuery<T extends OptionType, I extends OptionSpec<T>>
			extends CommoditySpecQuery<T, I> {

		private EntityQueryEntityFilter<I, ServiceSpec> serviceSpec;

		public OptionSpecQuery(Class<I> entityClass) {
			super(entityClass);
			serviceSpec = createEntityFilter(OptionSpec_.serviceSpec);
		}

		public EntityQueryEntityFilter<I, ServiceSpec> serviceSpec() {
			return serviceSpec;
		}

	}

}
