package ru.argustelecom.box.nri.logicalresources.ip.address;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddress;
import ru.argustelecom.box.nri.logicalresources.ip.subnet.IPSubnetDtoTranslator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author d.khekk
 * @since 11.12.2017
 */
@RunWith(PowerMockRunner.class)
public class IPAddressDtoTranslatorTest {

	@InjectMocks
	private IPAddressDtoTranslator translator;

	@Mock
	private IPSubnetDtoTranslator subnetDtoTranslator;

	@Test
	public void shouldTranslate() {
		IPAddress ipAddress = new IPAddress(1L);
		ipAddress.setName("192.168.100.100");

		IPAddressDto dto = translator.translate(ipAddress);
		assertNotNull(dto);
		assertEquals(new Long(1L), dto.getId());
		Mockito.verify(subnetDtoTranslator, Mockito.times(1)).translateLazy(Mockito.any());
	}

	@Test
	public void shouldReturnNull() {
		IPAddress ipAddress = null;

		IPAddressDto dto = translator.translate(ipAddress);
		assertNull(dto);
	}
}