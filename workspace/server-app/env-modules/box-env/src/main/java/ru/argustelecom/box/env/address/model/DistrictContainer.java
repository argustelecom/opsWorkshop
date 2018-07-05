package ru.argustelecom.box.env.address.model;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Интерфей, который должны имплементить все сущности, которые могут быть родителями для {@linkplain District районов}.
 */
public interface DistrictContainer extends LocationContainer {

	default public List<District> getChildDistricts() {
		return getChildren().stream().filter(child -> child instanceof District).map(child -> (District) child)
				.collect(Collectors.toList());
	}

	public void addChild(District district);

	public void removeChild(District district);

}