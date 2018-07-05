package ru.argustelecom.box.env.billing.bill;

import static org.junit.Assert.assertEquals;
import static ru.argustelecom.box.env.billing.bill.model.DataMapper.marshal;
import static ru.argustelecom.box.env.billing.bill.model.DataMapper.unmarshal;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import ru.argustelecom.box.env.billing.bill.model.IncomesAgg;

public class IncomesAggTest {

	private Long analyticTypeId;
	private String keyword;
	private BigDecimal sum;
	private String incomesAggAsJson;

	@Before
	public void setup() {
		analyticTypeId = 400L;
		keyword = "incomesAgg";
		sum = new BigDecimal(550);
		incomesAggAsJson = "{\"analyticTypeId\":400,\"keyword\":\"incomesAgg\",\"sum\":550,\"error\":null}";
	}

	@Test
	public void shouldCreateIncomesAgg() {
		IncomesAgg incomesAgg = createIncomesAgg();

		checkIncomesAgg(incomesAgg);
	}

	@Test
	public void shouldWriteIncomesAggToJson() {
		IncomesAgg incomesAgg = createIncomesAgg();
		String json = marshal(incomesAgg);

		assertEquals(incomesAggAsJson, json);
	}

	@Test
	public void shouldReadIncomesAggFromJson() {
		IncomesAgg incomesAgg = unmarshal(incomesAggAsJson, IncomesAgg.class);

		checkIncomesAgg(incomesAgg);
	}

	@Test
	public void shouldCalcMathSum() {
		IncomesAgg incomesAgg = createIncomesAgg();

		assertEquals(sum, incomesAgg.getMathSum());
	}

	private IncomesAgg createIncomesAgg() {
		return IncomesAgg.builder().analyticTypeId(analyticTypeId).keyword(keyword).sum(sum).build();
	}

	private void checkIncomesAgg(IncomesAgg incomesAgg) {
		assertEquals(analyticTypeId, incomesAgg.getAnalyticTypeId());
		assertEquals(keyword, incomesAgg.getKeyword());
		assertEquals(sum, incomesAgg.getSum());
	}

}
