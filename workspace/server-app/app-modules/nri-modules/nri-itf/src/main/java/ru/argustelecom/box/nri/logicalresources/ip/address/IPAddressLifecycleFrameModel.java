package ru.argustelecom.box.nri.logicalresources.ip.address;

import lombok.Getter;
import ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseListener;
import ru.argustelecom.box.nri.logicalresources.ip.address.lifecycle.IPAddressState;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddress;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Named;
import java.io.Serializable;

/**
 * Фрейм для смены статуса IP-адреса
 *
 * @author d.khekk
 * @since 11.12.2017
 */
@Named("ipAddressLifecycleFM")
@PresentationModel
public class IPAddressLifecycleFrameModel extends LifecyclePhaseListener<IPAddressState, IPAddress> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * IP-адрес
	 */
	@Getter
	private IPAddress ipAddress;

	@Override
	public void beforeInitialization(IPAddress businessObject) {
		super.beforeInitialization(businessObject);
		this.ipAddress = businessObject;
	}

	@Override
	public void afterFinalization(IPAddress businessObject, IPAddressState oldState) {
		super.afterFinalization(businessObject, oldState);
		this.ipAddress = null;
	}
}
