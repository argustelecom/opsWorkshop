package ru.argustelecom.box.env.contactperson;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactPersonsDto {

	private List<ContactPersonDataDto> values;

	@Builder
	ContactPersonsDto(List<ContactPersonDataDto> values) {
		this.values = values;
	}

}