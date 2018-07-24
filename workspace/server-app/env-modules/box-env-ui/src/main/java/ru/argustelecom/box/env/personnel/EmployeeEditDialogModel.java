package ru.argustelecom.box.env.personnel;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.contact.ContactEditFrameModel;
import ru.argustelecom.box.env.overall.nls.OverallMessagesBundle;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.env.party.nls.PersonnelMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.page.outcome.OutcomeConstructor;
import ru.argustelecom.box.inf.page.outcome.param.IdentifiableOutcomeParam;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "employeeEditDM")
@PresentationModel
public class EmployeeEditDialogModel implements Serializable {

	private static final long serialVersionUID = 6989614820463189145L;

	@Inject
	private OutcomeConstructor outcome;

	@Inject
	private EmployeeDataAppService employeeDataAs;

	@Inject
	private ContactEditFrameModel contactEditFM;

	private EmployeeData employeeDataDto;

	@Getter
	@Setter
	private Employee employee;

	public void open() {
		RequestContext.getCurrentInstance().update("employee_edit_form-employee_edit_dlg");
		RequestContext.getCurrentInstance().execute("PF('employeeEditDlg').show()");
	}

	public void init() {
		if (employeeDataDto == null)
			employeeDataDto = new EmployeeDataDto();
	}

	public boolean isEditMode() {
		return employeeDataDto.getEmployeeId() != null;
	}

	public String submit() {
		if (isEditMode()) {
			edit();
			contactEditFM.submit();
		} else {
			String result = create();
			contactEditFM.setParty(employee.getParty());
			contactEditFM.submit();
			return result;
		}

		return StringUtils.EMPTY;
	}

	public void cancel() {
		employeeDataDto = null;
	}

	private void edit() {
		//@formatter:off
		employeeDataAs.renamePerson(
			employeeDataDto.getPersonId(),
			employeeDataDto.getPrefix(),
			employeeDataDto.getFirstName(),
			employeeDataDto.getSecondName(),
			employeeDataDto.getLastName(),
			employeeDataDto.getSuffix()
		);

		employeeDataAs.editPersonData(
			employeeDataDto.getPersonId(),
			employeeDataDto.getNote()
		);

		employeeDataAs.editEmployeeData(
			employeeDataDto.getEmployeeId(),
			employeeDataDto.getAppointment(),
			employeeDataDto.getPersonnelNumber()
		);
		//@formatter:on

		RequestContext.getCurrentInstance().execute("PF('employeeEditDlg').hide()");
	}

	private String create() {
		Employee employeeWithSamePersonnelNumber = employeeDataAs
				.findEmployeeByPersonnelNumber(employeeDataDto.getPersonnelNumber());

		if (employeeWithSamePersonnelNumber == null) {
			//@formatter:off
			Employee employee = employeeDataAs.createEmployee(
				employeeDataDto.getPrefix(),
				employeeDataDto.getFirstName(),
				employeeDataDto.getSecondName(),
				employeeDataDto.getLastName(),
				employeeDataDto.getSuffix(),
				employeeDataDto.getAppointment(),
				employeeDataDto.getPersonnelNumber(),
				employeeDataDto.getNote()
			);

			employeeDataDto.setEmployeeId(employee.getId());
			employeeDataDto.setPersonId(employee.getParty().getId());

			// add avatar
			if (personAvatarFm.isAvatarChanged() && employeeDataDto.getImageInputStream() != null)
				employeeDataAs.addPersonAvatar(
						employeeDataDto.getPersonId(),
						employeeDataDto.getImageInputStream(),
						employeeDataDto.getImageFormatName()
				);
			//@formatter:on

			this.employee = employee;
			return outcome.construct(EmployeeCardViewModel.VIEW_ID, IdentifiableOutcomeParam.of("employee", employee));
		} else {
			OverallMessagesBundle overallMessages = LocaleUtils.getMessages(OverallMessagesBundle.class);
			PersonnelMessagesBundle personnelMessages = LocaleUtils.getMessages(PersonnelMessagesBundle.class);

			Notification.error(
					overallMessages.error(),
					personnelMessages.employeeAlreadyExists(
							employeeWithSamePersonnelNumber.getObjectName(),
							employeeWithSamePersonnelNumber.getPersonnelNumber()
					)
			);
			return StringUtils.EMPTY;
		}
	}

	public EmployeeDataDto getEditableEmployee() {
		return employeeDataDto;
	}

	public void setEditableEmployee(EmployeeDataDto employeeDataDto) {
		this.employeeDataDto = employeeDataDto;
	}

}