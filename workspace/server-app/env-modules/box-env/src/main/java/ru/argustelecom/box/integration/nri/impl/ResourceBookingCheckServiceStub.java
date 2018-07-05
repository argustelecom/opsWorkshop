package ru.argustelecom.box.integration.nri.impl;

import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.integration.nri.nls.IntegrationNriMessageBundle;
import ru.argustelecom.box.integration.nri.ResourceBookingCheckService;

/**
 * Заглушка для сервиса проверки полноты брони
 * Created by b.bazarov on 09.02.2018.
 */
public class ResourceBookingCheckServiceStub implements ResourceBookingCheckService {
	@Override
	public boolean check(Service serviceInstance) {
		IntegrationNriMessageBundle messages = LocaleUtils.getMessages(IntegrationNriMessageBundle.class);
		throw new UnsupportedOperationException(messages.noNriModulAvailable());
	}
}
