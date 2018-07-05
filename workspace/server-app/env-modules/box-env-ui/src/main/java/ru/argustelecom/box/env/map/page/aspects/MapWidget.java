package ru.argustelecom.box.env.map.page.aspects;

import java.io.Serializable;

import ru.argustelecom.system.inf.map.component.LMapWidget;
import ru.argustelecom.system.inf.page.PresentationModel;

/**
 * Всякие аспекты хотят иметь виджет карты сети - этот бин держит виджет, чтобы не пробрасывать его отдельно в каждый
 * бин, связанный с аспектами.
 * 
 * @author s.golovanov
 */
@PresentationModel
public class MapWidget implements Serializable {

	private static final long serialVersionUID = 1L;

	private LMapWidget value;

	public LMapWidget get() {
		return value;
	}

	public void set(LMapWidget value) {
		this.value = value;
	}

}
