package ru.argustelecom.ops.env.stl;

/**
 * Представляет вхождение одного номера телефона в некоторый произвольный текст
 *
 * @see PhoneNumber#extractAll(CharSequence, String)
 */
public class PhoneNumberEntry {

	private PhoneNumber number;
	private String rawNumber;
	private int start;
	private int end;

	protected PhoneNumberEntry(PhoneNumber number, String rawNumber, int start, int end) {
		this.number = number;
		this.rawNumber = rawNumber;
		this.start = start;
		this.end = end;
	}

	/**
	 * Возвращает нормализованный номер телефона, который удалось извлечь из произвольного текста
	 *
	 * @return
	 */
	public PhoneNumber number() {
		return number;
	}

	/**
	 * Возвращает сырую строку номера телефона, извлеченного из текста. Строка представлена в том виде, в котором она
	 * указана в тексте
	 *
	 * @return
	 */
	public String rawNumber() {
		return rawNumber;
	}

	/**
	 * Возвращает стартовую позицию найденного номера телефона в исходном тексте
	 *
	 * @return
	 */
	public int start() {
		return start;
	}

	/**
	 * Возвращает конечную позицию найденного номера телефона в исходном тексте
	 *
	 * @return
	 */
	public int end() {
		return end;
	}

}
