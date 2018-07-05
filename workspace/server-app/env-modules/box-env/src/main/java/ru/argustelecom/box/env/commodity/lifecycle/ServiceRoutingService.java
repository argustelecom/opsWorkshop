package ru.argustelecom.box.env.commodity.lifecycle;

import java.io.Serializable;

import javax.inject.Inject;

import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.commodity.model.ServiceState;
import ru.argustelecom.box.env.lifecycle.api.LifecycleRoutingService;
import ru.argustelecom.box.inf.service.DomainService;

@DomainService
public class ServiceRoutingService implements Serializable {

	private static final long serialVersionUID = -7044866743350028187L;

	@Inject
	private LifecycleRoutingService routingService;

	public boolean activate(Service service) {
		if (service.inState(ServiceState.INACTIVE)) {
			routingService.performRouting(service, ServiceState.ACTIVE, false);
			return true;
		}

		return false;
	}

	public boolean deactivate(Service service) {
		if (service.inState(ServiceState.ACTIVE)) {
			routingService.performRouting(service, ServiceState.INACTIVE, false);
			return true;
		}

		return false;
	}

}
