package ru.argustelecom.box.env.commodity.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import ru.argustelecom.box.env.type.model.TypeInstance;
import ru.argustelecom.box.env.type.model.TypeInstanceSpec;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;

/**
 * Сущность, описывающая спецификацию для конкретного типа {@linkplain CommodityType услуги/товара/опции}. Под спецификацией
 * можно понимать некий шаблон услуги/товара/опции, в котором заполнены значения
 * {@linkplain ru.argustelecom.box.env.type.model.TypeProperty характеристик}, с которыми данная услуга/товар/опция должны
 * быть созданы.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "commodity_spec")
public abstract class CommoditySpec<T extends CommodityType> extends TypeInstance<T> implements TypeInstanceSpec<T> {

	private static final long serialVersionUID = 1404429984797578560L;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected CommoditySpec() {
		super();
	}

	/**
	 * Конструктор предназначен для инстанцирования спецификацией. Не делай этот конструктор публичным. Не делай других
	 * публичных конструкторов. Экземпляры спецификаций должны инстанцироваться сугубо спецификацией для обеспечения
	 * корректной инициализации пользовательских свойств или отношений между спецификацией и ее экземпляром.
	 * 
	 * @param id
	 *            - идентификатор экземпляра спецификации, должен быть получен при помощи соответствующего генератора
	 *            через сервис IdSequence
	 * 
	 * @see ru.argustelecom.box.env.idsequence.IdSequenceService
	 */
	protected CommoditySpec(Long id) {
		super(id);
	}

	@Override
	@Access(AccessType.PROPERTY)
	@ManyToOne(targetEntity = CommodityType.class, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "commodity_type_id")
	public T getType() {
		return super.getType();
	}

	public abstract static class CommoditySpecQuery<T extends CommodityType, I extends CommoditySpec<T>>
			extends TypeInstanceQuery<T, I> {

		public CommoditySpecQuery(Class<I> entityClass) {
			super(entityClass);
		}

		@Override
		protected EntityQueryEntityFilter<I, ? super T> createTypeFilter() {
			return createEntityFilter(CommoditySpec_.type);
		}

	}

}