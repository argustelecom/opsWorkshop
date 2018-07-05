package ru.argustelecom.box.env.numerationpattern.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static ru.argustelecom.box.env.numerationpattern.model.NumerationSequence.PeriodType;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ru.argustelecom.box.env.numerationpattern.NumerationSequenceGenerator;
import ru.argustelecom.box.env.numerationpattern.model.NumerationSequence;
import ru.argustelecom.box.env.numerationpattern.NumerationSequenceRepository;

@RunWith(MockitoJUnitRunner.class)
public class NumerationSequenceGeneratorTest {
	@Mock
	private NumerationSequenceRepository numerationSequenceRepository;

	@InjectMocks
	private NumerationSequenceGenerator numerationSequenceGenerator;

	@Test
	public void testCurrentValueIsNullAndCapacityIsSet() {
		NumerationSequence numerationSequence = new NumerationSequence(1L);

		numerationSequence.setName("seq");
		numerationSequence.setCurrentValue(null);
		numerationSequence.setInitialValue(10L);
		numerationSequence.setCapacity(5);

		when(numerationSequenceRepository.findNumerationSequenceByName("seq", true)).thenReturn(numerationSequence);

		assertEquals("00010", numerationSequenceGenerator.getNextNumber("seq"));
	}

	@Test
	public void testCapacityIsNull() {
		NumerationSequence numerationSequence = new NumerationSequence(1L);

		numerationSequence.setName("seq");
		numerationSequence.setInitialValue(10L);

		when(numerationSequenceRepository.findNumerationSequenceByName("seq", true)).thenReturn(numerationSequence);

		assertEquals("10", numerationSequenceGenerator.getNextNumber("seq"));
	}

	@Test
	public void testDateIsValid() {
		NumerationSequence numerationSequence = new NumerationSequence(1L);

		numerationSequence.setName("seq");
		numerationSequence.setCurrentValue(20L);
		numerationSequence.setIncrement(5L);
		numerationSequence.setValidTo(
				Date.from(LocalDate.now().plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));

		when(numerationSequenceRepository.findNumerationSequenceByName("seq", true)).thenReturn(numerationSequence);

		assertEquals("25", numerationSequenceGenerator.getNextNumber("seq"));
	}

	@Test
	public void testDateIsInvalid() {
		NumerationSequence numerationSequence = new NumerationSequence(1L);

		numerationSequence.setName("seq");
		numerationSequence.setInitialValue(5L);
		numerationSequence.setPeriod(PeriodType.DAY);
		numerationSequence.setValidTo(
				Date.from(LocalDate.now().minusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));

		when(numerationSequenceRepository.findNumerationSequenceByName("seq", true)).thenReturn(numerationSequence);

		assertEquals("5", numerationSequenceGenerator.getNextNumber("seq"));
	}

	@Test
	public void testValidDateIsNull() {
		NumerationSequence numerationSequence = new NumerationSequence(1L);

		numerationSequence.setName("seq");
		numerationSequence.setCurrentValue(5L);
		numerationSequence.setIncrement(5L);
		numerationSequence.setValidTo(null);

		when(numerationSequenceRepository.findNumerationSequenceByName("seq", true)).thenReturn(numerationSequence);

		assertEquals("10", numerationSequenceGenerator.getNextNumber("seq"));
	}
}