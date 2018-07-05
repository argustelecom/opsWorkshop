package ru.argustelecom.box.env.commodity.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;

/**
 * Сущность уточняющая {@linkplain CommoditySpec спецификацию услуги/товара/опции} до спецификации услуги.
 */
@Entity
@Access(AccessType.FIELD)
public class GoodsSpec extends CommoditySpec<GoodsType> {

	protected GoodsSpec() {
		super();
	}

	protected GoodsSpec(Long id) {
		super(id);
	}

	public static class GoodsSpecQuery<I extends GoodsSpec> extends CommoditySpecQuery<GoodsType, I> {

		public GoodsSpecQuery(Class<I> entityClass) {
			super(entityClass);
		}

	}

	private static final long serialVersionUID = 239587071778230082L;

}