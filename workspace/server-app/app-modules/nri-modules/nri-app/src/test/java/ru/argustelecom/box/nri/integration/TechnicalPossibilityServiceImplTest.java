package ru.argustelecom.box.nri.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.commodity.model.ServiceSpec;
import ru.argustelecom.box.integration.nri.TechPossibility;
import ru.argustelecom.box.nri.tp.TechnicalPossibilityAppService;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

/**
 * Created by s.kolyada on 09.09.2017.
 */
@RunWith(PowerMockRunner.class)
public class TechnicalPossibilityServiceImplTest {

	@Mock
	private TechnicalPossibilityAppService appService;

	@InjectMocks
	private TechnicalPossibilityServiceImpl service;

	@Test(expected = IllegalArgumentException.class)
	public void shouldValidateInput() throws Exception {
		service.checkTechnicalPossibility(null, null);
	}

	@Test
	public void shouldCheckPossibility() throws Exception {
		when(appService.checkPossibility(any(), any())).thenReturn(TechPossibility.AVAILABLE_IN_BUILDING);

		assertTrue(TechPossibility.AVAILABLE_IN_BUILDING.equals(service.checkTechnicalPossibility(ServiceSpec.builder().build(),
				new Building(1L))));
	}
}