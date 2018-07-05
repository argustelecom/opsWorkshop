package ru.argustelecom.box.nri.schema.requirements.resources.comparators;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;

/**
 * Created by s.kolyada on 17.10.2017.
 */
@RunWith(PowerMockRunner.class)
public class NumberToNumberComparatorTest {

	@InjectMocks
	private NumberToNumberComparator testingClass;


	@Test
	public void shouldReturnFalseToEmtyValue() throws Exception {
		assertFalse(testingClass.compare(CompareAction.EQUALS, "", "1"));
		assertFalse(testingClass.compare(CompareAction.EQUALS, "1", ""));
		assertFalse(testingClass.compare(CompareAction.EQUALS, "", ""));
		assertFalse(testingClass.compare(CompareAction.EQUALS, "1", null));
		assertFalse(testingClass.compare(CompareAction.EQUALS, null, "1"));
		assertFalse(testingClass.compare(CompareAction.EQUALS, null, null));
	}
}