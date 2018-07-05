package ru.argustelecom.box.env.address;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.NoSuchElementException;
import ru.argustelecom.box.env.address.testdata.BuildingCreationProvider;
import ru.argustelecom.box.env.address.testdata.RegionCreationProvider;
import ru.argustelecom.box.env.address.testdata.StreetCreationProvider;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.DataProvider;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.argustelecom.box.env.UITestUtils.*;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class AddressDirectoryIT extends AbstractWebUITest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldCreateCountry(@InitialPage AddressDirectoryPage page) {
        String countryName = uniqueId("Тестовая страна");

        page.openCreationDialog.click();
        page.categories.select("Страна");
        page.creationDialog.name.input(countryName);
        page.creationDialog.create.click();

        assertTrue(getTreeNode(page.locationTree, countryName).isPresent());
    }

    @Test
    public void shouldCreateRegion(
            @InitialPage AddressDirectoryPage page,
            @DataProvider(
                    providerClass = RegionCreationProvider.class,
                    contextPropertyName = RegionCreationProvider.COUNTRY_NAME
            ) String countryName
    ) {
        String nameVal = uniqueId("Тестовый регион");
        String levelName = getTestRunContextProperty(RegionCreationProvider.LEVEL_NAME, String.class);
        String typeName = getTestRunContextProperty(RegionCreationProvider.TYPE_NAME, String.class);

        getTreeNode(page.locationTree, countryName).select();

        page.openCreationDialog.click();
        page.categories.select("Регион");
        page.creationDialog.name.input(nameVal);
        page.creationDialog.levels.select(levelName);
        page.creationDialog.types.select(typeName);
        page.creationDialog.create.click();

        assertEquals(nameVal, page.attributes.name.getValue());
        assertEquals(typeName, page.attributes.type.getValue());
    }

    @Test
    public void shouldCreateStreet(
            @InitialPage AddressDirectoryPage page,
            @DataProvider(
                    providerClass = StreetCreationProvider.class,
                    contextPropertyName = StreetCreationProvider.COUNTRY_NAME
            ) String countryName
    ) {
        String nameVal = uniqueId("Тестовая улица");
        String regionName = getTestRunContextProperty(StreetCreationProvider.REGION_NAME, String.class);
        String typeName = getTestRunContextProperty(StreetCreationProvider.TYPE_NAME, String.class);

        getTreeNode(page.locationTree, countryName, regionName).select();

        page.openCreationDialog.click();
        page.categories.select("Улица");
        page.creationDialog.name.input(nameVal);
        page.creationDialog.types.select(typeName);
        page.creationDialog.create.click();

        assertEquals(nameVal, page.attributes.name.getValue());
        assertEquals(typeName, page.attributes.type.getValue());
    }

    @Test
    public void shouldCreateBuildingThenDelete(
            @InitialPage AddressDirectoryPage page,
            @DataProvider(
                    providerClass = BuildingCreationProvider.class,
                    contextPropertyName = BuildingCreationProvider.COUNTRY_NAME
            ) String countryName
    ) {
        String regionName = getTestRunContextProperty(BuildingCreationProvider.REGION_NAME, String.class);
        String streetName = getTestRunContextProperty(BuildingCreationProvider.STREET_NAME, String.class);

        getTreeNode(page.locationTree, countryName, regionName, streetName).select();

        String numberVal = uniqueId();
        String corpusVal = "2-И";
        String wingVal = "11";
        String postIndexVal = "87979";
        String landmarkVal = "Тестовый ориентир";

        page.openBuildingCreationDialog.click();
        page.buildingCreationDialog.number.input(numberVal);
        page.buildingCreationDialog.corpus.input(corpusVal);
        page.buildingCreationDialog.wing.input(wingVal);
        page.buildingCreationDialog.postIndex.input(postIndexVal);
        page.buildingCreationDialog.landmark.input(landmarkVal);
        page.buildingCreationDialog.create.click();

        assertEquals(numberVal, lastRowCell(page.buildings, 2).getTextString());
        assertEquals(corpusVal, lastRowCell(page.buildings, 3).getTextString());
        assertEquals(wingVal, lastRowCell(page.buildings, 4).getTextString());
        assertEquals(landmarkVal, lastRowCell(page.buildings, 5).getTextString());
        assertEquals(postIndexVal, lastRowCell(page.buildings, 6).getTextString());

        lastRow(page.buildings).select();
        page.deleteBuilding();

        exception.expect(NoSuchElementException.class);
        page.buildings.findRow(numberVal);
    }
}