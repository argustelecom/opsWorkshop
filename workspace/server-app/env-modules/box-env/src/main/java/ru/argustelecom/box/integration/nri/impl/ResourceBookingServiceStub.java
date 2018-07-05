package ru.argustelecom.box.integration.nri.impl;

import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.service.DomainService;
import ru.argustelecom.box.integration.nri.nls.IntegrationNriMessageBundle;
import ru.argustelecom.box.integration.nri.ResourceBookingResult;
import ru.argustelecom.box.integration.nri.ResourceBookingService;
import ru.argustelecom.box.integration.nri.ResourceLoadingResult;
import ru.argustelecom.box.integration.nri.ResourceLoadingService;

/**
 * Заглушка для сервиса бронирования ресурсов
 * Created by s.kolyada on 18.12.2017.
 */
@DomainService
public class ResourceBookingServiceStub implements ResourceBookingService,ResourceLoadingService {

	@Override
	public ResourceBookingResult bookResources(Service serviceInstance) {
		unavailable();
		return null;
	}

	@Override
	public ResourceBookingResult releaseBooking(Service serviceInstance) {
		unavailable();
		return null;
	}

	@Override
	public ResourceLoadingResult loadResources(Service serviceInstance) {
		unavailable();
		return null;
	}

	@Override
	public ResourceLoadingResult releaseLoading(Service serviceInstance) {
		unavailable();
		return null;
	}

	private void unavailable() {
		IntegrationNriMessageBundle messages = LocaleUtils.getMessages(IntegrationNriMessageBundle.class);
		throw new UnsupportedOperationException(messages.noNriModulAvailable());
	}
}
