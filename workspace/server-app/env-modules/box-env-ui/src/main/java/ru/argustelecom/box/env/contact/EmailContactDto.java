package ru.argustelecom.box.env.contact;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EmailContactDto extends ContactDto {

	@Builder
	protected EmailContactDto(Long id, String name, String value, ContactType type, ContactCategory category,
			String comment) {
		super(id, name, value, type, category, comment);
	}

	@Override
	public String getValue() {
		return super.getValue();
	}

}