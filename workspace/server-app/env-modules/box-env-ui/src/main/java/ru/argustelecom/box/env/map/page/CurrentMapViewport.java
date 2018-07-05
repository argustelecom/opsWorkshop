package ru.argustelecom.box.env.map.page;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.faces.context.FacesContext;

import org.geolatte.geom.Envelope;
import org.geolatte.geom.G2D;
import org.jboss.logging.Logger;

import ru.argustelecom.system.inf.map.component.LMapWidget;
import ru.argustelecom.system.inf.map.component.model.MapViewport;
import ru.argustelecom.system.inf.map.component.model.MapViewportInfAccessor;
import ru.argustelecom.system.inf.page.ObservablePresentationState;
import ru.argustelecom.system.inf.page.PresentationState;

/**
 * Текущий MapViewport.
 * <p>
 * Из него получают информацию о текущем базовом слое и видимой области карты. Он управляет видимой областью карты.
 * 
 * @author d.amelin
 */
@PresentationState
public class CurrentMapViewport extends ObservablePresentationState<MapViewport> {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(CurrentMapViewport.class);

	private MapViewport value;
	private LMapWidget mapWidget;

	/**
	 * Текущий {@link MapViewport}.
	 */
	@Override
	public MapViewport getValue() {
		return value;
	}

	@Override
	protected void doSetValue(MapViewport value) {
		this.value = value;

		// Обычно здесь логика "сделать что-то по изменению значения", но MapViewport довольно-таки жирное свойство, и
		// данный State отражает его полностью, в результате изменения частных вещей типа "видимый envelope" и "текущий
		// baseLayer" сливаются в монолитное "изменился viewport". Чтобы отличать, оверрайдим setValue, где есть
		// возможность сравнить новое значение со старым и понять изменения.
	}

	@Override
	public void setValue(MapViewport value) {
		MapViewport oldValue = getValue();
		super.setValue(value);

		// зачем это все см. в ru.argustelecom.system.inf.map.component.LMapWidget.updateViewport()
		boolean updateViewport = false;
		Envelope<G2D> oldEnvelopeToFit = oldValue == null ? null : MapViewportInfAccessor.envelopeToFit(oldValue);
		Envelope<G2D> envelopeToFit = value == null ? null : MapViewportInfAccessor.envelopeToFit(value);
		if (envelopeToFit != null && !envelopeToFit.equals(oldEnvelopeToFit)) {
			log.debugv("Команда вписать энвелоп {0}", envelopeToFit);
			updateViewport = true;
		}
		// на GET-реквесте можем получить готовый center-zoom от клиента
		// он будет засетчен сюда последним, после сетов от MapViewModel#postConstruct и
		// #onCurrentMapObjectChanged, что хорошо
		// опять же эта особая логика здесь нужна только по причинам, описанным в LMapWidget.updateViewport()
		if (!FacesContext.getCurrentInstance().isPostback()) {
			if (value != null && value.getCenter() != null && value.getZoom() != null) {
				log.debugv("Команда восстановить позицию {0}-{1}", value.getCenter(), value.getZoom());
				updateViewport = true;
			}
		}
		if (updateViewport) {
			checkNotNull(mapWidget);
			mapWidget.updateViewport(value);
		}
		// наконец из-за той же проблемы LMapWidget.updateViewport() можем получать несколько вызовов updateViewport на
		// GET-реквесте
	}

	public LMapWidget getMapWidget() {
		return mapWidget;
	}

	public void setMapWidget(LMapWidget mapWidget) {
		this.mapWidget = mapWidget;
	}

}
