package ru.argustelecom.box.env.commodity;

import lombok.val;
import org.jboss.arquillian.graphene.page.InitialPage;
import org.junit.Test;
import ru.argustelecom.box.env.commodity.testdata.CommodityGroupProvider;
import ru.argustelecom.box.env.commodity.testdata.OptionTypeTelephonyZoneProvider;
import ru.argustelecom.box.env.commodity.testdata.ServiceTypeOptionTypeProvider;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.system.inf.testframework.it.ui.navigation.LocationParameterProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.DataProvider;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static ru.argustelecom.box.env.UITestUtils.getTreeNode;
import static ru.argustelecom.box.env.UITestUtils.uniqueId;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class CommodityTypeDirectoryIT extends AbstractWebUITest implements LocationParameterProvider {

    @Test
    public void shouldCreateGroupThenDelete(@InitialPage CommodityTypeDirectoryPage page) {
        String nameVal = uniqueId("Тестовая группа");
        String keywordVal = uniqueId();

        page.openCreationDialog.click();
        page.commodityTypeCategory.select("Группа");
        page.creationDialog.name.input(nameVal);
        page.creationDialog.keyword.input(keywordVal);
        page.creationDialog.create.click();

        assertTrue(getTreeNode(page.commodityTree, nameVal).isPresent());
        assertEquals(nameVal, page.attributes.name.getValue());
        assertEquals(keywordVal, page.attributes.keyword.getValue());

        page.delete();
        assertNull(getTreeNode(page.commodityTree, nameVal));
    }

    @Test
    public void shouldCreateGoodsTypeThenDelete(
            @InitialPage CommodityTypeDirectoryPage page,
            @DataProvider(
                    contextPropertyName = CommodityGroupProvider.GROUP_NAME,
                    providerClass = CommodityGroupProvider.class
            ) String groupName
    ) {
        String nameVal = uniqueId("Тестовый тип товара");

        page.openCreationDialog.click();
        page.commodityTypeCategory.select("Тип товара");
        page.creationDialog.name.input(nameVal);
        page.creationDialog.groups.select(groupName);
        page.creationDialog.create.click();

        assertTrue(getTreeNode(page.commodityTree, groupName, nameVal).isPresent());
        assertEquals(nameVal, page.attributes.name.getValue());

        page.delete();
        assertNull(getTreeNode(page.commodityTree, groupName, nameVal));
    }

    @Test
    public void shouldCreateServiceTypeThenDelete(
            @InitialPage CommodityTypeDirectoryPage page,
            @DataProvider(
                    contextPropertyName = CommodityGroupProvider.GROUP_NAME,
                    providerClass = CommodityGroupProvider.class
            ) String groupName
    ) {
        String nameVal = uniqueId("Тестовый тип услуги");

        page.openCreationDialog.click();
        page.commodityTypeCategory.select("Тип услуги");
        page.creationDialog.name.input(nameVal);
        page.creationDialog.groups.select(groupName);
        page.creationDialog.create.click();

        assertTrue(getTreeNode(page.commodityTree, groupName, nameVal).isPresent());
        assertEquals(nameVal, page.attributes.name.getValue());

        page.delete();
        assertNull(getTreeNode(page.commodityTree, groupName, nameVal));
    }

    @Test
    public void shouldCreateOptionTypeThenDelete(
            @InitialPage CommodityTypeDirectoryPage page,
            @DataProvider(
                    contextPropertyName = CommodityGroupProvider.GROUP_NAME,
                    providerClass = CommodityGroupProvider.class
            ) String groupName
    ) {
        String nameVal = uniqueId("Тестовый тип опции");

        page.openCreationDialog.click();
        page.commodityTypeCategory.select("Тип опции");
        page.creationDialog.name.input(nameVal);
        page.creationDialog.groups.select(groupName);
        page.creationDialog.create.click();

        assertTrue(getTreeNode(page.commodityTree, groupName, nameVal).isPresent());
        assertEquals(nameVal, page.attributes.name.getValue());

        page.delete();
        assertNull(getTreeNode(page.commodityTree, groupName, nameVal));
    }

    @Test
    public void shouldAddOptionTypeToServiceType(
            @InitialPage CommodityTypeDirectoryPage page,
            @DataProvider(
                    contextPropertyName = ServiceTypeOptionTypeProvider.OPTION_TYPE_NAME,
                    providerClass = ServiceTypeOptionTypeProvider.class
            ) String optionTypeName
    ) {
        page.options.openAddOptionTypeDialog.click();
        page.options.select(optionTypeName);
        page.options.add.click();

        assertTrue(page.options.getOptionTypeNames().contains(optionTypeName));
    }

    @Test
    public void shouldAddTelephonyZoneToOptionType(
            @InitialPage CommodityTypeDirectoryPage page,
            @DataProvider(
                    contextPropertyName = OptionTypeTelephonyZoneProvider.ZONE_NAME,
                    providerClass = OptionTypeTelephonyZoneProvider.class
            ) String telephonyZoneName
    ) {
        page.telephonyZones.openAddTelephonyZoneDialog.click();
        page.telephonyZones.select(telephonyZoneName);
        page.telephonyZones.add.click();

        assertTrue(page.telephonyZones.getTelephonyZonesNames().contains(telephonyZoneName));
    }

    @Override
    public Map<String, String> provideLocationParameters() {
        Map<String, String> params = new HashMap<>();

        if (testName.getMethodName().equals("shouldAddOptionTypeToServiceType")) {
            val serviceTypeId = getTestRunContextProperty(ServiceTypeOptionTypeProvider.SERVICE_TYPE_ID, Long.class);
            params.put("selectedType", "ServiceType-" + serviceTypeId);
        }

        if (testName.getMethodName().equals("shouldAddTelephonyZoneToOptionType")) {
            val optionTypeId = getTestRunContextProperty(OptionTypeTelephonyZoneProvider.OPTION_TYPE_ID, Long.class);
            params.put("selectedType", "OptionType-" + optionTypeId);
        }

        return params;
    }
}