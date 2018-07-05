package ru.argustelecom.box.env.address.testdata;

import ru.argustelecom.box.env.address.LocationLevelRepository;
import ru.argustelecom.box.env.address.model.*;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import javax.inject.Inject;

import static ru.argustelecom.box.env.util.UITestUtils.uniqueId;

public class BuildingCreationProvider implements TestDataProvider {

    public static final String COUNTRY_NAME = "BuildingCreationProvider.country";
    public static final String REGION_NAME = "BuildingCreationProvider.region";
    public static final String STREET_NAME = "BuildingCreationProvider.type";

    @Inject
    private LocationTestDataUtils locationTestDataUtils;

    @Inject
    private LocationLevelRepository locationLevelRp;

    @Override
    public void provide(TestRunContext testRunContext) {
        Country country = locationTestDataUtils.findOrCreateCountry(uniqueId("Тестовая страна"));
        LocationLevel level = locationLevelRp.street();
        LocationType type = locationTestDataUtils.findOrCreateLocationType(level, uniqueId("Тестовый тип"), "тт", true);
        Region region = locationTestDataUtils.findOrCreateRegion(country, type, uniqueId("Тестовый регион"));
        Street street = locationTestDataUtils.findOrCreateStreet(region, type, uniqueId("Тестовая улица"));

        testRunContext.setBusinessPropertyWithMarshalling(COUNTRY_NAME, country.getObjectName());
        testRunContext.setBusinessPropertyWithMarshalling(REGION_NAME, region.getObjectName());
        testRunContext.setBusinessPropertyWithMarshalling(STREET_NAME, street.getObjectName());
    }
}