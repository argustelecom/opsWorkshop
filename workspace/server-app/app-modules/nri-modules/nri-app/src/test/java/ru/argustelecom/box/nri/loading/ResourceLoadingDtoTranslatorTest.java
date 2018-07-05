package ru.argustelecom.box.nri.loading;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.nri.loading.model.ResourceLoading;
import ru.argustelecom.box.nri.logicalresources.LogicalResourceDto;
import ru.argustelecom.box.nri.logicalresources.LogicalResourceDtoTranslator;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddress;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by s.kolyada on 06.02.2018.
 */
@RunWith(PowerMockRunner.class)
public class ResourceLoadingDtoTranslatorTest {

	@InjectMocks
	private ResourceLoadingDtoTranslator testingClass;

	@Mock
	private LogicalResourceDtoTranslator logicalResourceDtoTranslator;

	@Test
	public void shouldTranslateNull() throws Exception {
		ResourceLoadingDto res = testingClass.translate(null);
		assertNull(res);
	}

	@Test
	public void shouldTranslate() throws Exception {
		ResourceLoading loading = ResourceLoading.builder()
				.id(1L)
				.loadedLogicalResource(Collections.singleton(IPAddress.builder()
						.name("192.168.100.22")
						.build()))
				.build();

		when(logicalResourceDtoTranslator.translate(anyObject())).thenReturn(LogicalResourceDto.builder()
			.build());

		ResourceLoadingDto dto = testingClass.translate(loading);

		assertEquals(dto.getId(), loading.getId());
		assertFalse(dto.getResources().isEmpty());

		verify(logicalResourceDtoTranslator, times(1)).translate(anyObject());
	}
}