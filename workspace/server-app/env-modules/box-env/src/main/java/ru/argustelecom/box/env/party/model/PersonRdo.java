package ru.argustelecom.box.env.party.model;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonRdo extends PartyRdo {

	private String namePrefix;
	private String firstName;
	private String secondName;
	private String lastName;
	private String nameSuffix;
	private String shortName;
	private String shortInitials;
	private String fullName;
	private String fullInitials;

	private String note;

	@Builder
	public PersonRdo(Long id, Map<String, String> properties, String namePrefix, String firstName, String secondName,
			String lastName, String nameSuffix, String shortName, String shortInitials, String fullName,
			String fullInitials, String note) {
		super(id, properties);
		this.namePrefix = namePrefix;
		this.firstName = firstName;
		this.secondName = secondName;
		this.lastName = lastName;
		this.nameSuffix = nameSuffix;
		this.shortName = shortName;
		this.shortInitials = shortInitials;
		this.fullName = fullName;
		this.fullInitials = fullInitials;
		this.note = note;
	}

}