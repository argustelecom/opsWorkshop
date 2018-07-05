package ru.argustelecom.box.env.commodity.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;

/**
 * Сущность описывающая типы товаров.
 */
@Entity
@Access(AccessType.FIELD)
public class GoodsType extends CommodityType {

	protected GoodsType() {
		super();
	}

	protected GoodsType(Long id) {
		super(id);
	}

	public static class GoodsTypeQuery<T extends GoodsType> extends CommodityTypeQuery<T> {

		public GoodsTypeQuery(Class<T> entityClass) {
			super(entityClass);
		}

	}

	private static final long serialVersionUID = -8211507803440102033L;

}