package ru.argustelecom.box.nri.logicalresources.ip.address;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddress;
import ru.argustelecom.system.inf.transaction.UnitOfWork;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author d.khekk
 * @since 11.12.2017
 */
@RunWith(PowerMockRunner.class)
public class IPAddressViewModelTest {

	@Mock
	private IPAddressViewState viewState;

	@Mock
	private IPAddressRepository repository;

	@Mock
	private IPAddressAppService service;

	@Mock
	private IPAddressDtoTranslatorTmp translator;

	@Mock
	private UnitOfWork unitOfWork;

	@InjectMocks
	private IPAddressViewModel model;

	private IPAddress defaultIp = new IPAddress(1L);
	private IPAddressDtoTmp defaultDto = IPAddressDtoTmp.builder().id(1L).build();

	@Before
	public void setUp() throws Exception {
		doNothing().when(unitOfWork).makePermaLong();
		when(translator.translate(defaultIp)).thenReturn(defaultDto);
		when(viewState.getIpAddress()).thenReturn(defaultIp);
		model.postConstruct();
	}

	@Test
	public void shouldReturnRealIp() {
		when(viewState.getIpAddress()).thenReturn(defaultIp)
		;
		IPAddress realIpAddress = model.getRealIpAddress();

		assertNotNull(realIpAddress);
		assertEquals(defaultIp, realIpAddress);
	}

	@Test
	public void shouldChangeComment() {
		//TODO
//		doNothing().when(service).changeComment(defaultDto);
//
//		model.changeComment();
//
//		verify(service, atLeastOnce()).changeComment(defaultDto);
	}
}