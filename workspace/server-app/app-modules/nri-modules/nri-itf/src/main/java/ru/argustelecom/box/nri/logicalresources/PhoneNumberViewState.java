package ru.argustelecom.box.nri.logicalresources;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumber;
import ru.argustelecom.system.inf.page.PresentationState;

import java.io.Serializable;

/**
 * Состояние страницы ресурсов
 * @author b.bazarov
 * @since 22.11.2017
 */
@PresentationState
@Getter
@Setter
public class PhoneNumberViewState implements Serializable {

	private static final long serialVersionUID = 1L;

	private PhoneNumber phoneNumber;
}
