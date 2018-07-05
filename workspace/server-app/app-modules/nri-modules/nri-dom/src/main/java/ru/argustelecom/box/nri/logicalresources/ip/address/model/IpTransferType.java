package ru.argustelecom.box.nri.logicalresources.ip.address.model;

import lombok.Getter;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Метод передачи данных IP-адреса
 *
 * @author d.khekk
 * @since 11.12.2017
 */
public enum IpTransferType {
	UNICAST("Unicast"),
	MULTICAST("Multicast"),
	BROADCAST("Broadcast");

	/**
	 * Имя метода передачи данных
	 */
	@Getter
	private String name;

	/**
	 * Конструктор
	 *
	 * @param name имя метода передачи данных
	 */
	IpTransferType(String name) {
		this.name = name;
	}

	/**
	 * Получить список значений в листе
	 *
	 * @return лист значений
	 */
	public static List<IpTransferType> listOfValues() {
		return asList(values());
	}
}
