package ru.argustelecom.box.nri.loading;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.nri.loading.model.ResourceLoading;
import ru.argustelecom.box.nri.logicalresources.LogicalResourceDto;
import ru.argustelecom.box.nri.logicalresources.LogicalResourceDtoTranslator;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddress;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by s.kolyada on 06.02.2018.
 */
@RunWith(PowerMockRunner.class)
public class ResourceLoadingAppServiceTest {

	@InjectMocks
	private ResourceLoadingAppService testingClass;

	@Mock
	private ResourceLoadingRepository resourceLoadingRepository;

	@Mock
	private LogicalResourceDtoTranslator logicalResourceDtoTranslator;


	@Test
	public void shouldLoadAllResourcesUnderService() throws Exception {
		Service service = createService();

		when(resourceLoadingRepository.loadAllLoadingsByService(Mockito.eq(service)))
				.thenReturn(Collections.singletonList(ResourceLoading.builder()
						.loadedLogicalResource(Collections.singleton(IPAddress.builder().name("192.168.100.22").build()))
						.build()));

		when(logicalResourceDtoTranslator.translate(any())).thenReturn(LogicalResourceDto.builder().build());

		List<LogicalResourceDto> result = testingClass.loadAllLoadedResourcesByService(service);

		assertNotNull(result);
		assertFalse(result.isEmpty());
		Mockito.verify(resourceLoadingRepository, Mockito.times(1))
				.loadAllLoadingsByService(Mockito.eq(service));
		verify(logicalResourceDtoTranslator, times(1)).translate(any());
	}

	private Service createService() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		Constructor c = Service.class.getDeclaredConstructor();
		c.setAccessible(true);
		return (Service)c.newInstance();
	}
}