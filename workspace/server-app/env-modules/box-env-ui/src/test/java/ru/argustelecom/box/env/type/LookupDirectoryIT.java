package ru.argustelecom.box.env.type;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.NoSuchElementException;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.box.env.type.testdata.LookupCategoryProvider;
import ru.argustelecom.box.env.type.testdata.LookupEntryProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.DataProvider;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;

import static org.junit.Assert.assertEquals;
import static ru.argustelecom.box.env.UITestUtils.lastRow;
import static ru.argustelecom.box.env.util.UITestUtils.uniqueId;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class LookupDirectoryIT extends AbstractWebUITest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldCreateCategory(@InitialPage LookupDirectoryPage page) {
        String nameVal = uniqueId("Тестовая категория");
        String descriptionVal = "Тестовое описание";

        page.openCategoryCreationDialog.click();
        page.categoryCreationDialog.name.input(nameVal);
        page.categoryCreationDialog.description.input(descriptionVal);
        page.categoryCreationDialog.create.click();

        assertEquals(nameVal, lastRow(page.categories).getCell(1).getTextString());
        assertEquals(descriptionVal, lastRow(page.categories).getCell(2).getTextString());
    }

    @Test
    public void shouldAddEntryToCategory(
            @InitialPage LookupDirectoryPage page,
            @DataProvider(
                    providerClass = LookupCategoryProvider.class,
                    contextPropertyName = LookupCategoryProvider.CATEGORY_NAME
            ) String categoryName
    ) {
        page.categories.findRow(categoryName).select();

        String nameVal = uniqueId("Тестовое значение");
        String descriptionVal = "Тестовое описание";

        page.openEntryCreationDialog.click();
        page.entryCreationDialog.name.input(nameVal);
        page.entryCreationDialog.description.input(descriptionVal);
        page.entryCreationDialog.create.click();

        assertEquals(nameVal, lastRow(page.entries).getCell(1).getTextString());
        assertEquals(descriptionVal, lastRow(page.entries).getCell(2).getTextString());
    }

    @Test
    public void shouldDeactivateEntry(
            @InitialPage LookupDirectoryPage page,
            @DataProvider(
                    providerClass = LookupEntryProvider.class,
                    contextPropertyName = LookupEntryProvider.ENTRY_NAME
            ) String entryName
    ) {
        String categoryName = getTestRunContextProperty(LookupEntryProvider.CATEGORY_NAME, String.class);

        page.categories.findRow(categoryName).select();
        page.entries.findRow(entryName).select();
        page.deactivate.click();

        exception.expect(NoSuchElementException.class);
        page.entries.findRow(entryName);
    }
}