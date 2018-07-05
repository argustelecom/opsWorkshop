package ru.argustelecom.box.nri.integration;

import org.apache.commons.lang3.Validate;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.commodity.model.ServiceSpec;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.service.DomainService;
import ru.argustelecom.box.integration.nri.TechPossibility;
import ru.argustelecom.box.integration.nri.TechnicalPossibilityService;
import ru.argustelecom.box.nri.integration.nls.NriIntegrationMessageBundle;
import ru.argustelecom.box.nri.tp.TechnicalPossibilityAppService;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.interceptor.Interceptor;

/**
 * Интеграционный сервис для определения технической возможности
 * Created by s.kolyada on 31.08.2017.
 */
@DomainService
@Alternative
@Priority(Interceptor.Priority.APPLICATION)
public class TechnicalPossibilityServiceImpl implements TechnicalPossibilityService {

	@Inject
	private TechnicalPossibilityAppService possibilityService;

	@Override
	public TechPossibility checkTechnicalPossibility(ServiceSpec specification, Location location) {
		NriIntegrationMessageBundle messages = LocaleUtils.getMessages(NriIntegrationMessageBundle.class);
		Validate.isTrue(location != null,messages.locationDidNotSet());
		Validate.isTrue(specification != null,messages.specificationDidNotSet());
		return possibilityService.checkPossibility(specification, location);
	}
}
