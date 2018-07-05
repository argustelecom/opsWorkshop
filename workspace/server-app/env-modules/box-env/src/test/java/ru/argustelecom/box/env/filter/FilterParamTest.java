package ru.argustelecom.box.env.filter;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import ru.argustelecom.box.env.filter.model.FilterParam;
import ru.argustelecom.box.env.security.model.Role;
import ru.argustelecom.system.inf.chrono.DateUtils;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FilterParamMapper.class, EntityConverter.class})
public class FilterParamTest {

	String dateFilterParamName;
	Date dateFilterParamValue;
	String stringFilterParamName;
	String stringFilterParamValue;
	String numberFilterParamName;
	Long numberFilterParamValue;
	String enumFilterParamName;
	Enum<SomeEnum> enumFilterParamValue;
	Long entityFilterParamValueId;
	String entityFilterParamName;
	Role entityFilterParamValue;

	@Mock
	EntityManager em;

	@Before
	public void setup() throws Exception {
		dateFilterParamName = "DateFilterParam";
		dateFilterParamValue = new SimpleDateFormat(DateUtils.DATETIME_DEFAULT_PATTERN).parse("01.01.2017 22:30");
		stringFilterParamName = "StringFilter";
		stringFilterParamValue = "Какая-то строка";
		numberFilterParamName = "NumberFilter";
		numberFilterParamValue = 33L;
		enumFilterParamName = "EnumFilter";
		enumFilterParamValue = SomeEnum.SECOND_VALUE;
		entityFilterParamValueId = 1L;
		entityFilterParamName = "EntityFilter";
		entityFilterParamValue = new Role(entityFilterParamValueId);

		Mockito.when(em.getReference(Role.class, entityFilterParamValueId)).thenReturn(entityFilterParamValue);
		final EntityConverter converter = new EntityConverterWithoutCallsAnyFinalClasses(em);
		PowerMockito.mockStatic(EntityConverter.class, Answers.CALLS_REAL_METHODS.get());
		PowerMockito.whenNew(EntityConverter.class).withNoArguments().thenReturn(converter);
		PowerMockito.mockStatic(FilterParamMapper.class, Answers.CALLS_REAL_METHODS.get());
	}

	@Test
	public void shouldCreateDateFilter() {
		FilterParam dateFilterParam = FilterParam.create(dateFilterParamName, dateFilterParamValue);
		assertEquals(dateFilterParamName, dateFilterParam.getName());
		assertEquals("java.util.Date", dateFilterParam.getValueClassName());
		assertEquals("1483299000000", dateFilterParam.getValueAsString());
		assertEquals(dateFilterParamValue, dateFilterParam.getValue());
	}

	@Test
	public void shouldCreateStringFilter() {
		FilterParam stringFilterParam = FilterParam.create(stringFilterParamName, stringFilterParamValue);
		assertEquals(stringFilterParamName, stringFilterParam.getName());
		assertEquals("java.lang.String", stringFilterParam.getValueClassName());
		assertEquals("\"Какая-то строка\"", stringFilterParam.getValueAsString());
		assertEquals(stringFilterParamValue, stringFilterParam.getValue());
	}

	@Test
	public void shouldCreateNumberFilter() {
		FilterParam numberFilterParam = FilterParam.create(numberFilterParamName, numberFilterParamValue);
		assertEquals(numberFilterParamName, numberFilterParam.getName());
		assertEquals("java.lang.Long", numberFilterParam.getValueClassName());
		assertEquals("33", numberFilterParam.getValueAsString());
		assertEquals(numberFilterParamValue, numberFilterParam.getValue());
	}

	@Test
	public void shouldCreateEnumFilter() {
		FilterParam enumFilterParam = FilterParam.create(enumFilterParamName, enumFilterParamValue);
		assertEquals(enumFilterParamName, enumFilterParam.getName());
		assertEquals("ru.argustelecom.box.env.filter.FilterParamTest$SomeEnum", enumFilterParam.getValueClassName());
		assertEquals("\"SECOND_VALUE\"", enumFilterParam.getValueAsString());
		assertEquals(enumFilterParamValue, enumFilterParam.getValue());
	}

	@Test
	public void shouldCreateEntityFilter() {
		FilterParam entityFilterParam = FilterParam.create(entityFilterParamName, entityFilterParamValue);
		assertEquals(entityFilterParamName, entityFilterParam.getName());
		assertEquals("ru.argustelecom.box.env.security.model.Role", entityFilterParam.getValueClassName());
		assertEquals("Role-1", entityFilterParam.getValueAsString());
		assertEquals(entityFilterParamValue, entityFilterParam.getValue());
	}

	public static enum SomeEnum {
		FIRST_VALUE, SECOND_VALUE;
	}

	public class EntityConverterWithoutCallsAnyFinalClasses extends EntityConverter {

		public EntityConverterWithoutCallsAnyFinalClasses(EntityManager em) {
			super(em);
		}

		@Override
		protected Class<?> getClassAsObject(String className) throws Exception {
			return Role.class;
		}
	}
}
