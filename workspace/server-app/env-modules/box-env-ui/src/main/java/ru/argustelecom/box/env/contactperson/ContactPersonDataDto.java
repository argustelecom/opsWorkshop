package ru.argustelecom.box.env.contactperson;

import java.io.InputStream;
import java.util.Date;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.person.PersonDataDto;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true, of = { "contactPersonId" })
@NoArgsConstructor
public class ContactPersonDataDto extends PersonDataDto {

	private Long contactPersonId;
	private Long companyId;
	private String companyName;
	private String appointment;
	private String email;
	private String phone;
	private String mailTo;
	private String callTo;

	@Builder
	protected ContactPersonDataDto(Long contactPersonId, Long companyId, String companyName, String appointment,
			String email, String phone, Long personId, String prefix, String firstName, String secondName,
			String lastName, String suffix, String note, InputStream imageInputStream,
			String imageFormatName, String mailTo, String callTo) {

		super(personId, prefix, firstName, secondName, lastName, suffix, note, imageInputStream,
				imageFormatName);

		this.contactPersonId = contactPersonId;
		this.companyId = companyId;
		this.companyName = companyName;
		this.appointment = appointment;
		this.email = email;
		this.phone = phone;
		this.mailTo = mailTo;
		this.callTo = callTo;
	}

}