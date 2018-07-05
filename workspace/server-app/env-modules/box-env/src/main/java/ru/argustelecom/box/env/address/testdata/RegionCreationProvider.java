package ru.argustelecom.box.env.address.testdata;

import ru.argustelecom.box.env.address.model.Country;
import ru.argustelecom.box.env.address.model.LocationLevel;
import ru.argustelecom.box.env.address.model.LocationType;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import javax.inject.Inject;

import static ru.argustelecom.box.env.util.UITestUtils.uniqueId;

public class RegionCreationProvider implements TestDataProvider {

    public static final String COUNTRY_NAME = "RegionCreationProvider.country";
    public static final String LEVEL_NAME = "RegionCreationProvider.level";
    public static final String TYPE_NAME = "RegionCreationProvider.type";

    @Inject
    private LocationTestDataUtils locationTestDataUtils;

    @Override
    public void provide(TestRunContext testRunContext) {
        Country country = locationTestDataUtils.findOrCreateCountry(uniqueId("Тестовая страна"));
        LocationLevel level = locationTestDataUtils.findOrCreateTestLocationLevel();
        LocationType type = locationTestDataUtils.findOrCreateLocationType(level, uniqueId("Тестовый тип"), "тт", true);

        testRunContext.setBusinessPropertyWithMarshalling(COUNTRY_NAME, country.getObjectName());
        testRunContext.setBusinessPropertyWithMarshalling(LEVEL_NAME, level.getObjectName());
        testRunContext.setBusinessPropertyWithMarshalling(TYPE_NAME, type.getObjectName());
    }
}