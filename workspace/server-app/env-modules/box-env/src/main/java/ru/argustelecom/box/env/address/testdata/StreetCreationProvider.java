package ru.argustelecom.box.env.address.testdata;

import ru.argustelecom.box.env.address.LocationLevelRepository;
import ru.argustelecom.box.env.address.model.Country;
import ru.argustelecom.box.env.address.model.LocationLevel;
import ru.argustelecom.box.env.address.model.LocationType;
import ru.argustelecom.box.env.address.model.Region;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import javax.inject.Inject;

import static ru.argustelecom.box.env.util.UITestUtils.uniqueId;

public class StreetCreationProvider implements TestDataProvider {

    public static final String COUNTRY_NAME = "StreetCreationProvider.country";
    public static final String REGION_NAME = "StreetCreationProvider.region";
    public static final String TYPE_NAME = "StreetCreationProvider.type";

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

        testRunContext.setBusinessPropertyWithMarshalling(COUNTRY_NAME, country.getObjectName());
        testRunContext.setBusinessPropertyWithMarshalling(TYPE_NAME, type.getObjectName());
        testRunContext.setBusinessPropertyWithMarshalling(REGION_NAME, region.getObjectName());
    }
}