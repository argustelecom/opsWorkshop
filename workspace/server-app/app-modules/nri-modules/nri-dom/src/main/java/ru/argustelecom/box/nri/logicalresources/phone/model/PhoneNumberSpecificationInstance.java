package ru.argustelecom.box.nri.logicalresources.phone.model;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.type.model.TypeInstance;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Инстанс спецификации телефонного номера
 * Created by s.kolyada on 30.10.2017.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "nri", name = "phone_number_specification_instance")
@Getter
@Setter
public class PhoneNumberSpecificationInstance extends TypeInstance<PhoneNumberSpecification> {

	private static final long serialVersionUID = 1L;

	/**
	 * Телефонный номер, к которому относится специяикация
	 */
	@OneToOne(fetch = FetchType.EAGER, optional = false, cascade = {CascadeType.ALL})
	@JoinColumn(name = "phone_number_id", updatable = false)
	private PhoneNumber phoneNumber;

	/**
	 * конструктор с id
	 * @param id id
	 */
	public PhoneNumberSpecificationInstance(Long id) {
		super(id);
	}
	protected PhoneNumberSpecificationInstance() {
		super();
	}

	@Override
	@Access(AccessType.PROPERTY)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "phone_number_spec_id")
	public PhoneNumberSpecification getType() {
		return super.getType();
	}

}
