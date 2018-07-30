package ru.argustelecom.box.env.contact;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CustomContactDto extends ContactDto {

	@Builder
	protected CustomContactDto(Long id, String name, String value, ContactType type, ContactCategory category,
			String comment) {
		super(id, name, value, type, category, comment);
	}

}