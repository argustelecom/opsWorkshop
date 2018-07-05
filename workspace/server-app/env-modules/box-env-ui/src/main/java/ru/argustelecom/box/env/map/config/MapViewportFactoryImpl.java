package ru.argustelecom.box.env.map.config;

import org.geolatte.geom.Envelope;
import org.geolatte.geom.G2D;
import org.jboss.logging.Logger;
import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.address.model.Region;
import ru.argustelecom.box.env.address.model.Street;
import ru.argustelecom.box.env.map.geocoding.ObjectGeoRepository;
import ru.argustelecom.box.env.map.geocoding.SpecializedObjectGeoRepository;
import ru.argustelecom.box.env.map.geocoding.model.ObjectGeo;
import ru.argustelecom.system.inf.map.PositionUtils;
import ru.argustelecom.system.inf.map.component.model.MapViewport;
import ru.argustelecom.system.inf.map.component.model.MapViewportFactory;
import ru.argustelecom.system.inf.map.component.model.baselayer.BaseLayer;
import ru.argustelecom.system.inf.metadata.Metadata;
import ru.argustelecom.system.inf.modelbase.SuperClass;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Collections;

@Dependent
public class MapViewportFactoryImpl implements MapViewportFactory, Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(MapViewportFactoryImpl.class);

	private double pointPadding = MapViewportFactory.DEFAULT_POINT_PADDING;

	private boolean showInterpolationWarning = true;

	@Inject
	private ObjectGeoRepository geoRep;

	@Inject
	@Any
	private Instance<SpecializedObjectGeoRepository> repositories;

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public MapViewport createViewportToShow(SuperClass object, BaseLayer baseLayer) {
//
//		SpecializeEnvelopeFactory envelopeFactory = instanceFor(object);
//		if (envelopeFactory != null) {
//			return new MapViewport(envelopeFactory.createEnvelopeToShow(object, baseLayer), baseLayer);
//		}

		ObjectGeo viewportGeo = geoRep.findByObject(object, baseLayer.getMapId());

		// М.б. здесь будут нужны какие-то подключаемые продуктами "плагины",
		// чтобы wfm мог подключить способ поиска задачи или что-то такое.
		if (object instanceof Building && viewportGeo == null) {
			viewportGeo = findLocationRelatedGeo((Building) object, baseLayer.getMapId());
		}

		if (viewportGeo != null) {
			if (viewportGeo.getMbr() != null) {
				return new MapViewport(viewportGeo.getMbr(), baseLayer);
			} else {
				return new MapViewport(
						PositionUtils.padPositionToEnvelope(viewportGeo.getPosition(), getPointPadding()), baseLayer);
			}
		}

		// ВГ: по-моему, показывать весь мир или регион при ненайденном объекте это бесячее поведение. если поиск
		// достаточно надежный, то пользователь скорее сделает поиск заново, чем будет позиционировать карту вручную.
		// поэтому помогать делать зумаут не надо.
		// ДА: Да, лучше отличать случай когда не удалось найти достаточно точный viewport чтобы зумаут не бесил и
		// возвращать null. Реально это означает для зданий понимать насколько большой регион найден. Потому что если по
		// зданию нашли регион деревня, то хорошо, надо показать. Если по зданию нашли регион РФ, то это плохо, можно
		// возвращать null. Не доделал это,т.к. не придумал критерия как определить насколько точный регион найден.
		// Делать return null, если регион не нашелся, - глупо, т.к. на практике хотя бы РФ да найдётся. Думаю
		// реализация д.б. как ограничение на сколько раз можно подниматься вверх по иерархии регионов. Сейчас
		// поднимается без ограничения. Я думаю имеет смысл подниматься building->region->parentRegion, т.е. здание ->
		// НаселённыйПункт -> Область.

		// Наверно проще возвращать viewport на всю подложку, если не нашли.
		// чем возвращать null и заставлять вызывающего обрабатывать эту ситуацию.
		// При необходимости можно ввести настройку, как введена padding.
		return new MapViewport(baseLayer);
	}

	private ObjectGeo findLocationRelatedGeo(Building building, Long mapId) {
		for (SpecializedObjectGeoRepository sor : repositories) {
			if (!sor.supportsAnyClass(Collections.singleton(Location.class))) {
				continue;
			}
			return sor.findByObject(building, mapId);
		}
		return null;
	}

	private ObjectGeo findStreetRelatedGeo(Street street, Long mapId) {
		// М.б. когда-нибуть стоит искать здания второй адрес которых на этой улице.
		return findRegionRelatedGeo((Region)street.getParent(), mapId);
	}

	private ObjectGeo findRegionRelatedGeo(Region region, Long mapId) {
		return geoRep.findByObject(region, mapId);
	}

	@Override
	public double getPointPadding() {
		return pointPadding;
	}

	@Override
	public void setPointPadding(double value) {
		pointPadding = value;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isShowInterpolationWarning() {
		return showInterpolationWarning;
	}

	@Override
	public void setShowInterpolationWarning(boolean showInterpolationWarning) {
		this.showInterpolationWarning = showInterpolationWarning;
	}

	/** {@inheritDoc} */
	@Override
	public boolean fitsObject(MapViewport viewport, SuperClass value) {
		if (viewport == null || viewport.getEnvelope() == null)
			return false;

		Envelope<G2D> objectMbr;

		ObjectGeo sourceObject = geoRep.findByObject(value, viewport.getBaseLayer().getMapId());
		objectMbr = sourceObject == null ? null : sourceObject.getMbr();

		return objectMbr != null && viewport.getEnvelope().contains(objectMbr);
	}
}
