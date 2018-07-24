package ru.argustelecom.box.env.person;

import org.hibernate.validator.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(of = { "personId" })
public class Person {

	private Long personId;
	private String prefix;
	@NotBlank
	private String firstName;
	private String secondName;
	@NotBlank
	private String lastName;
	private String suffix;
	private String note;

	public Person() {
	}

}