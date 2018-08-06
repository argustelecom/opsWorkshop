package ru.argustelecom.ops.env.contact;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import ru.argustelecom.ops.inf.modelbase.BusinessObject;
import ru.argustelecom.system.inf.modelbase.NamedObject;

/**
 * Класс вводящий понятие контакта, базовый класс для всех видов контактов.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "system")
public abstract class Contact<T> extends BusinessObject implements NamedObject {

	private static final long serialVersionUID = 764789879075783495L;

	@ManyToOne(fetch = FetchType.LAZY)
	private ContactType type;

	@Transient
	private T value;

	@Column(length = 512)
	private String comment;


	protected Contact() {
	}

	protected Contact(Long id) {
		super(id);
	}

	@Override
	public String getObjectName() {
		return value.toString();
	}

	/**
	 * @return Тип контакта.
	 */
	public ContactType getType() {
		return type;
	}

	public void setType(ContactType type) {
		this.type = type;
	}

	/**
	 * @return Значение хранимое в качестве контакта.
	 */
	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}





}