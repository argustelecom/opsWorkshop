package ru.argustelecom.box.nri.resources;

import org.junit.Test;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;

import static org.junit.Assert.*;

public class ResourceViewStateTest {

	private ResourceViewState viewState = new ResourceViewState();

	@Test
	public void shouldInitialize() throws Exception {
		viewState.setResource(ResourceInstance.builder().id(1L).build());

		assertNotNull(viewState.getResource());
	}
}