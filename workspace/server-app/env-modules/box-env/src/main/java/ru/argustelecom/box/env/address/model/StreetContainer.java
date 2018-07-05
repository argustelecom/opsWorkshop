package ru.argustelecom.box.env.address.model;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Интерфей, который должны имплементить все сущности, которые могут быть родителями для {@linkplain Street улиц}.
 */
public interface StreetContainer extends LocationContainer {

	default public List<Street> getChildStreets() {
		return getChildren().stream().filter(child -> child instanceof Street).map(child -> (Street) child)
				.collect(Collectors.toList());
	}

	public void addChild(Street street);

	public void removeChild(Street street);

}