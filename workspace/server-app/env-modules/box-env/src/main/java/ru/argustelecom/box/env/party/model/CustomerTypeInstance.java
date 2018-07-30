package ru.argustelecom.box.env.party.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import ru.argustelecom.box.env.type.model.IndexTable;
import ru.argustelecom.box.env.type.model.InstanceTable;
import ru.argustelecom.box.env.type.model.Type;
import ru.argustelecom.box.env.type.model.TypeInstance;
import ru.argustelecom.box.env.type.model.TypeInstanceDescriptor;
import ru.argustelecom.box.env.type.model.TypeInstanceUniqueListener;

@Entity
@Access(AccessType.FIELD)
@EntityListeners(TypeInstanceUniqueListener.class)
//@formatter:off
@TypeInstanceDescriptor(
		indexTable = @IndexTable(
				schema = "system",
				table = "customer_property_index"
		),
		instanceTable = @InstanceTable(
				schema = "system",
				table = "customer_type_instance"
		)
)
//@formatter:on
public class CustomerTypeInstance extends TypeInstance<CustomerType> {

	private static final long serialVersionUID = -4425465736547521009L;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected CustomerTypeInstance() {
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
	 * @see Type#createInstance(Class, Long)
	 */
	protected CustomerTypeInstance(Long id) {
		super(id);
	}

	@Override
	@Access(AccessType.PROPERTY)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "customer_type_id")
	public CustomerType getType() {
		return super.getType();
	}

}