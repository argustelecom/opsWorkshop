package ru.argustelecom.box.env.commodity.telephony.lifecycle;

import static ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionState.ACTIVE;
import static ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionState.INACTIVE;

import java.io.Serializable;

import javax.inject.Inject;

import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOption;
import ru.argustelecom.box.env.lifecycle.api.LifecycleRoutingService;
import ru.argustelecom.box.inf.service.DomainService;

@DomainService
public class TelephonyOptionRoutingService implements Serializable {

	private static final long serialVersionUID = 2062892717601085024L;

	@Inject
	private LifecycleRoutingService routingSrv;

	public boolean activate(TelephonyOption option) {
		if (option.inState(INACTIVE)) {
			routingSrv.performRouting(option, ACTIVE, false);
			return true;
		}

		return false;
	}

	public boolean deactivate(TelephonyOption option) {
		if (option.inState(ACTIVE)) {
			routingSrv.performRouting(option, INACTIVE, false);
			return true;
		}

		return false;
	}

}
