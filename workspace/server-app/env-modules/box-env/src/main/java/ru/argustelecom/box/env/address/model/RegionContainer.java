package ru.argustelecom.box.env.address.model;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Интерфей, который должны имплементить все сущности, которые могут быть родителями для {@linkplain Region регионов}.
 */
public interface RegionContainer extends LocationContainer {

	default public List<Region> getChildRegions() {
		return getChildren().stream().filter(child -> child instanceof Region).map(child -> (Region) child)
				.collect(Collectors.toList());
	}

	public void addChild(Region region);

	public void removeChild(Region region);

}