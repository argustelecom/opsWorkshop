package ru.argustelecom.box.env.stl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Предназначен для описания типа номера телефона. Немного отличается от нижележащего гугловского отсутствием откровенно
 * ненужных типов, таких как пейджер или shared cost. По сути, инкапсулирует в себе нижележащий тип номера телефона и
 * скрывает его от внешнего мира, предоставляя более простую, но достаточную, абстракцию.
 */
public enum PhoneNumberType {

	/**
	 * Фиксированная линия, медные сети, стандартный проводной телефонный номер
	 */
	FIXED_LINE,

	/**
	 * Мобильные номера телефонов, в России начинаются с кода 900 и так далее
	 */
	MOBILE,

	/**
	 * В некоторых странах нет возможности определить по коду, относится ли данный телефонный номер к фиксированной
	 * линии или к мобильным номерам. В этом случае тип может быть либо фиксированным, либо мобильным без уточнения
	 */
	FIXED_LINE_OR_MOBILE,

	/**
	 * Номера телефонов горячих линий и прочих консультационных центров, звонки на которые бесплатны. В России чаще
	 * всего имеют код 800
	 */
	TOLL_FREE,

	/**
	 * Хз что это, но в России вроде бы тоже есть и начинается с кода 809
	 */
	PREMIUM_RATE,

	/**
	 * VOIP номера, примеров не нашел, но использовать собираемся, поэтому надеюсь на чудо
	 */
	VOIP,

	/**
	 * Если не смогли определить тип телефона или если он нам не интересен (например, пейджер, да кому он сейчас нужен),
	 * то возвращается этот тип номера телефона.
	 */
	UNKNOWN;

	/**
	 * Позволяет определить тип номера телефона по типу делегата. Не предназначен для внешнего использования, т.к. типы
	 * делегатов не доступны нигде, кроме box-env
	 *
	 * @param delegateType
	 * @return
	 */
	public static PhoneNumberType identify(Object delegateType) {
		PhoneNumberType result = null;

		if (delegateType instanceof com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType) {
			result = NUMBER_TYPES.get(delegateType);
		}

		return result != null ? result : UNKNOWN;
	}

	private static final Map<com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType, PhoneNumberType> NUMBER_TYPES;
	static {
		//@formatter:off
		Map<com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType, PhoneNumberType> numberTypes = new HashMap<>();
		numberTypes.put(com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType.FIXED_LINE,           FIXED_LINE);
		numberTypes.put(com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType.MOBILE,               MOBILE);
		numberTypes.put(com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType.FIXED_LINE_OR_MOBILE, FIXED_LINE_OR_MOBILE);
		numberTypes.put(com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType.TOLL_FREE,            TOLL_FREE);
		numberTypes.put(com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType.PREMIUM_RATE,         PREMIUM_RATE);
		numberTypes.put(com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType.VOIP,                 VOIP);
		//@formatter:on

		NUMBER_TYPES = Collections.unmodifiableMap(numberTypes);
	}

}
