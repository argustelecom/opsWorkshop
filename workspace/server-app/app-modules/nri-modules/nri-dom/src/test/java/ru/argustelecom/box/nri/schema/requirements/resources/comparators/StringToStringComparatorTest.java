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
public class StringToStringComparatorTest {

	@InjectMocks
	private StringToStringComparator testingClass;


	@Test
	public void shouldBeEquals() throws Exception {
		String str1 = null;
		String str2 = null;
		assertTrue(testingClass.compare(CompareAction.EQUALS, str1, str2));

		str1 = "";
		assertTrue(testingClass.compare(CompareAction.EQUALS, str1, str2));

		str1 = "123";
		str2 = "123";
		assertTrue(testingClass.compare(CompareAction.EQUALS, str1, str2));

	}

	@Test
	public void shouldBeNotEquals() throws Exception {
		String str1 = "1";
		String str2 = null;
		assertFalse(testingClass.compare(CompareAction.EQUALS, str1, str2));

		str1 = "123444";
		str2 = "123";
		assertFalse(testingClass.compare(CompareAction.EQUALS, str1, str2));
	}

	@Test
	public void shouldContain() throws Exception {
		String str1 = "123123123";
		String str2 = "23";
		assertTrue(testingClass.compare(CompareAction.CONTAINS, str1, str2));
	}

	@Test
	public void shouldNotContain() throws Exception {
		String str1 = "";
		String str2 = "";
		assertFalse(testingClass.compare(CompareAction.CONTAINS, str1, str2));

		str1 = "12";
		str2 = "34";
		assertFalse(testingClass.compare(CompareAction.CONTAINS, str1, str2));
	}

	@Test
	public void shouldContainIn() throws Exception {
		String str1 = "123123123";
		String str2 = "23";
		assertTrue(testingClass.compare(CompareAction.CONTAINS_IN, str2, str1));
	}

	@Test
	public void shouldNotContainIn() throws Exception {
		String str1 = "123";
		String str2 = "567";
		assertFalse(testingClass.compare(CompareAction.CONTAINS_IN, str1, str2));

		str1 = "";
		str2 = "";
		assertFalse(testingClass.compare(CompareAction.CONTAINS_IN, str1, str2));
	}

	@Test(expected = IllegalStateException.class)
	public void shouldFailDueUnsupportedOperation() throws Exception {
		testingClass.compare(CompareAction.MORE, "", "");
	}
}