package ru.argustelecom.box.env.address.testdata;

import javax.inject.Inject;

import com.google.common.base.Preconditions;

import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.address.model.LocationLevel;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

public class LocationLevelProvider implements TestDataProvider {

	@Inject
	private LocationTestDataUtils locationTestDataUtils;

	public static final String LEVEL_NAME = "LocationLevelProvider.level";

	@Override
	public void provide(TestRunContext testRunContext) {
		LocationLevel level = locationTestDataUtils.findOrCreateTestLocationLevel();
		testRunContext.setBusinessPropertyWithMarshalling(LEVEL_NAME, level.getObjectName());
	}
}
