package ru.argustelecom.system.inf.application;

import ru.argustelecom.system.inf.application.WebAppCtx.WebApp;

public class WebAppCtxProviderBoxPersonalArea extends WebAppCtxProvider {

	@Override
	protected WebApp determine() {
		return WebApp.BOX_PERSONAL_AREA;
	}

}
