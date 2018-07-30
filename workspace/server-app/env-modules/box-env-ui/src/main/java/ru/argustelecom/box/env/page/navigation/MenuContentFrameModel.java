package ru.argustelecom.box.env.page.navigation;

import ru.argustelecom.system.inf.configuration.packages.Packages;
import ru.argustelecom.system.inf.page.PresentationModel;

import java.io.Serializable;

/**
 * Контроллер фрейма с основным меню
 * Created by s.kolyada on 30.08.2017.
 */
@PresentationModel
public class MenuContentFrameModel  implements Serializable {
	private static final long serialVersionUID = -8084190640187703883L;

	/**
	 * Доступен ли технический учёт
	 * @return истина если доступен, иначе ложь
	 */
	public Boolean isNriAvailable() {
		return Packages.instance().isPackageDeployed(777L);
	}
}
