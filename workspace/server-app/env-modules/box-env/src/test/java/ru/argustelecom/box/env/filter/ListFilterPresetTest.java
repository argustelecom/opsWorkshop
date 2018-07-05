package ru.argustelecom.box.env.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Set;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;

import ru.argustelecom.box.env.filter.model.FilterParam;
import ru.argustelecom.box.env.filter.model.ListFilterPreset;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.system.inf.chrono.DateUtils;

@RunWith(MockitoJUnitRunner.class)
public class ListFilterPresetTest {

	@InjectMocks
	ListFilterPresetRepository repository;

	@Mock
	IdSequenceService sequenceService;

	@Mock
	EntityManager em;

	FilterParam stringFilterParam;
	FilterParam dateFilterParam;
	FilterParam numberFilterParam;

	@Before
	public void setup() throws ParseException {
		stringFilterParam = FilterParam.create("StringFilter", "Какая-то строка");
		dateFilterParam = FilterParam.create("DateFilter",
				new SimpleDateFormat(DateUtils.DATETIME_DEFAULT_PATTERN).parse("01.01.2017 22:30"));
		numberFilterParam = FilterParam.create("NumberFilter", 40L);
		when(sequenceService.nextValue(ListFilterPreset.class)).thenReturn(1L);
	}

	@Test
	public void shouldCreateListFilterPreset() {
		ListFilterPreset listFilterPreset = createListFilterPreset(Sets.newHashSet(stringFilterParam, dateFilterParam));
		assertEquals(2, listFilterPreset.getFilterParams().size());
		assertTrue(listFilterPreset.getFilterParams().contains(stringFilterParam));
		assertTrue(listFilterPreset.getFilterParams().contains(dateFilterParam));
	}

	@Test
	public void shouldEditListFilterPreset() {
		ListFilterPreset listFilterPreset = createListFilterPreset(Sets.newHashSet(stringFilterParam, dateFilterParam));
		repository.save(listFilterPreset, Sets.newHashSet(numberFilterParam));
		assertEquals(1, listFilterPreset.getFilterParams().size());
		assertTrue(listFilterPreset.getFilterParams().contains(numberFilterParam));
	}

	private ListFilterPreset createListFilterPreset(Set<FilterParam> filters) {
		return repository.create("Test", new Employee(1L), "EMPLOYEE_LIST", filters);
	}
}
