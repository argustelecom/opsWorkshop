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

import ru.argustelecom.box.env.billing.bill.model.ChargesRaw;
import ru.argustelecom.box.env.billing.bill.model.ChargesRawByNonRecurrent;
import ru.argustelecom.box.env.billing.bill.model.ChargesRawByRecurrent;
import ru.argustelecom.box.env.billing.bill.model.ChargesRawByUsage;
import ru.argustelecom.system.inf.chrono.DateUtils;

public class ChargesRawTest {

	private Long analyticTypeId;
	private Long productId;
	private Long subscriptionId;
	private BigDecimal taxRate;
	private BigDecimal sum;
	private BigDecimal discountSum;
	private Date startDate;
	private Date endDate;
	private boolean row;
	private Long optionId;
	private Long serviceId;
	private Long providerId;
	private boolean withoutContract;
	private String chargesRawByRecurrentAsJson;
	private String chargesRawByNonRecurrentAsJson;
	private String chargesRawByUsageAsJson;

	@Before
	public void setup() throws ParseException {
		analyticTypeId = 100L;
		productId = 1L;
		subscriptionId = 2L;
		taxRate = new BigDecimal(0.13);
		sum = new BigDecimal(200);
		discountSum = new BigDecimal(10);
		startDate = new SimpleDateFormat(DateUtils.DATE_DEFAULT_PATTERN).parse("01.10.2017");
		endDate = new SimpleDateFormat(DateUtils.DATE_DEFAULT_PATTERN).parse("30.10.2017");
		row = true;
		optionId = 3L;
		serviceId = 4L;
		providerId = 5L;
		withoutContract = false;
		chargesRawByRecurrentAsJson = "{\"analyticTypeId\":100,\"error\":null,\"subjectId\":1,\"chargesType\":\"RECURRENT\","
				+ "\"taxRate\":0.13000000000000000444089209850062616169452667236328125,\"startDate\":1506805200000,"
				+ "\"endDate\":1509310800000,\"sum\":200,\"discountSum\":10,\"row\":true,\"subscriptionId\":2}";
		chargesRawByNonRecurrentAsJson = "{\"analyticTypeId\":100,\"error\":null,\"subjectId\":1,\"chargesType\":\"NONRECURRENT\","
				+ "\"taxRate\":0.13000000000000000444089209850062616169452667236328125,\"startDate\":1506805200000,"
				+ "\"endDate\":1509310800000,\"sum\":200,\"discountSum\":null,\"row\":true}";
		chargesRawByUsageAsJson = "{\"analyticTypeId\":100,\"error\":null,\"subjectId\":3,\"chargesType\":\"USAGE\","
				+ "\"taxRate\":0.13000000000000000444089209850062616169452667236328125,\"startDate\":1506805200000,"
				+ "\"endDate\":1509310800000,\"sum\":200,\"discountSum\":null,\"row\":true,\"serviceId\":4,\"providerId\":5,"
				+ "\"withoutContract\":false}";
	}

	@Test
	public void shouldCreateChargesRawByRecurrent() {
		ChargesRawByRecurrent chargesRaw = createChargesRawByRecurrent();

		checkChargesRawByRecurrent(chargesRaw);
	}

	@Test
	public void shouldWriteChargesRawByRecurrentToJson() {
		ChargesRaw chargesRaw = createChargesRawByRecurrent();
		String json = marshal(chargesRaw);

		assertEquals(chargesRawByRecurrentAsJson, json);
	}

	@Test
	public void shouldReadChargesRawByRecurrentFromJson() {
		ChargesRawByRecurrent chargesRaw = unmarshal(chargesRawByRecurrentAsJson, ChargesRawByRecurrent.class);

		checkChargesRawByRecurrent(chargesRaw);
	}

	@Test
	public void shouldCreateChargesRawByNonRecurrent() {
		ChargesRawByNonRecurrent chargesRaw = createChargesRawByNonRecurrent();

		checkChargesRawByNonRecurrent(chargesRaw);
	}

	@Test
	public void shouldWriteChargesRawByNonRecurrentToJson() {
		ChargesRaw chargesRaw = createChargesRawByNonRecurrent();
		String json = marshal(chargesRaw);

		assertEquals(chargesRawByNonRecurrentAsJson, json);
	}

