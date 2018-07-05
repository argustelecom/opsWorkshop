package ru.argustelecom.box.nri.logicalresources.ip.address.lifecycle;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.inf.nls.LocaleUtils;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Статусы IP-адреса
 *
 * @author d.khekk
 * @since 08.12.2017
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum IPAddressState implements LifecycleState<IPAddressState> {

	//@formatter:off
	AVAILABLE("{LogicalResourceStateBundle:box.nri.logical.state.available}", "IPAddressAvailable"),
	OCCUPIED("{LogicalResourceStateBundle:box.nri.logical.state.occupied}", "IPAddressOccupied"),
	DELETED("{LogicalResourceStateBundle:box.nri.logical.state.out_of_processing}", "IPAddressDeleted");
	//@formatter:on

	private String name;

	@Getter
	private String eventQualifier;

	/**
	 * Значение по умолчанию
	 *
	 * @return Значение по умолчанию
	 */
	public static IPAddressState defaultStatus() {
		return AVAILABLE;
	}

	@Override
	public Iterable<IPAddressState> getStates() {
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
}
