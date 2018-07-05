package ru.argustelecom.box.env.type.model;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

import com.google.common.collect.Lists;

import lombok.Setter;

public class OrdinalTest {

	@Test
	public void changeOrdinalNumberUp() {
		Integer min = 1, max = 5, current = 2, to = 4;
		changeOrdinalNumber(prepareOrdinal(min, max, current), to);
	}

	@Test
	public void changeOrdinalNumberDown() {
		Integer min = 1, max = 5, current = 4, to = 1;
		changeOrdinalNumber(prepareOrdinal(min, max, current), to);
	}

	@Test
	public void changeSameOrdinalNumber() {
		Integer min = 1, max = 5, current = 4, to = 4;
		changeOrdinalNumber(prepareOrdinal(min, max, current), to);
	}

	@Test
	public void changeOrdinalNumberBorderUp() {
		Integer min = 1, max = 5, current = 1, to = 5;
		changeOrdinalNumber(prepareOrdinal(min, max, current), to);
	}

	@Test
	public void changeOrdinalNumberBorderDown() {
		Integer min = 1, max = 5, current = 5, to = 1;
		changeOrdinalNumber(prepareOrdinal(min, max, current), to);
	}

	@Test(expected = IllegalArgumentException.class)
	public void changeOrdinalNumberBorder() {
		Integer min = 1, max = 5, current = 5, to = 6;
		changeOrdinalNumber(prepareOrdinal(min, max, current), to);
	}

	@Test
	public void testNormalize() {
		Integer min = 1, max = 5, current = 1, nTimes = 3;
		List<Integer> ordinalsToRepeat = Lists.newArrayList(1, 2);
		List<? extends Ordinal> results = Lists
				.newArrayList(prepareOrdinal(min, max, current, nTimes, ordinalsToRepeat).group());
		Ordinal.normalize(results);
		results.sort(Ordinal.comparator());
		IntStream.range(0, results.size())
				.forEach(index -> assertEquals(Integer.valueOf(index + 1), results.get(index).getOrdinalNumber()));
	}

	private void changeOrdinalNumber(Ordinal ordinal, Integer to) {
		Integer current = ordinal.getOrdinalNumber();
		ordinal.changeOrdinalNumber(to);

		assertEquals(ordinal.getOrdinalNumber(), to);

		IntStream.range(Integer.min(current, to), Integer.max(current, to)).forEach(index -> {
			if (current < to && current != index) {
				assertEquals(ordinal.group().get(index - 1).getOrdinalNumber(), Integer.valueOf(index - 1));
			} else if (current > to && to != index) {
				assertEquals(ordinal.group().get(index - 1).getOrdinalNumber(), Integer.valueOf(index + 1));
			}
		});
	}

	private Ordinal prepareOrdinal(Integer start, Integer end, Integer current) {
		return prepareOrdinal(start, end, current, null, Lists.newArrayList());
	}

	private Ordinal prepareOrdinal(Integer start, Integer end, Integer current, Integer nTimes,
			List<Integer> repeatOrdinals) {
		List<Ordinal> resultList = IntStream.rangeClosed(start, end)
				.filter(ordinalNumber -> !current.equals(ordinalNumber)).mapToObj(OrdinalTestImpl::new)
				.collect(() -> repeat(nTimes, repeatOrdinals), List::add, List::addAll);
		OrdinalTestImpl ordinal = new OrdinalTestImpl(current);
		resultList.add(ordinal);
		resultList.sort(Ordinal.comparator());
		ordinal.setGroup(Collections.unmodifiableList(resultList));
		return ordinal;
	}

	private List<Ordinal> repeat(Integer nTimes, List<Integer> repeatOrdinals) {
		return repeatOrdinals.stream().flatMap(ordinal -> IntStream.rangeClosed(1, nTimes)
				.mapToObj(value -> new OrdinalTestImpl(ordinal)).collect(Collectors.toList()).stream())
				.collect(Collectors.toList());
	}

	private class OrdinalTestImpl implements Ordinal {

		private Integer ordinalNumber;
		@Setter
		private List<Ordinal> group;

		OrdinalTestImpl(Integer ordinalNumber) {
			this.ordinalNumber = ordinalNumber;
		}

		@Override
		public Integer getOrdinalNumber() {
			return ordinalNumber;
		}

		@Override
		public void setOrdinalNumber(Integer ordinalNumber) {
			this.ordinalNumber = ordinalNumber;
		}

		@Override
		public List<Ordinal> group() {
			return group;
		}

		@Override
		public Integer initialOrdinalNumber() {
			return 1;
		}
	}

}