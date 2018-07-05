package ru.argustelecom.box.integration.nri.service.impl;

import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.service.DomainService;
import ru.argustelecom.box.integration.nri.nls.IntegrationNriMessageBundle;
import ru.argustelecom.box.integration.nri.service.ServiceInfoService;
import ru.argustelecom.box.integration.nri.service.model.ResourceRepresentation;

import java.util.Set;

/**
 * Заглушка для сервиса получения информации об услуге из ТУ
 * см. BOX-2738
 * Created by s.kolyada on 11.04.2018.
 */
@DomainService
public class ServiceInfoServiceStub implements ServiceInfoService {

	@Override
	public Set<ResourceRepresentation> allLoadedResourcesByService(Service service) {
		IntegrationNriMessageBundle messages = LocaleUtils.getMessages(IntegrationNriMessageBundle.class);
		throw new UnsupportedOperationException(messages.noNriModulAvailable());
	}
}
