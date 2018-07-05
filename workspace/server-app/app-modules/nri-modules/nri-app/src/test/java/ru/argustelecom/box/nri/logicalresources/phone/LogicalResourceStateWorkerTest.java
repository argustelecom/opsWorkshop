package ru.argustelecom.box.nri.logicalresources.phone;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.nri.logicalresources.phone.lifecycle.PhoneNumberState;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumber;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecification;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecificationInstance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class LogicalResourceStateWorkerTest {

	@Mock
	private PhoneNumberRepository repository;

	@InjectMocks
	private LogicalResourceStateWorker testingClass;

	@Test
	public void shouldNotSave(){
		when(repository.findAllWithState(eq(PhoneNumberState.LOCKED))).thenReturn(new ArrayList<>());
		testingClass.changeState();
		verify(repository, times(0)).save(any());
	}
	@Test
	public void shouldChange(){
		Date date = new Date();

		PhoneNumberSpecification specification4Days = new PhoneNumberSpecification(1L);
		specification4Days.setBlockedInterval(4);
		PhoneNumberSpecification specification1Days = new PhoneNumberSpecification(2L);
		specification1Days.setBlockedInterval(1);

		PhoneNumberSpecificationInstance inst4Days = mock(PhoneNumberSpecificationInstance.class);
		when(inst4Days.getType()).thenReturn(specification4Days);
		PhoneNumberSpecificationInstance inst1Days = mock(PhoneNumberSpecificationInstance.class);
		when(inst1Days.getType()).thenReturn(specification1Days);

		List<PhoneNumber> list = new ArrayList<>();
		PhoneNumber number = new PhoneNumber(1L);
		number.setSpecInstance(inst4Days);
		number.setStateChangeDate(new Date(date.getTime() - 1000*60*60*48));
		list.add(number);
		number = new PhoneNumber(2L);
		number.setStateChangeDate(new Date(date.getTime() - 1000*60*60*24));
		number.setSpecInstance(inst1Days);
		list.add(number);
		when(repository.findAllWithState(eq(PhoneNumberState.LOCKED))).thenReturn(list);
		testingClass.changeState();
		verify(repository, times(1)).save(any());
	}

	@Test
	public void shouldChangeAllZeroBlockedInterval(){
		Date date = new Date();

		PhoneNumberSpecification specification0Days = new PhoneNumberSpecification(1L);
		specification0Days.setBlockedInterval(0);


		PhoneNumberSpecificationInstance inst0Days = mock(PhoneNumberSpecificationInstance.class);
		when(inst0Days.getType()).thenReturn(specification0Days);

		List<PhoneNumber> list = new ArrayList<>();
		PhoneNumber number = new PhoneNumber(1L);
		number.setSpecInstance(inst0Days);
		number.setStateChangeDate(new Date(date.getTime() - 1000*60*60*48));
		list.add(number);
		number = new PhoneNumber(2L);
		number.setStateChangeDate(new Date(date.getTime() - 1000*60*60*24));
		number.setSpecInstance(inst0Days);
		list.add(number);
		when(repository.findAllWithState(eq(PhoneNumberState.LOCKED))).thenReturn(list);
		testingClass.changeState();
		verify(repository, times(2)).save(any());
	}
}