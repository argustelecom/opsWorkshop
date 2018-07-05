package ru.argustelecom.box.env.contact;

import lombok.Builder;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.env.validator.Skype;

@NoArgsConstructor
public class SkypeContactDto extends ContactDto {

	@Builder
	protected SkypeContactDto(Long id, String name, String value, ContactType type, ContactCategory category,
			String comment) {
		super(id, name, value, type, category, comment);
	}

	@Override
	@Skype
	public String getValue() {
		return super.getValue();
	}

}