package ru.argustelecom.box.env.personnel;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeListDto extends ConvertibleDto {
	private Long id;
	private String personnelNumber;
	private String firstName;
	private String secondName;
	private String lastName;
	private boolean fired;
	private LoginListDto loginListDto;

	@Builder
	public EmployeeListDto(Long id, String personnelNumber, String firstName, String secondName, String lastName,
			boolean fired, LoginListDto loginListDto) {
		this.id = id;
		this.personnelNumber = personnelNumber;
		this.firstName = firstName;
		this.secondName = secondName;
		this.lastName = lastName;
		this.fired = fired;
		this.loginListDto = loginListDto;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return EmployeeListDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return Employee.class;
	}
}
