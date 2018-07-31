package ru.argustelecom.ops.env.personnel;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.ops.env.contact.ContactEditFrameModel;
import ru.argustelecom.ops.env.idsequence.IdSequenceService;
import ru.argustelecom.ops.env.overall.nls.OverallMessagesBundle;
import ru.argustelecom.ops.env.party.model.Appointment;
import ru.argustelecom.ops.env.party.model.Party;
import ru.argustelecom.ops.env.party.model.role.Employee;
import ru.argustelecom.ops.env.party.nls.PersonnelMessagesBundle;
import ru.argustelecom.ops.env.party.model.Person;
import ru.argustelecom.ops.inf.nls.LocaleUtils;
import ru.argustelecom.ops.inf.page.outcome.OutcomeConstructor;
import ru.argustelecom.ops.inf.page.outcome.param.IdentifiableOutcomeParam;
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

	@Inject
	private IdSequenceService idSequence;

	@Getter
	@Setter
	private Employee employee;

	private String prefix;
	private String firstName;
	private String secondName;
	private String lastName;
	private String suffix;
	private String note;
	private String personnelNumber;
	private String employeeName;
	private Appointment appointment;
	private Party party;

	public void open() {
		RequestContext.getCurrentInstance().update("employee_edit_form-employee_edit_dlg");
		RequestContext.getCurrentInstance().execute("PF('employeeEditDlg').show()");
	}

	public boolean isEditMode() {
		return employee.getId() != null;
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
		employee = null;
	}

	private void edit() {
		//@formatter:off
		employeeDataAs.renamePerson(
			employee.getId(),
			employee.getPerson().getName().prefix(),
			employee.getPerson().getName().firstName(),
			employee.getPerson().getName().secondName(),
			employee.getPerson().getName().lastName(),
			employee.getPerson().getName().suffix()
		);

		employeeDataAs.editPersonData(
			employee.getId(),
			employee.getPerson().getNote()
		);

		employeeDataAs.editEmployeeData(
			employee.getId(),
			employee.getAppointment(),
			employee.getPersonnelNumber()
		);
		//@formatter:on

		RequestContext.getCurrentInstance().execute("PF('employeeEditDlg').hide()");
	}

	private String create() {
		Employee employeeWithSamePersonnelNumber = employeeDataAs
				.findEmployeeByPersonnelNumber(employee.getPersonnelNumber());

		if (employeeWithSamePersonnelNumber == null) {
			Employee employee = new Employee(idSequence.nextValue(Employee.class), employeeName, appointment,
					personnelNumber, false, idSequence.nextValue(Person.class), prefix, firstName, secondName, lastName,
					suffix, note);
			employee.setParty(party);
			this.employee = employee;
			return outcome.construct(EmployeeCardViewModel.VIEW_ID, IdentifiableOutcomeParam.of("employee", employee));
		} else {
			OverallMessagesBundle overallMessages = LocaleUtils.getMessages(OverallMessagesBundle.class);
			PersonnelMessagesBundle personnelMessages = LocaleUtils.getMessages(PersonnelMessagesBundle.class);

			Notification.error(overallMessages.error(),
					personnelMessages.employeeAlreadyExists(employeeWithSamePersonnelNumber.getObjectName(),
							employeeWithSamePersonnelNumber.getPersonnelNumber()));
			return StringUtils.EMPTY;
		}
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getPersonnelNumber() {
		return personnelNumber;
	}

	public void setPersonnelNumber(String personnelNumber) {
		this.personnelNumber = personnelNumber;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public Appointment getAppointment() {
		return appointment;
	}

	public void setAppointment(Appointment appointment) {
		this.appointment = appointment;
	}

	public Party getParty() {
		return party;
	}

	public void setParty(Party party) {
		this.party = party;
	}
}