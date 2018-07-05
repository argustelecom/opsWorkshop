package ru.argustelecom.box.env.personnel;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.junit.Test;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Row;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;

import static org.junit.Assert.assertEquals;
import static ru.argustelecom.box.env.UITestUtils.uniqueId;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class AppointmentDirectoryIT extends AbstractWebUITest  {

    @Test
    public void shouldOpenPageAndCreationDialog(@InitialPage AppointmentDirectoryPage page) {
        String nameVal = uniqueId("Тестовая должность");

        page.openCreationDialog.click();
        page.name.input(nameVal);
        page.create.click();

        Row appointmentRow = page.appointmentTable.findRow(nameVal);
        assertEquals(nameVal, appointmentRow.getCell(1).getTextString());
    }
}
