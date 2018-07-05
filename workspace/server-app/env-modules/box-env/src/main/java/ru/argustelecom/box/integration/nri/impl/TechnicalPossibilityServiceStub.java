package ru.argustelecom.box.integration.nri.impl;

import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.commodity.model.ServiceSpec;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.service.DomainService;
import ru.argustelecom.box.integration.nri.nls.IntegrationNriMessageBundle;
import ru.argustelecom.box.integration.nri.TechPossibility;
import ru.argustelecom.box.integration.nri.TechnicalPossibilityService;

/**
 * Заглушка для сервиса определения тех.возможности
 * Created by s.kolyada on 31.08.2017.
 */
@DomainService
public class TechnicalPossibilityServiceStub implements TechnicalPossibilityService {

	@Override
	public TechPossibility checkTechnicalPossibility(ServiceSpec serviceSpecification, Location location) {
		IntegrationNriMessageBundle messages = LocaleUtils.getMessages(IntegrationNriMessageBundle.class);
		throw new UnsupportedOperationException(messages.noNriModulAvailable());
	}
}
