package ru.argustelecom.box.env.personnel;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import ru.argustelecom.box.env.address.DirectoryViewModel;
import ru.argustelecom.box.env.party.AppointmentRepository;
import ru.argustelecom.box.env.party.model.Appointment;
import ru.argustelecom.box.env.party.nls.PersonnelMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@PresentationModel
public class AppointmentDirectoryViewModel extends ViewModel implements DirectoryViewModel<Appointment> {

	private static final long serialVersionUID = -1824720427025689676L;

	@Inject
	private AppointmentRepository appointmentRepository;

	private List<Appointment> types;
	private List<Appointment> selectedTypes;

	private String newName;

	private PersonnelMessagesBundle messages;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
		messages = LocaleUtils.getMessages(PersonnelMessagesBundle.class);
	}

	@Override
	public List<Appointment> getTypes() {
		if (types == null) {
			types = appointmentRepository.allAppointments();
			sortData();
		}
		return types;
	}

	@Override
	public void create() {
		Appointment newAppointment = appointmentRepository.createAppointment(newName);
		types.add(newAppointment);
		sortData();
		cleanCreationParams();
		Notification.info(messages.appointmentCreated(),
				messages.appointmentSuccessfullyCreated(newAppointment.getObjectName()));
	}

	@Override
	public void remove(Appointment directory) {
		em.remove(directory);
		types.remove(directory);
		Notification.info(messages.appointmentRemoved(),
				messages.appointmentSuccessfullyRemoved(directory.getObjectName()));
	}

	@Override
	public void cleanCreationParams() {
		newName = null;
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	@Override
	public List<Appointment> getSelectedTypes() {
		return selectedTypes;
	}

	public void setSelectedTypes(List<Appointment> selectedTypes) {
		this.selectedTypes = selectedTypes;
	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

}