	@Test
	public void shouldReadChargesRawByNonRecurrentFromJson() {
		ChargesRawByNonRecurrent chargesRaw = unmarshal(chargesRawByNonRecurrentAsJson, ChargesRawByNonRecurrent.class);

		checkChargesRawByNonRecurrent(chargesRaw);
	}

	@Test
	public void shouldCreateChargesRawByUsage() {
		ChargesRawByUsage chargesRaw = createChargesRawByUsage();

		checkChargesRawByUsage(chargesRaw);
	}

	@Test
	public void shouldWriteChargesRawByUsageToJson() {
		ChargesRaw chargesRaw = createChargesRawByUsage();
		String json = marshal(chargesRaw);

		assertEquals(chargesRawByUsageAsJson, json);
	}

	@Test
	public void shouldReadChargesRawByUsageFromJson() {
		ChargesRawByUsage chargesRaw = unmarshal(chargesRawByUsageAsJson, ChargesRawByUsage.class);

		checkChargesRawByUsage(chargesRaw);
	}

	private ChargesRawByRecurrent createChargesRawByRecurrent() {
		return ChargesRawByRecurrent.builder().analyticTypeId(analyticTypeId).productId(productId)
				.subscriptionId(subscriptionId).taxRate(taxRate).sum(sum).discountSum(discountSum).startDate(startDate)
				.endDate(endDate).row(row).build();
	}

	private ChargesRawByNonRecurrent createChargesRawByNonRecurrent() {
		return ChargesRawByNonRecurrent.builder().analyticTypeId(analyticTypeId).productId(productId).taxRate(taxRate)
				.sum(sum).startDate(startDate).endDate(endDate).row(row).build();
	}

	private ChargesRawByUsage createChargesRawByUsage() {
		return ChargesRawByUsage.builder().analyticTypeId(analyticTypeId).optionId(optionId).serviceId(serviceId)
				.providerId(providerId).taxRate(taxRate).sum(sum).startDate(startDate).endDate(endDate).row(row)
				.withoutContract(withoutContract).build();
	}

	private void checkChargesRawByRecurrent(ChargesRawByRecurrent chargesRaw) {
		assertEquals(analyticTypeId, chargesRaw.getAnalyticTypeId());
		assertEquals(productId, chargesRaw.getSubjectId());
		assertEquals(subscriptionId, chargesRaw.getSubscriptionId());
		assertEquals(taxRate, chargesRaw.getTaxRate());
		assertEquals(sum, chargesRaw.getSum());
		assertEquals(discountSum, chargesRaw.getDiscountSum());
		assertEquals(startDate, chargesRaw.getStartDate());
		assertEquals(endDate, chargesRaw.getEndDate());
		assertEquals(row, chargesRaw.isRow());
	}

	private void checkChargesRawByNonRecurrent(ChargesRawByNonRecurrent chargesRaw) {
		assertEquals(analyticTypeId, chargesRaw.getAnalyticTypeId());
		assertEquals(productId, chargesRaw.getSubjectId());
		assertEquals(taxRate, chargesRaw.getTaxRate());
		assertEquals(sum, chargesRaw.getSum());
		assertEquals(null, chargesRaw.getDiscountSum());
		assertEquals(startDate, chargesRaw.getStartDate());
		assertEquals(endDate, chargesRaw.getEndDate());
		assertEquals(row, chargesRaw.isRow());
	}

	private void checkChargesRawByUsage(ChargesRawByUsage chargesRaw) {
		assertEquals(analyticTypeId, chargesRaw.getAnalyticTypeId());
		assertEquals(optionId, chargesRaw.getSubjectId());
		assertEquals(serviceId, chargesRaw.getServiceId());
		assertEquals(providerId, chargesRaw.getProviderId());
		assertEquals(taxRate, chargesRaw.getTaxRate());
		assertEquals(sum, chargesRaw.getSum());
		assertEquals(null, chargesRaw.getDiscountSum());
		assertEquals(startDate, chargesRaw.getStartDate());
		assertEquals(endDate, chargesRaw.getEndDate());
		assertEquals(row, chargesRaw.isRow());
		assertEquals(withoutContract, chargesRaw.isWithoutContract());
	}
}
