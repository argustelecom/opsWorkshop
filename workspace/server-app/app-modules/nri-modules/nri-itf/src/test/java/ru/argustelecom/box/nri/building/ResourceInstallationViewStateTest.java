package ru.argustelecom.box.nri.building;

import org.junit.Test;
import ru.argustelecom.box.nri.coverage.model.ResourceInstallation;

import static org.junit.Assert.assertNotNull;

/**
 * Created by s.kolyada on 09.09.2017.
 */
public class ResourceInstallationViewStateTest {

	private ResourceInstallationViewState viewState = new ResourceInstallationViewState();

	@Test
	public void shouldInitialize() throws Exception {
		viewState.setInstallation(ResourceInstallation.builder().id(1L).build());

		assertNotNull(viewState.getInstallation());
	}
}