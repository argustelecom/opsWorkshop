package ru.argustelecom.box.env.contact;

import lombok.Builder;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.env.validator.Phone;

@NoArgsConstructor
public class PhoneContactDto extends ContactDto {

	@Builder
	protected PhoneContactDto(Long id, String name, String value, ContactType type, ContactCategory category,
			String comment) {
		super(id, name, value, type, category, comment);
	}

	@Override
	@Phone
	public String getValue() {
		return super.getValue();
	}

}