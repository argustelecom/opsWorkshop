package ru.argustelecom.box.env.map.page.mapsearchresult;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

import org.geolatte.geom.G2D;

import ru.argustelecom.system.inf.map.component.model.MapModel;
import ru.argustelecom.system.inf.map.component.model.MapViewport;
import ru.argustelecom.system.inf.map.component.model.baselayer.BaseLayer;
import ru.argustelecom.system.inf.map.geojson.Feature;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public abstract class MapSearchResult implements Serializable {
	private static final long serialVersionUID = -4966156879815385134L;

	public void onStart() {
	}

	public abstract void onFinish();

	public abstract boolean isSupportedObject(Object o);

	public abstract void loadData();

	public abstract void populateMapModel(MapModel mapModel);

	public void mapObjectChanged() {
	}

	/**
	 * Опциональная реакция на редактирование объекта карты.
	 * Вызывается из метода onFeatureEdited модели карты {@link ru.argustelecom.box.env.map.page.MapViewModel}
	 */
	public void onFeatureEdited(String layerId, Feature feature) {

	};

	public MapViewport searchViewport(Set<G2D> positions, BaseLayer baseLayer){
		
		Double minLon = positions.stream().map(p -> p.getLon()).collect(Collectors.toSet()).stream().min(Double::compareTo).get();
		Double minLat = positions.stream().map(p -> p.getLat()).collect(Collectors.toSet()).stream().min(Double::compareTo).get();
		
		Double maxLon = positions.stream().map(p -> p.getLon()).collect(Collectors.toSet()).stream().max(Double::compareTo).get();
		Double maxLat = positions.stream().map(p -> p.getLat()).collect(Collectors.toSet()).stream().max(Double::compareTo).get();
		
		return new MapViewport(new G2D(minLon, minLat), new G2D(maxLon, maxLat), baseLayer);
	}
}
