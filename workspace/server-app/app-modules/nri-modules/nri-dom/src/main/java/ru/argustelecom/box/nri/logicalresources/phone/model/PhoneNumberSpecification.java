package ru.argustelecom.box.nri.logicalresources.phone.model;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.type.model.Type;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Спецификация телефонного номера
 * Created by s.kolyada on 30.10.2017.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "nri", name = "phone_number_specification")
@Getter
@Setter
public class PhoneNumberSpecification extends Type {

	private static final long serialVersionUID = 1L;

	/**
	 * Маска телефонного номера
	 */
	@Column(name = "mask")
	private String mask;

	/**
	 * Время в сутках после которого можно переводить из временно заблокированно в доступно
	 */
	@Column(name = "blocked_interval")
	private int blockedInterval = 0;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected PhoneNumberSpecification() {
		super();
	}

	/**
	 * Создает экземпляр спецификации. Т.к. спецификация является метаданными, то для ее идентификации необходимо
	 * использовать генератор {@link ru.argustelecom.box.inf.modelbase.MetadataUnit#generateId()} или
	 * {@link ru.argustelecom.box.inf.modelbase.MetadataUnit#generateId(javax.persistence.EntityManager)}. Этот же
	 * идентификатор распространяется на холдера свойств спецификации. Только использование единого генератора для всех
	 * потомков спецификации может гарантированно уберечь от наложения идентификаторов в холдерах
	 *
	 * @param id - идентификатор, полученный из генератора идентификаторов метаданных
	 */
	public PhoneNumberSpecification(Long id) {
		super(id);
	}
}
