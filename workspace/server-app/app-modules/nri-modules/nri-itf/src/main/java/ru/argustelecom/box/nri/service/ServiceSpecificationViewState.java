package ru.argustelecom.box.nri.service;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.commodity.model.ServiceSpec;
import ru.argustelecom.system.inf.page.PresentationState;

import java.io.Serializable;

/**
 * Состояние страницы спецификации услуги
 * @author b.bazarov
 * @since 05.10.2017
 */
@PresentationState
@Getter
@Setter
public class ServiceSpecificationViewState implements Serializable {
	private static final long serialVersionUID = 1L;

	private ServiceSpec specification;
}
