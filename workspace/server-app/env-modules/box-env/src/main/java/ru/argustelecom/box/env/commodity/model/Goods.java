package ru.argustelecom.box.env.commodity.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;

/**
 * Сущность описывающая экземпляр товара.
 */
@Entity
@Access(AccessType.FIELD)
public class Goods extends Commodity<GoodsType, GoodsSpec> {

	private static final long serialVersionUID = -2764846014088153795L;

	protected Goods() {
		super();
	}

	protected Goods(Long id) {
		super(id);
	}

	public static class GoodsQuery<I extends Goods> extends CommodityQuery<GoodsType, GoodsSpec, I> {

		public GoodsQuery(Class<I> entityClass) {
			super(entityClass);
		}
	}
}