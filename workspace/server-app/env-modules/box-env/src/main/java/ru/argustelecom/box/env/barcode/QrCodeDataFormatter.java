package ru.argustelecom.box.env.barcode;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.defaultIfEmpty;
import static org.apache.commons.lang.StringUtils.isEmpty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class QrCodeDataFormatter implements BarcodeDataFormatter {

	private static final String DEFAULT_DELIMITER = ",";

	private List<Item> items = new ArrayList<>();

	@Override
	public String getFormattedData() {
		StringBuilder formattedDataBuilder = new StringBuilder();
		items.forEach(item -> formattedDataBuilder.append(item.getFormattedValue()).append(getDelimiter()));
		String formattedData = formattedDataBuilder.toString();
		return formattedData.substring(0, formattedData.length() - 1);
	}

	public void put(String value) {
		if (isEmpty(value))
			return;

		items.add(new Item(value));
	}

	public void put(String keyword, String value, boolean required) {
		if (isEmpty(keyword))
			return;

		items.add(new KeyValueItem(keyword, defaultIfEmpty(value, EMPTY), required));
	}

	public boolean contains(String keyword) {
		Optional<Item> itemOptional = getItems().stream()
				.filter(item -> item instanceof KeyValueItem && ((KeyValueItem) item).getKeyword().equals(keyword))
				.findAny();
		return itemOptional.isPresent();
	}

	public String getDelimiter() {
		return DEFAULT_DELIMITER;
	}

	private static final String REQUIRED_PARAM_WITH_EMPTY_VALUE_MESSAGE_TEMPLATE = "Отсутствует значение для обязательного параметра: '%s'";

	protected void checkItems() {
		items.forEach(item -> {
			if (item instanceof KeyValueItem) {
				KeyValueItem keyValueItem = (KeyValueItem) item;
				if (keyValueItem.isRequired() && isEmpty(keyValueItem.getValue()))
					throw new BarcodeException(
							String.format(REQUIRED_PARAM_WITH_EMPTY_VALUE_MESSAGE_TEMPLATE, keyValueItem.getKeyword()));
			}
		});
	}

	public List<Item> getItems() {
		return Collections.unmodifiableList(items);
	}

	@Getter
	@Setter
	private class KeyValueItem extends Item {

		private String keyword;
		private boolean required;

		private KeyValueItem(String keyword, String value, boolean required) {
			super(value);
			this.keyword = keyword;
			this.required = required;
		}

		@Override
		public String getFormattedValue() {
			return String.format("%s=%s", getKeyword(), getValue());
		}

	}

	@Getter
	@Setter
	@AllArgsConstructor
	private class Item {

		private String value;

		public String getFormattedValue() {
			return value;
		}

	}

}