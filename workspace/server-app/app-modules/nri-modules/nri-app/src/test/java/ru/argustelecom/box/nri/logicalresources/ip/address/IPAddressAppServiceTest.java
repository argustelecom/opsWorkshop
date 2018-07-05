package ru.argustelecom.box.nri.logicalresources.ip.address;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddress;
import ru.argustelecom.system.inf.exception.BusinessException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author d.khekk
 * @since 11.12.2017
 */
@RunWith(PowerMockRunner.class)
public class IPAddressAppServiceTest {

	@Mock
	private IPAddressRepository repository;

	@Mock
	private IPAddressDtoTranslator translator;

	@Mock
	private IdSequenceService idSequenceService;

	@InjectMocks
	private IPAddressAppService service;

	private IPAddress defaultIp = IPAddress.builder().id(1L).name("192.168.100.93").isStatic(true).build();
	private IPAddressDto defaultDto = IPAddressDto.builder().id(1L).name("192.168.100.93").isStatic(true).build();

	@Before
	public void setUp() throws Exception {
		defaultIp.setName("102.102.102.102");
		when(translator.translate(defaultIp)).thenReturn(defaultDto);
		when(idSequenceService.nextValue(any())).thenReturn(1L);
		when(repository.create(defaultIp)).thenReturn(defaultIp);
		when(repository.findOne(1L)).thenReturn(defaultIp);
		when(repository.findOneWithRefresh(1L)).thenReturn(defaultIp);
		doNothing().when(repository).save(defaultIp);
	}

	@Test
	public void shouldCreate() {
		IPAddressDto persistedIpDto = service.create(defaultDto);

		assertNotNull(persistedIpDto);
		assertEquals(defaultDto, persistedIpDto);
	}

	@Test
	public void shouldFindOne() {
		IPAddressDto foundDto = service.findOne(1L);

		assertNotNull(foundDto);
		assertEquals(defaultDto, foundDto);
	}

	@Test
	public void shouldFindNothing() {
		IPAddressDto foundDto = service.findOne(null);

		assertNull(foundDto);
	}

	@Test
	public void shouldChangeComment() {
		service.changeComment(defaultDto);

		verify(repository, atLeastOnce()).findOneWithRefresh(1L);
		verify(repository, atLeastOnce()).save(defaultIp);
	}

	@Test(expected = BusinessException.class)
	public void shouldThrowExceptionWhileCreating() {
		service.create(IPAddressDto.builder().id(1L).build());
	}

	@Test(expected = BusinessException.class)
	public void shouldThwowExceptionWhileChangingComment() {
		service.changeComment(IPAddressDto.builder().build());
	}
}