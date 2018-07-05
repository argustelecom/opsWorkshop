package ru.argustelecom.box.nri.logicalresources;

import lombok.Getter;
import ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseListener;
import ru.argustelecom.box.nri.logicalresources.phone.lifecycle.PhoneNumberState;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumber;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Named;
import java.io.Serializable;

/**
 * Фрейм для смены статуса номера телефона
 *
 * @author d.khekk
 * @since 03.11.2017
 */
@Named("phoneLifecycleFM")
@PresentationModel
public class PhoneNumberLifecycleFrameModel extends LifecyclePhaseListener<PhoneNumberState, PhoneNumber> implements Serializable {

	public static final Long serialVersionUID = 1L;

	/**
	 * Номер телефона
	 */
	@Getter
	private PhoneNumber phoneNumber;

	@Override
	public void beforeInitialization(PhoneNumber businessObject) {
		super.beforeInitialization(businessObject);
		this.phoneNumber = businessObject;
	}

	@Override
	public void afterFinalization(PhoneNumber businessObject, PhoneNumberState oldState) {
		super.afterFinalization(businessObject, oldState);
		this.phoneNumber = null;
	}
}
