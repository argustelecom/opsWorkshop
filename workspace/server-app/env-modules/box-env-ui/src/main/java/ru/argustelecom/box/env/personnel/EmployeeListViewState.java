package ru.argustelecom.box.env.personnel;

import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.system.inf.page.PresentationState;

import java.io.Serializable;

@Named(value = "employeeListVS")
@PresentationState
@Getter
@Setter
public class EmployeeListViewState implements Serializable {
	private static final long serialVersionUID = -7160117817796622913L;
}