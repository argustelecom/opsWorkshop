package ru.argustelecom.box.env.party.testdata;

import ru.argustelecom.box.env.party.AppointmentRepository;
import ru.argustelecom.box.env.party.PartyRepository;
import ru.argustelecom.box.env.party.model.Appointment;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.env.security.RoleRepository;
import ru.argustelecom.box.env.security.model.Role;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static ru.argustelecom.box.env.util.UITestUtils.getOrElse;
import static ru.argustelecom.box.env.util.UITestUtils.uniqueId;

public class EmployeeTestDataUtils implements Serializable {

    private static final long serialVersionUID = 2381727097127503110L;

    @Inject
    private AppointmentRepository appointmentRepository;

    @Inject
    private PartyRepository partyRepository;

    @Inject
    private RoleRepository roleRepository;

    @PersistenceContext
    private EntityManager em;

    /**
     * Возвращает первую найденную должность {@link Appointment}, если ничего найдено не было создает должнсть с указанным именем
     *
     * @param name Название должности
     * @return Должность
     */
    public Appointment findOrCreateTestAppointment(String name) {
        return getOrElse(appointmentRepository.allAppointments(), () ->
                appointmentRepository.createAppointment(name)
        );
    }

    /**
     * Создает и возвращает тестового сотрудника
     * @return Тестовый сотрудник
     */
    public Employee createTestEmployee() {

        String personalNumber = uniqueId();
        Appointment appointment = findOrCreateTestAppointment(personalNumber);
        String prefix = "Его высочество";
        String firstName = truncate("Имя " + personalNumber, 30);
        String secondName = truncate("Фамилия " + personalNumber, 30);
        String lastName = truncate("Отчество " + personalNumber, 30);
        String suffix = "ибн";

        return partyRepository.createEmployee(
                prefix,
                lastName,
                firstName,
                secondName,
                suffix,
                personalNumber,
                appointment,
                null,
                null
        );
    }

    public Role findOrCreateTestRole(String name) {

        List<Role> allRoles = roleRepository.queryAllRoles();
        if (!allRoles.isEmpty()) {
            return allRoles.get(0);
        }
        // описание роли не имеет значения и не видится ситуации, когда может понадобится
        return roleRepository.createRole(name, "");
    }

    /**
     * Укоротить строку до указанного размера.
     *
     * @return Строка указанной длинны
     */
    private String truncate(String text, int length) {
        if (text.length() > length) {
            return text.substring(0, length);
        } else {
            return text;
        }
    }
}
