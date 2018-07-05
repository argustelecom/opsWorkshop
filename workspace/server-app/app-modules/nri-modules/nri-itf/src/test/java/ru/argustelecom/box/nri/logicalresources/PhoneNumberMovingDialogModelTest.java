package ru.argustelecom.box.nri.logicalresources;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberAppService;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberDto;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberPool;

import java.util.Collections;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

/**
 * @author d.khekk
 * @since 27.11.2017
 */
@RunWith(PowerMockRunner.class)
public class PhoneNumberMovingDialogModelTest {

	@Mock
	private PhoneNumberAppService service;

	@InjectMocks
	private PhoneNumberMovingDialogModel model;

	private PhoneNumberPool defaultPool = PhoneNumberPool.builder().id(1L).name("Pool").build();
	private PhoneNumberDtoTmp defaultNumber = PhoneNumberDtoTmp.builder().id(1L).name("Number").build();

	@Before
	public void setUp() throws Exception {
		model.onMovingDialogOpen(Collections.singletonList(defaultPool), Collections.singletonList(defaultNumber));
	}

	@Test
	public void shouldDoThingsOnDialogOpen() throws Exception {
		assertNotNull(model.getPools());
		assertTrue(model.getPools().contains(defaultPool));
		assertNotNull(model.getPhonesToMove());
		assertTrue(model.getPhonesToMove().contains(defaultNumber));
	}

	@Test
	public void shouldMovePhoneNumbers() throws Exception {
		doNothing().when(service).changePool(defaultNumber.convertToBaseDto(), defaultPool);
		model.setOnMoveButtonPressed(System.out::println);
		model.setSelectedPool(defaultPool);

		model.move();
		verify(service, atLeastOnce()).changePool(defaultNumber.convertToBaseDto(), defaultPool);
	}
}