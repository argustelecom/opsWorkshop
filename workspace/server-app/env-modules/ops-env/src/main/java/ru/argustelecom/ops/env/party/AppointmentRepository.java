package ru.argustelecom.ops.env.party;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import ru.argustelecom.ops.env.idsequence.IdSequenceService;
import ru.argustelecom.ops.env.party.model.Appointment;
import ru.argustelecom.ops.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;

@Repository
public class AppointmentRepository implements Serializable {

	private static final long serialVersionUID = 5856544043002987782L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequence;

	public Appointment createAppointment(@NotNull String name) {
		Appointment newAppointment = new Appointment(idSequence.nextValue(Appointment.class));
		newAppointment.setName(name);
		em.persist(newAppointment);
		return newAppointment;
	}

	private static final String ALL_APPOINTMENTS = "AppointmentRepository.allAppointments";

	@NamedQuery(name = ALL_APPOINTMENTS, query = "from Appointment a")
	public List<Appointment> allAppointments() {
		return em.createNamedQuery(ALL_APPOINTMENTS, Appointment.class).getResultList();
	}

}