package ru.argustelecom.box.env.address.testdata;

import com.google.common.base.Preconditions;
import ru.argustelecom.box.env.address.LocationLevelRepository;
import ru.argustelecom.box.env.address.LocationRepository;
import ru.argustelecom.box.env.address.LocationTypeRepository;
import ru.argustelecom.box.env.address.model.*;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.argustelecom.box.env.util.UITestUtils.getOrElse;
import static ru.argustelecom.box.env.util.UITestUtils.uniqueId;

public class LocationTestDataUtils implements Serializable {

    private static final long serialVersionUID = -5352837797100565393L;

    @Inject
    private LocationRepository locationRp;

    @Inject
    private LocationTypeRepository locationTypeRp;

    @Inject
    private LocationLevelRepository locationLevelRp;

    private static final String TEST_COUNTRY_NAME = "Страна";
    private static final String TEST_LOCATION_TYPE_REGION = "Город";
    private static final String TEST_REGION_NAME = "Регион";
    private static final String TEST_LOCATION_TYPE_STREET = "Улица";
    private static final String TEST_STREET_NAME = "Тестовая";

    public Location findOrCreateTestLocation() {
        Country country = findOrCreateCountry(TEST_COUNTRY_NAME);
        checkArgument(locationRp.reindex(country.getId()));

        // прежде чем создавать регион, нужно озаботиться его типом
        // COUNTRY_SUBJECT -- это видимо субъект федерации или аналог. Подходит в качестве региона
        LocationType locationTypeRegion = findOrCreateLocationType(locationLevelRp.countrySubject(),
                TEST_LOCATION_TYPE_REGION, "г.", true);
        // теперь наконец регион
        Region region = findOrCreateRegion(country, locationTypeRegion, TEST_REGION_NAME);
        checkArgument(locationRp.reindex(region.getId()));

        // аналогично тип улицы и сама улица
        LocationType streetLocationType = findOrCreateLocationType(locationLevelRp.street(),
                TEST_LOCATION_TYPE_STREET, "ул.", false);
        Street street = findOrCreateStreet(region, streetLocationType, TEST_STREET_NAME);
        checkArgument(locationRp.reindex(street.getId()));

        // здание будем каждый раз создавать. Пусть с каждым тестом появляются новые дома для людей, нужен город-сад.
        String number = UUID.randomUUID().toString().substring(0, 10);
        Building building = locationRp.createBuilding(street, number, /* corpus = */ null, /* wing = */null,
                /* postIndex = */null, /* landmark = */null);
        checkArgument(locationRp.reindex(building.getId()));
        return building;
    }

    public LocationType findOrCreateLocationType(LocationLevel locationLevel, String name, String shortName,
                                                 boolean isRegion) {
        LocationType result = null;
        List<LocationType> possibleLocationTypes = locationTypeRp.findLocationTypes(locationLevel);
        for (LocationType currentLocationType : possibleLocationTypes) {
            if (currentLocationType.getName().equals(name)) {
                result = currentLocationType;
            }
        }
        if (result == null) {
            result = isRegion ? locationTypeRp.createRegionType(locationLevel, name, shortName)
                    : locationTypeRp.createStreetType(name, shortName);
        }
        Preconditions.checkState(result != null);
        return result;

    }

    public LocationType findOrCreateTestLodgingType() {
        return getOrElse(locationTypeRp.findLodgingTypes(),
                () -> locationTypeRp.createLodgingType("Тестовое помещение", "т.")
        );
    }

    public LocationLevel findOrCreateTestLocationLevel() {
        return getOrElse(locationLevelRp.findAllLevels(),
                () -> locationLevelRp.createLevel(uniqueId("Тестовый уровень"))
        );
    }

    public Country findOrCreateCountry(@NotNull String name) {
        Country result = locationRp.findCountry(name);
        if (result != null) {
            return result;
        }
        return locationRp.createCountry(name);
    }

    public Region findOrCreateRegion(@NotNull Country country, @NotNull LocationType locationType, @NotNull String name) {
        Region result = locationRp.findRegion(country, locationType, name);
        if (result != null) {
            return result;
        }
        return locationRp.createRegion(country, name, locationType);
    }

    public Street findOrCreateStreet(@NotNull Region region, @NotNull LocationType locationType, @NotNull String name) {
        Street result = locationRp.findStreet(region, locationType, name);
        if (result != null) {
            return result;
        }
        return locationRp.createStreet(region, name, locationType);
    }
}