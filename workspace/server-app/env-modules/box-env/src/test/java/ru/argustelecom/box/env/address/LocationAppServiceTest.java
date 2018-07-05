package ru.argustelecom.box.env.address;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayDeque;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by b.bazarov on 17.10.2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class LocationAppServiceTest {

	@Mock
	private LocationRepository locationRepository;

	@InjectMocks
	private LocationAppService locationAppService;


	@Test
	public void getLocationsLikeEmptyString() {
		ArgumentCaptor<ArrayDeque> argument = ArgumentCaptor.forClass(ArrayDeque.class);
		locationAppService.getLocationsLike("", 1);

		verify(locationRepository, times(1)).searchLocationsLike(argument.capture(), eq(1));
		assertNotNull(argument.getValue());
		assertEquals(0, argument.getValue().size());
	}

	@Test
	public void getLocationsLike() {
		ArgumentCaptor<ArrayDeque> argument = ArgumentCaptor.forClass(ArrayDeque.class);
		locationAppService.getLocationsLike("Российская федерация,санкт-петербург,дыбенко", 1);

		verify(locationRepository, times(1)).searchLocationsLike(argument.capture(), eq(1));
		assertNotNull(argument.getValue());
		assertEquals(3, argument.getValue().size());
		assertEquals(true, argument.getValue().contains("санкт-петербург"));
		assertEquals(true, argument.getValue().contains("Российская федерация"));
		assertEquals(true, argument.getValue().contains("дыбенко"));


	}

}