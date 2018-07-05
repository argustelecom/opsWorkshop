package ru.argustelecom.box.nri.logicalresources;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberPool;
import ru.argustelecom.system.inf.page.PresentationState;

import java.io.Serializable;

/**
 * Состояние страницы пулов
 *
 * @author d.khekk
 * @since 28.11.2017
 */
@PresentationState
public class PhoneNumberPoolViewState implements Serializable {

	private static final Long serialVersionUID = 1L;

	/**
	 * Пул для загрузки
	 */
	@Getter
	@Setter
	private PhoneNumberPool pool;
}
