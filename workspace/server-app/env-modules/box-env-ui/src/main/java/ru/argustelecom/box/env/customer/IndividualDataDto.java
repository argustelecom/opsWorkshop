package ru.argustelecom.box.env.customer;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.contact.EmailContactDto;
import ru.argustelecom.box.env.person.PersonDataDto;

@Getter
@Setter
@NoArgsConstructor
public class IndividualDataDto extends CustomerDataDto {

	private String name;
	private PersonDataDto personData;

	@Builder
	public IndividualDataDto(Long individualId, String name, String typeName, boolean vip, EmailContactDto mainEmail,
			Long personId, String prefix, String firstName, String secondName, String lastName, String suffix,
			String note) {

		super(individualId, typeName, vip, mainEmail);

		this.name = name;
		this.personData = new PersonDataDto(personId, prefix, firstName, secondName, lastName, suffix, note,
				null, null);
	}

}