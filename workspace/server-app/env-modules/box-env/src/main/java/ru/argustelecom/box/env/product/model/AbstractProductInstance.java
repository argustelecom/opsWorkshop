package ru.argustelecom.box.env.product.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import ru.argustelecom.box.env.type.model.TypeInstance;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;

@MappedSuperclass
@Access(AccessType.FIELD)
public abstract class AbstractProductInstance extends TypeInstance<AbstractProductType> {

	private static final long serialVersionUID = -7339106438449712049L;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected AbstractProductInstance() {
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
	protected AbstractProductInstance(Long id) {
		super(id);
	}

	@Override
	@Access(AccessType.PROPERTY)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "product_type_id")
	public AbstractProductType getType() {
		return super.getType();
	}

	public abstract static class AbstractProductInstanceQuery<I extends AbstractProductInstance>
			extends TypeInstanceQuery<AbstractProductType, I> {

		public AbstractProductInstanceQuery(Class<I> entityClass) {
			super(entityClass);
		}

		@Override
		protected EntityQueryEntityFilter<I, AbstractProductType> createTypeFilter() {
			return createEntityFilter(AbstractProductInstance_.type);
		}

	}

}