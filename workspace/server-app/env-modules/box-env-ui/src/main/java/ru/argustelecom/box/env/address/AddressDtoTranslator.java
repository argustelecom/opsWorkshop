package ru.argustelecom.box.env.address;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Optional.ofNullable;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;

import java.util.Optional;

import javax.inject.Inject;

import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.address.model.Lodging;
import ru.argustelecom.box.env.techservice.coverage.CoverageRepository;
import ru.argustelecom.box.env.techservice.coverage.model.Coverage;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.system.inf.exception.SystemException;

@DtoTranslator
public class AddressDtoTranslator {

	@Inject
	private CoverageRepository coverageRp;

	@Inject
	private LocationTypeDtoTranslator locationTypeDtoTr;

	@Inject
	private BuildingDtoTranslator buildingDtoTr;

	public AddressDto translate(Location location) {
		location = initializeAndUnproxy(location);

		if (location instanceof Building)
			return translateFrom((Building) location);
		if (location instanceof Lodging)
			return translateFrom((Lodging) location);

		throw new SystemException("AddressDtoTranslator supports only Building and Lodging");
	}

	private AddressDto translateFrom(Building building) {
		Optional<Coverage> coverageOptional = ofNullable(coverageRp.find(building));

		//@formatter:off
		return AddressDto.builder()
					.building(buildingDtoTr.translate(building))
					.fullName(building.getFullName())
					.coverageId(coverageOptional.map(Coverage::getId).orElse(null))
					.coverageStateName(coverageOptional.map(c -> c.getState().getObjectName()).orElse(null))
					.coverageStateColor(coverageOptional.map(c -> c.getState().getColor()).orElse(null))
					.coverageNote(coverageOptional.map(Coverage::getNote).orElse(null))
				.build();
		//@formatter:on
	}

	private AddressDto translateFrom(Lodging lodging) {
		Location parent = initializeAndUnproxy(lodging.getParent());
		checkArgument(parent instanceof Building);

		AddressDto address = translateFrom((Building) parent);

		address.setLodgingId(lodging.getId());
		address.setLodgingNumber(lodging.getNumber());
		address.setLodgingType(locationTypeDtoTr.translate(lodging.getType()));
		address.setFullName(lodging.getFullName());

		return address;
	}

}