package ru.argustelecom.box.inf.queue.api;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ru.argustelecom.box.inf.queue.api.QueueProducer.Priority;

public class PriorityTest {

	@Test
	public void shouldReturnPriorityByValue() {
		Priority testedValue;

		testedValue = Priority.valueOf(Priority.HIGHEST.value());
		assertEquals(Priority.HIGHEST, testedValue);

		testedValue = Priority.valueOf(Priority.MEDIUM.value());
		assertEquals(Priority.MEDIUM, testedValue);

		testedValue = Priority.valueOf(Priority.LOWEST.value());
		assertEquals(Priority.LOWEST, testedValue);

		testedValue = Priority.valueOf(Priority.HIGHEST.value() - 1);
		assertEquals(Priority.HIGHEST, testedValue);

		testedValue = Priority.valueOf(Priority.LOWEST.value() + 1);
		assertEquals(Priority.LOWEST, testedValue);
	}

}
