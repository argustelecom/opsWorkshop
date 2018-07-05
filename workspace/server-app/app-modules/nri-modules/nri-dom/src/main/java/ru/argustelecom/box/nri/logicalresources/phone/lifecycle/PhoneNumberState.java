package ru.argustelecom.box.nri.logicalresources.phone.lifecycle;

import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.inf.nls.LocaleUtils;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Статусы телефонного номера
 * Created by s.kolyada on 27.10.2017.
 */
public enum PhoneNumberState implements LifecycleState<PhoneNumberState> {

	//@formatter:off
	AVAILABLE("{PhoneNumberStateBundle:ru.argustelecom.box.nri.logicalresources.phone.free}", "PhoneNumberFree"),
	OCCUPIED("{PhoneNumberStateBundle:ru.argustelecom.box.nri.logicalresources.phone.occupied}", "PhoneNumberOccupied"),
	LOCKED("{PhoneNumberStateBundle:ru.argustelecom.box.nri.logicalresources.phone.locked}", "PhoneNumberLocked"),
	DELETED("{PhoneNumberStateBundle:ru.argustelecom.box.nri.logicalresources.phone.deleted}", "PhoneNumberDeleted");
	//@formatter:on

	private String name;
	private String eventQualifier;

	PhoneNumberState(String name, String eventQualifier) {
		this.name = name;
		this.eventQualifier = eventQualifier;
	}

	@Override
	public Iterable<PhoneNumberState> getStates() {
		return Arrays.asList(values());
	}

	@Override
	public Serializable getKey() {
		return this;
	}

	@Override
	public String getName() {
		return LocaleUtils.getLocalizedMessage(name, getClass());
	}

	@Override
	public String getEventQualifier() {
		return eventQualifier;
	}

	/**
	 * Значение по умолчанию
	 *
	 * @return Значение по умолчанию
	 */
	public static PhoneNumberState defaultStatus() {
		return PhoneNumberState.AVAILABLE;
	}
}
