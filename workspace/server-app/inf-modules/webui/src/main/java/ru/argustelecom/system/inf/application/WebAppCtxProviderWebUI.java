package ru.argustelecom.system.inf.application;

import ru.argustelecom.system.inf.application.WebAppCtx.WebApp;

/**
 * Идентифицирует себя как {@link WebApp#WEBUI}.
 * <p>
 * Умышленно лежит здесь, единственный класс в war. Остальные исходники разнесены по продуктам, и этот класс мог бы быть
 * в system-ui, но он служит в т.ч. ортогональной цели: отличать webui от webui-mobile в отдельных моментах
 * инфраструктуры.
 * 
 * @author s.golovanov
 * @see ru.argustelecom.system.inf.application.WebAppCtxProviderWebUIMobile
 */
public class WebAppCtxProviderWebUI extends WebAppCtxProvider {
	@Override
	protected WebApp determine() {
		return WebApp.BOXUI;
	}
}
