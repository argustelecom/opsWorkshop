package ru.argustelecom.box.env.personnel;

import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.system.inf.page.PresentationState;

@Named(value = "employeeListVS")
@PresentationState
@Getter
@Setter
public class EmployeeListViewState {
	private static final long serialVersionUID = -7160117817796622913L;
}