package ru.argustelecom.box.env.measure;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.junit.Test;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static ru.argustelecom.box.env.UITestUtils.getTreeNode;
import static ru.argustelecom.box.env.UITestUtils.uniqueId;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class MeasureUnitDirectoryIT extends AbstractWebUITest {

    @Test
    public void shouldCreateMeasureUnitThenDelete(@InitialPage MeasureUnitDirectoryPage page) {
        String codeVal = uniqueId("");
        String nameVal = uniqueId("Тестовая единица");
        String symbolVal = uniqueId("").substring(0, 7);
        String coefficientVal = "5";
        String groupVal = "Единицы времени";

        page.openCreationDialog.click();
        page.code.input(codeVal);
        page.name.input(nameVal);
        page.symbol.input(symbolVal);
        page.coefficient.input(coefficientVal);
        page.groups.select(groupVal);
        page.create.click();

        assertTrue(getTreeNode(page.units, groupVal, nameVal).isPresent());

        page.delete();
        assertNull(getTreeNode(page.units, groupVal, nameVal));
    }
}