package ru.argustelecom.box.env.barcode;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static ru.argustelecom.box.env.barcode.ST00012QrCodeDataFormatter.ST00012QrCodeItem.requiredItems;

import java.util.List;

import lombok.Getter;

public class ST00012QrCodeDataFormatter extends QrCodeDataFormatter {

	private static final String MISSING_REQUIRED_PARAM_MESSAGE_TEMPLATE = "Отсутствует обязательный параметр: '%s'";
	private static final String PREF = "ST00012";
	private static final String DELIMITER = "|";

	@Override
	public String getFormattedData() {
		checkItems();
		return String.format("%s|%s", PREF, super.getFormattedData());
	}

	@Override
	public String getDelimiter() {
		return DELIMITER;
	}

	@Override
	protected void checkItems() {
		super.checkItems();
		for (ST00012QrCodeItem item : requiredItems()) {
			if (!contains(item.getKeyword()))
				throw new BarcodeException(String.format(MISSING_REQUIRED_PARAM_MESSAGE_TEMPLATE, item.getKeyword()));
		}
	}

	public enum ST00012QrCodeItem {

		//@formatter:off
		NAME 			("Name", "Наименование получателя платежа", true, true),
		PERSONAL_ACC	("PersonalAcc", "Номер счета получателя платежа", true, true),
		PERS_ACC		("PersAcc", "Номер лицевого счёта клиента", false, false),
		BANK_NAME 		("BankName", "Название банка получателя платежа", true, true),
		BIC 			("BIC", "БИК", true, true),
		CORRESP_ACC 	("CorrespAcc", "Номер кор./сч. банка получателя платежа", true, true),
		SUM 			("Sum", "Сумма платежа, в копейках", false, false),
		PURPOSE 		("Purpose", "Название платежа (назначение)", false, false),
		PAYEE_INN		("PayeeINN", "ИНН получателя платежа", false, true),
		LAST_NAME		("LastName", "Фамилия плательщика", false, false),
		FIRST_NAME		("FirstName", "Имя плательщика", false, false),
		MIDDLE_NAME		("MiddleName", "Отчество плательщика", false, false),
		PAYM_PERIOD		("PaymPeriod", "Период оплаты", false, false),
		CATEGORY		("Category", "Вид платежа", false, true),
		PAYER_ADDRESS	("PayerAddress", "Адрес плательщика", false, false);
		//@formatter:on

		@Getter
		private String keyword;
		@Getter
		private String name;
		@Getter
		private boolean required;
		@Getter
		private boolean ownerCharacteristic;

		public static List<ST00012QrCodeItem> requiredItems() {
			return stream(values()).filter(ST00012QrCodeItem::isRequired).collect(toList());
		}

		public static ST00012QrCodeItem findByKeyword(String keyword) {
			return stream(values()).filter(item -> item.getKeyword().equals(keyword)).findFirst().orElse(null);
		}

		public static List<ST00012QrCodeItem> ownerCharacteristicItems() {
			return stream(values()).filter(ST00012QrCodeItem::isOwnerCharacteristic).collect(toList());
		}

		ST00012QrCodeItem(String keyword, String name, boolean required, boolean ownerCharacteristic) {
			this.keyword = keyword;
			this.name = name;
			this.required = required;
			this.ownerCharacteristic = ownerCharacteristic;
		}

	}
}