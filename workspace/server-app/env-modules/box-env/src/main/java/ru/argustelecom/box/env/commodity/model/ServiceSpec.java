package ru.argustelecom.box.env.commodity.model;

import lombok.Builder;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;

/**
 * Сущность уточняющая {@linkplain CommoditySpec спецификацию услуги/товара/опции} до спецификации услуги.
 */
@Entity
@Access(AccessType.FIELD)
public class ServiceSpec extends CommoditySpec<ServiceType> {

	protected ServiceSpec() {
		super();
	}

	@Builder
	protected ServiceSpec(Long id) {
		super(id);
	}

	public static class ServiceSpecQuery<I extends ServiceSpec> extends CommoditySpecQuery<ServiceType, I> {

		public ServiceSpecQuery(Class<I> entityClass) {
			super(entityClass);
		}

	}

	private static final long serialVersionUID = -7655596076252180407L;

}