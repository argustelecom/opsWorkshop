package ru.argustelecom.box.env;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import ru.argustelecom.box.env.util.SecurityUtils;

@Named
@RequestScoped
public class ModuleCheckingBean implements Serializable {

	private static final long serialVersionUID = 6865818327927375131L;

	public boolean isNriIntegrationEnabled() {
		return SecurityUtils.isNriIntegrationEnabled();
	}
}
