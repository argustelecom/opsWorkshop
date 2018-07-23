package ru.argustelecom.box.env.party.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * Участник являющийся физическим лицом.
 */
@Entity
@Access(AccessType.FIELD)
public class Person extends Party {

	private static final long serialVersionUID = 3653846184126846410L;

	@Getter
	@Embedded
	private PersonName name;

	@Override
	public String getObjectName() {
		return name.fullInitials();
	}

	public void rename(PersonName newName) {
		checkArgument(newName != null, "Person Name is required for renaming");
		if (!Objects.equals(this.name, newName)) {
			this.name = newName;
			// TODO [события предметной области]
			// DomainEvents.instance().fire(new PersonChangedEvent<PersonName>(this, "name", oldName, newName));
		}
	}

	/**
	 * Конструктор для JPA
	 */
	protected Person() {
	}

	/**
	 * Билдер для удобного инстанцирования Person воспользуйся методом {@link Person#builder()}
	 */
	@Builder
	protected Person(@NonNull Long id, @NonNull PersonName name, String note) {
		super(id);
		this.name = checkNotNull(name);
	}

}