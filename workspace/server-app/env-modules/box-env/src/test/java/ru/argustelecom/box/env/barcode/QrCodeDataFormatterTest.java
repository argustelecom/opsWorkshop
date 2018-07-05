package ru.argustelecom.box.env.barcode;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class QrCodeDataFormatterTest {

	private QrCodeDataFormatter formatter;

	@Before
	public void init() {
		formatter = new QrCodeDataFormatter();
	}

	@Test
	public void getFormattedData() throws Exception {
		formatter.put("1");
		formatter.put("second", "2", true);
		formatter.put("third", "3", true);
		formatter.put("");
		formatter.put("fifth");
		formatter.put("sixth", null, false);

		String resultString = "1,second=2,third=3,fifth,sixth=";
		assertEquals("Incorrect data format", resultString, formatter.getFormattedData());
	}

	@Test
	public void putItem() throws Exception {
		formatter.put("first");
		assertEquals("Item with value didn't add", 1, formatter.getItems().size());

		formatter.put("");
		assertEquals("Item with empty value added", 1, formatter.getItems().size());

		formatter.put(null);
		assertEquals("Item without value added", 1, formatter.getItems().size());
	}

	@Test
	public void putKeyValueItem() throws Exception {
		formatter.put("first", "1", true);
		assertEquals("Required item didn't add", 1, formatter.getItems().size());

		formatter.put("", "2", true);
		assertEquals("Item with empty key added", 1, formatter.getItems().size());

		formatter.put(null, "3", true);
		assertEquals("Item without key added", 1, formatter.getItems().size());

		formatter.put("fourth", "", false);
		assertEquals("Not required item with empty value didn't add", 2, formatter.getItems().size());

		formatter.put("fourth", null, false);
		assertEquals("Not required item without value didn't add", 3, formatter.getItems().size());

		formatter.put("fifth", null, true);
		assertEquals("Required item without value didn't add", 4, formatter.getItems().size());

		formatter.put("sixth", "6", false);
		assertEquals("Not required item didn't add", 5, formatter.getItems().size());
	}

}