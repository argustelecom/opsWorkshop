package ru.argustelecom.box.env.contact;

import lombok.Builder;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.env.validator.Email;

@NoArgsConstructor
public class EmailContactDto extends ContactDto {

	@Builder
	protected EmailContactDto(Long id, String name, String value, ContactType type, ContactCategory category,
			String comment) {
		super(id, name, value, type, category, comment);
	}

	@Override
	@Email(canBeNull = true)
	public String getValue() {
		return super.getValue();
	}

}