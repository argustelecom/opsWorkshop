package ru.argustelecom.box.env.address;

import java.util.Arrays;
import java.util.Objects;

import lombok.Getter;
import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.address.model.Country;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.address.model.Lodging;
import ru.argustelecom.box.env.address.model.Region;
import ru.argustelecom.box.env.address.model.Street;

public enum LocationClass {
	B(Building.class), C(Country.class), R(Region.class), S(Street.class), L(Lodging.class), U(null);

	@Getter
	private Class<? extends Location> clazz;

	LocationClass(Class<? extends Location> clazz) {
		this.clazz = clazz;
	}

	public static LocationClass findByClass(Class<?> clazz) {
		return Arrays.stream(values()).filter(locationClass -> Objects.equals(locationClass.getClazz(), clazz))
				.findFirst().orElseThrow(IllegalArgumentException::new);
	}
}
