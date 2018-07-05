package ru.argustelecom.box.env.personnel;

import java.io.InputStream;
import java.util.Date;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.party.model.Appointment;
import ru.argustelecom.box.env.person.PersonDataDto;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = { "name", "appointment", "personnelNumber", "fired" })
public class EmployeeDataDto extends PersonDataDto {

	private Long employeeId;
	private String name;
	private Appointment appointment;
	@NotBlank
	private String personnelNumber;
	private boolean fired;

	public EmployeeDataDto() {
		super();
	}

	@Builder
	EmployeeDataDto(Long employeeId, String employeeName, Appointment appointment, String personnelNumber,
			boolean fired, Long personId, String prefix, String firstName, String secondName, String lastName,
			String suffix, String note, InputStream imageInputStream, String imageFormatName) {

		super(personId, prefix, firstName, secondName, lastName, suffix, note, imageInputStream,
				imageFormatName);

		this.employeeId = employeeId;
		this.name = employeeName;
		this.appointment = appointment;
		this.personnelNumber = personnelNumber;
		this.fired = fired;
	}

}