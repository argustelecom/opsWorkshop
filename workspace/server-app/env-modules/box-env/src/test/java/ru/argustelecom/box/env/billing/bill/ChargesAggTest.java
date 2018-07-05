package ru.argustelecom.box.env.billing.bill;

import static org.junit.Assert.assertEquals;
import static ru.argustelecom.box.env.billing.bill.model.DataMapper.marshal;
import static ru.argustelecom.box.env.billing.bill.model.DataMapper.unmarshal;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import ru.argustelecom.box.env.billing.bill.model.ChargesAgg;

public class ChargesAggTest {

	private Long analyticTypeId;
	private String keyword;
	private Long subjectId;
	private BigDecimal sum;
	private BigDecimal discountSum;
	private BigDecimal tax;
	private BigDecimal sumWithoutTax;
	private boolean row;
	private boolean periodic;
	private String chargesAggAsJson;

	@Before
	public void setup() {
		analyticTypeId = 300L;
		keyword = "chargesAgg";
		subjectId = 14L;
		sum = new BigDecimal(300);
		tax = new BigDecimal(14.6);
		sumWithoutTax = new BigDecimal(285.4);
		discountSum = BigDecimal.ZERO;
		row = false;
		periodic = true;
		chargesAggAsJson = "{\"analyticTypeId\":300,\"keyword\":\"chargesAgg\",\"sum\":300,\"error\":null,\"subjectId\":14,"
				+ "\"sumWithoutTax\":285.3999999999999772626324556767940521240234375,"
				+ "\"tax\":14.5999999999999996447286321199499070644378662109375,\"discountSum\":0,\"row\":false,\"periodic\":true}";
	}

	@Test
	public void shouldCreateChargesAgg() {
		ChargesAgg chargesAgg = createChargesAgg();

		checkChargesAgg(chargesAgg);
	}

	@Test
	public void shouldWriteChargesAggToJson() {
		ChargesAgg chargesAgg = createChargesAgg();
		String json = marshal(chargesAgg);

		assertEquals(chargesAggAsJson, json);
	}

	@Test
	public void shouldReadChargesRowFromJson() {
		ChargesAgg chargesAgg = unmarshal(chargesAggAsJson, ChargesAgg.class);

		checkChargesAgg(chargesAgg);
	}

	@Test
	public void shouldCalcMathSum() {
		ChargesAgg chargesAgg = createChargesAgg();

		assertEquals(sum.negate(), chargesAgg.getMathSum());
	}

	private ChargesAgg createChargesAgg() {
		return ChargesAgg.builder().analyticTypeId(analyticTypeId).keyword(keyword).subjectId(subjectId).sum(sum)
				.tax(tax).sumWithoutTax(sumWithoutTax).discountSum(discountSum).row(row).periodic(periodic).build();
	}

	private void checkChargesAgg(ChargesAgg chargesAgg) {
		assertEquals(analyticTypeId, chargesAgg.getAnalyticTypeId());
		assertEquals(keyword, chargesAgg.getKeyword());
		assertEquals(subjectId, chargesAgg.getSubjectId());
		assertEquals(sum, chargesAgg.getSum());
		assertEquals(discountSum, chargesAgg.getDiscountSum());
		assertEquals(tax, chargesAgg.getTax());
		assertEquals(sumWithoutTax, chargesAgg.getSumWithoutTax());
		assertEquals(row, chargesAgg.isRow());
		assertEquals(periodic, chargesAgg.isPeriodic());
	}

}
