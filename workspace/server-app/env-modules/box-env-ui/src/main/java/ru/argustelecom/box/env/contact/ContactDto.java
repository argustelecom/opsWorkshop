package ru.argustelecom.box.env.contact;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.system.inf.modelbase.NamedObject;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = { "id" })
public class ContactDto implements NamedObject {

	private Long id;
	private String name;
	private String value;
	private ContactType type;
	private ContactCategory category;
	private String comment;

	@Override
	public String getObjectName() {
		return value;
	}

}