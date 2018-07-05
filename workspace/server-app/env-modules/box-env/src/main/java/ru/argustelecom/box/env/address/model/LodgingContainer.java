package ru.argustelecom.box.env.address.model;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Интерфей, который должны имплементить все сущности, которые могут быть родителями для {@linkplain Lodging
 * квартир/помещений}.
 */
public interface LodgingContainer extends LocationContainer {

	default public List<Lodging> getChildLodging() {
		return getChildren().stream().filter(child -> child instanceof Lodging).map(child -> (Lodging) child)
				.collect(Collectors.toList());
	}

	public void addChild(Lodging lodging);

	public void removeChild(Lodging lodging);

}