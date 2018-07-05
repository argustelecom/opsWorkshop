package ru.argustelecom.box.nri.resources.lifecycle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationDto;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by s.kolyada on 16.11.2017.
 */
@RunWith(PowerMockRunner.class)
public class LifecycleSpecificationsInfoFrameTest {

	@Mock
	private ResourceLifecycleAppService lifecycleAppService;

	@InjectMocks
	private LifecycleSpecificationsInfoFrame testingClass;

	@Test
	public void callPreRender() throws Exception {
		ResourceLifecycleDto lifecycleDto = ResourceLifecycleDto.builder().build();

		when(lifecycleAppService.findResourceSpecificationsWithLifecycle(eq(lifecycleDto)))
				.thenReturn(Arrays.asList(ResourceSpecificationDto.builder().build()));

		testingClass.preRender(lifecycleDto);

		assertEquals(lifecycleDto, testingClass.getLifecycle());
		assertNotNull(testingClass.getSupportingSpecifications());
		assertFalse(testingClass.getSupportingSpecifications().isEmpty());

		verify(lifecycleAppService,times(1))
				.findResourceSpecificationsWithLifecycle(eq(lifecycleDto));
	}
}