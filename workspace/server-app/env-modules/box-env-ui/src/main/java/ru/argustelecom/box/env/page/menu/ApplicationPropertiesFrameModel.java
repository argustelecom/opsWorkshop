package ru.argustelecom.box.env.page.menu;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.Serializable;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.box.env.billing.bill.PrefTableRepository;
import ru.argustelecom.system.inf.configuration.ServerRuntimeProperties;

@Named(value = "applicationPropertiesFm")
@ApplicationScoped
public class ApplicationPropertiesFrameModel implements Serializable {

	@Inject
	private PrefTableRepository prefTableRp;

	@Getter
	private PropertiesDto properties;

	@PostConstruct
	private void postConstruct() {
		if (properties == null) {
			ServerRuntimeProperties runtimeProperties = ServerRuntimeProperties.instance();
			
			String appVersion = runtimeProperties.getAppBuildNumber();
			if (isNullOrEmpty(appVersion) || Objects.equals(appVersion, runtimeProperties.getAppVersion())) {
				appVersion = runtimeProperties.getBoxVersion();
			}
			
			//@formatter:off
			properties = new PropertiesDto(
				appVersion,
				prefTableRp.getDbVersion(),
				runtimeProperties.getDbName()
			);
			//@formatter:on
		}
	}

	@Getter
	@AllArgsConstructor
	public class PropertiesDto {

		private String appVersion;
		private String dbVersion;
		private String dbName;

	}

	private static final long serialVersionUID = 7569554792466680727L;

}