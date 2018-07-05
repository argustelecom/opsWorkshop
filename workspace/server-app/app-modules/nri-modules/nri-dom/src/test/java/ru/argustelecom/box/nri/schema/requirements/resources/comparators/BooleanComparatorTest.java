package ru.argustelecom.box.nri.schema.requirements.resources.comparators;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;

/**
 * Created by b.bazarov on 24.10.2017.
 */
@RunWith(PowerMockRunner.class)
public class BooleanComparatorTest {

	@InjectMocks
	private BooleanComparator testingClass;

	@Test(expected = IllegalStateException.class)
	public void shouldIllegalStateExceptionOnCompare(){
		testingClass.compare(CompareAction.CONTAINS,null,null);
	}
	@Test
	public void shouldCompareNullNotNullFalse(){
		String b1 = null;
		String b2 = new Boolean(true).toString();
		assertFalse(testingClass.compare(CompareAction.EQUALS,b1,b2));
	}
	@Test
	public void shouldCompareNotNullNullTrue(){
		String b1 = new Boolean(true).toString();
		String b2 = null;
		assertTrue(testingClass.compare(CompareAction.NOT_EQUALS,b1,b2));
	}
	@Test
	public void shouldCompareTrueTrueTrue(){
		String b1 = new Boolean(true).toString();
		String b2 = new Boolean(true).toString();
		assertTrue(testingClass.compare(CompareAction.EQUALS,b1,b2));
	}
	@Test
	public void shouldCompareTrueTrueFalse(){
		String b1 = new Boolean(true).toString();
		String b2 = new Boolean(true).toString();
		assertFalse(testingClass.compare(CompareAction.NOT_EQUALS,b1,b2));
	}

}