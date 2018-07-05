package ru.argustelecom.box.nri.logicalresources;

import lombok.Getter;
import ru.argustelecom.box.inf.nls.LocaleUtils;

/**
 * Типы узлов логических ресурсов
 * Created by s.kolyada on 31.10.2017.
 */
@Getter
public enum LogicalResNodeType {
	//@formatter:off
	/**
	 * Корень
	 */
	ROOT("{LogicalResNodeTypeBundle:box.nri.logicalresources.root_title}", "{LogicalResNodeTypeBundle:box.nri.logicalresources.root_keyword}", ""),
	/**
	 * Телефонный номер
	 */
	PHONE_NUMBER("{LogicalResNodeTypeBundle:box.nri.logicalresources.phone_number_title}", "{LogicalResNodeTypeBundle:box.nri.logicalresources.phone_number_keyword}", "fa fa-tty"),
	/**
	 * Запись экземпляра
	 */
	PARTY_SPEC("{LogicalResNodeTypeBundle:box.nri.logicalresources.party_spec_title}", "{LogicalResNodeTypeBundle:box.nri.logicalresources.party_spec_keyword}", "");

	//@formatter:on

	/**
	 * Заголовок
	 */
	private String title;


	/**
	 * Ключевое слово
	 */
	private String keyword;

	/**
	 * Иконка
	 */
	private String icon;

	/**
	 * Конструктор
	 *
	 * @param title   заголовок
	 * @param keyword ключевое слово
	 * @param icon    иконка
	 */
	LogicalResNodeType(String title, String keyword, String icon) {
		this.title = title;
		this.keyword = keyword;
		this.icon = icon;
	}

	public String getKeyword() {
		return LocaleUtils.getLocalizedMessage(keyword, getClass());
	}

	public String getTitle() {
		return LocaleUtils.getLocalizedMessage(title, getClass());
	}
}

