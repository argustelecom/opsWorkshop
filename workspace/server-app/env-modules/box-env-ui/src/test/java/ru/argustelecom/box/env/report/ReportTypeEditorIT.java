package ru.argustelecom.box.env.report;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.junit.Test;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static ru.argustelecom.box.env.UITestUtils.uniqueId;
import static ru.argustelecom.box.env.report.model.ReportTypeState.BLOCKED;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class ReportTypeEditorIT extends AbstractWebUITest {

    @Test
    public void shouldCreateReportType(@InitialPage ReportTypeEditorPage page) {
        String nameVal = uniqueId("Тестовый тип отчёта");
        String descriptionVal = "Тестовое описание";

        page.openCreationDialog("Тип отчёта");
        page.creationDialog.name.input(nameVal);
        page.creationDialog.description.input(descriptionVal);
        page.creationDialog.create.click();

        page.reportTypesTree.getTreeNode(singletonList(nameVal)).select();

        assertEquals(nameVal, page.attributes.name.getValue());
        assertEquals(descriptionVal, page.attributes.description.getValue());
        assertEquals(BLOCKED.getName(), page.attributes.state.getValue());
    }
}
