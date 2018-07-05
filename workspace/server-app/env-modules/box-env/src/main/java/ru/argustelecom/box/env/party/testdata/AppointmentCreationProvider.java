package ru.argustelecom.box.env.party.testdata;

import ru.argustelecom.box.env.party.model.Appointment;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import javax.inject.Inject;

public class AppointmentCreationProvider implements TestDataProvider {

    public static final String APPOINTMENT_PROP_NAME = "appointment.provider.appointment";
    private static final String APPOINTMENT_TEST_NAME = "Тестовая должность";

    @Inject
    private EmployeeTestDataUtils employeeTestDataUtils;

    @Override
    public void provide(TestRunContext testRunContext) {
        Appointment appointment = employeeTestDataUtils.findOrCreateTestAppointment(APPOINTMENT_TEST_NAME);

       // ContactType contactType = Cont


        testRunContext.setBusinessPropertyWithMarshalling(APPOINTMENT_PROP_NAME, appointment);
    }
}

