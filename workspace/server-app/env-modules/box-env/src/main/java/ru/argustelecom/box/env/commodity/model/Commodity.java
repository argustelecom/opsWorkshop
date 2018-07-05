package ru.argustelecom.box.env.commodity.model;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.type.model.TypeInstance;
import ru.argustelecom.box.env.type.model.TypeInstanceDerivative;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;

/**
 * Сущность описывающая экземпляр товара/услуги.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "commodity")
public abstract class Commodity<T extends CommodityType, S extends CommoditySpec<T>> extends TypeInstance<T>
		implements TypeInstanceDerivative<T, S> {

	private static final long serialVersionUID = -6116954645218635819L;

	/**
	 * Дата создания.
	 */
	@Getter
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

	/**
	 * Спецификация(шаблон), по которому была создана данная услуга/товар.
	 */
	@Getter
	@Setter
	@ManyToOne(targetEntity = CommoditySpec.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "prototype_id")
	private S prototype;

	@Override
	@Access(AccessType.PROPERTY)
	@ManyToOne(targetEntity = CommodityType.class, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "type_id")
	public T getType() {
		return super.getType();
	}

	protected Commodity() {
		super();
	}

	protected Commodity(Long id) {
		super(id);
		creationDate = new Date();
	}

	@Override
	public String getObjectName() {
		return getType().getObjectName();
	}

	public abstract static class CommodityQuery<T extends CommodityType, S extends CommoditySpec<T>, I extends Commodity<T, S>>
			extends TypeInstanceQuery<T, I> {

		private EntityQueryEntityFilter<I, ? super S> prototype;

		public CommodityQuery(Class<I> entityClass) {
			super(entityClass);
			prototype = createEntityFilter(Commodity_.prototype);
		}

		public EntityQueryEntityFilter<I, ? super S> prototype() {
			return prototype;
		}

		@Override
		protected EntityQueryEntityFilter<I, ? super T> createTypeFilter() {
			return createEntityFilter(Commodity_.type);
		}
	}
}