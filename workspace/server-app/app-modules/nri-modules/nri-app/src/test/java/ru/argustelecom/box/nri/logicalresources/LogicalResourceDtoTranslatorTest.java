package ru.argustelecom.box.nri.logicalresources;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddress;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by s.kolyada on 06.02.2018.
 */
@RunWith(PowerMockRunner.class)
public class LogicalResourceDtoTranslatorTest {

	@InjectMocks
	private LogicalResourceDtoTranslator testingClass;

	@Test
	public void shouldTraslateNull() throws Exception {
		LogicalResourceDto res = testingClass.translate(null);

		assertNull(res);
	}

	@Test
	public void shouldTraslate() throws Exception {
		IPAddress ip = IPAddress.builder()
				.id(1L)
				.name("192.168.100.22")
				.build();

		LogicalResourceDto res = testingClass.translate(ip);

		assertNotNull(res);
		assertEquals(res.getId(), ip.getId());
		assertEquals(res.getObjectName(), ip.getName());
	}
}