package ru.argustelecom.box.env.billing.bill;

import static org.junit.Assert.assertEquals;
import static ru.argustelecom.box.env.billing.bill.model.DataMapper.marshal;
import static ru.argustelecom.box.env.billing.bill.model.DataMapper.unmarshal;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import ru.argustelecom.box.env.billing.bill.model.Summary;

public class SummaryTest {

	private Long analyticTypeId;
	private String keyword;
	private BigDecimal sum;
	private Boolean invertible;
	private String summaryAsJson;

	@Before
	public void setup() {
		analyticTypeId = 500L;
		keyword = "summary";
		sum = new BigDecimal(7000);
		invertible = true;
		summaryAsJson = "{\"analyticTypeId\":500,\"keyword\":\"summary\",\"sum\":7000,\"error\":null,\"invertible\":true}";
	}

	@Test
	public void shouldCreateSummary() {
		Summary summary = createSummary();

		checkSummary(summary);
	}

	@Test
	public void shouldWriteSummaryToJson() {
		Summary summary = createSummary();
		String json = marshal(summary);

		assertEquals(summaryAsJson, json);
	}

	@Test
	public void shouldReadSummaryFromJson() {
		Summary summary = unmarshal(summaryAsJson, Summary.class);

		checkSummary(summary);
	}

	@Test
	public void shouldCalcMathSum() {
		Summary summary = createSummary();

		assertEquals(sum, summary.getMathSum());
	}

	private Summary createSummary() {
		return Summary.builder().analyticTypeId(analyticTypeId).keyword(keyword).sum(sum).invertible(invertible)
				.build();
	}

	private void checkSummary(Summary summary) {
		assertEquals(analyticTypeId, summary.getAnalyticTypeId());
		assertEquals(keyword, summary.getKeyword());
		assertEquals(sum, summary.getSum());
	}

}
