package ru.argustelecom.box.env.billing.bill;

import static org.junit.Assert.assertEquals;
import static ru.argustelecom.box.env.billing.bill.model.DataMapper.marshal;
import static ru.argustelecom.box.env.billing.bill.model.DataMapper.unmarshal;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import ru.argustelecom.box.env.billing.bill.model.IncomesRaw;
import ru.argustelecom.system.inf.chrono.DateUtils;

public class IncomesRawTest {

	private Long analyticTypeId;
	private Long personalAccountId;
	private Long transactionId;
	private BigDecimal sum;
	private Date date;
	private String incomesRawAsJson;

	@Before
	public void setup() throws ParseException {
		analyticTypeId = 200L;
		personalAccountId = 3L;
		transactionId = 4L;
		sum = new BigDecimal(1000);
		date = new SimpleDateFormat(DateUtils.DATE_DEFAULT_PATTERN).parse("13.10.2017");
		incomesRawAsJson = "{\"analyticTypeId\":200,\"error\":null,\"personalAccountId\":3,\"transactionId\":4,\"date\":1507842000000,\"sum\":1000}";
	}

	@Test
	public void shouldCreateIncomesRaw() {
		IncomesRaw incomesRaw = createIncomesRaw();

		checkIncomesRaw(incomesRaw);
	}

	@Test
	public void shouldWriteIncomesRawToJson() {
		IncomesRaw incomesRaw = createIncomesRaw();
		String json = marshal(incomesRaw);

		assertEquals(incomesRawAsJson, json);
	}

	@Test
	public void shouldReadChargesRawFromJson() {
		IncomesRaw incomesRaw = unmarshal(incomesRawAsJson, IncomesRaw.class);

		checkIncomesRaw(incomesRaw);
	}

	private IncomesRaw createIncomesRaw() {
		return IncomesRaw.builder().analyticTypeId(analyticTypeId).personalAccountId(personalAccountId)
				.transactionId(transactionId).sum(sum).date(date).build();
	}

	private void checkIncomesRaw(IncomesRaw incomesRaw) {
		assertEquals(analyticTypeId, incomesRaw.getAnalyticTypeId());
		assertEquals(personalAccountId, incomesRaw.getPersonalAccountId());
		assertEquals(transactionId, incomesRaw.getTransactionId());
		assertEquals(sum, incomesRaw.getSum());
		assertEquals(date, incomesRaw.getDate());
	}
}
