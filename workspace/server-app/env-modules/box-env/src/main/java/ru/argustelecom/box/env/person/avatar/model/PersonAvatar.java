package ru.argustelecom.box.env.person.avatar.model;

import java.sql.Blob;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import ru.argustelecom.box.env.party.model.Person;
import ru.argustelecom.system.inf.modelbase.SuperClass;

@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "person_avatar")
public class PersonAvatar extends SuperClass {

	private static final long serialVersionUID = -5925599826333381434L;

	private Blob image;

	private String formatName;

	protected PersonAvatar() {
		super();
	}

	public PersonAvatar(Person person, Blob image, String formatName) {
		super(person.getId(), SuperClass.ENT_SUPER_CLASS);
		this.image = image;
		this.formatName = formatName;
	}

	@Id
	@Override
	@Access(AccessType.PROPERTY)
	public Long getId() {
		return super.getId();
	}

	public Blob getImage() {
		return image;
	}

	public void setImage(Blob image) {
		this.image = image;
	}

	public String getFormatName() {
		return formatName;
	}

	public void setFormatName(String formatName) {
		this.formatName = formatName;
	}

}
