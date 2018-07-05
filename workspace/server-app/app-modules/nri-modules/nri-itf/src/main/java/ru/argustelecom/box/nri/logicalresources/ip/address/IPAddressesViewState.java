package ru.argustelecom.box.nri.logicalresources.ip.address;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.filter.FilterMapEntry;
import ru.argustelecom.box.env.filter.FilterViewState;
import ru.argustelecom.box.nri.logicalresources.ip.address.lifecycle.IPAddressState;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddressPurpose;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IpTransferType;
import ru.argustelecom.box.nri.logicalresources.ip.subnet.model.IPSubnet;
import ru.argustelecom.system.inf.page.PresentationState;

import javax.inject.Named;

import static ru.argustelecom.box.nri.logicalresources.ip.address.IPAddressesViewState.IPAddressFilter.IS_PRIVATE;
import static ru.argustelecom.box.nri.logicalresources.ip.address.IPAddressesViewState.IPAddressFilter.IS_STATIC;
import static ru.argustelecom.box.nri.logicalresources.ip.address.IPAddressesViewState.IPAddressFilter.NAME;
import static ru.argustelecom.box.nri.logicalresources.ip.address.IPAddressesViewState.IPAddressFilter.PURPOSE;
import static ru.argustelecom.box.nri.logicalresources.ip.address.IPAddressesViewState.IPAddressFilter.STATE;
import static ru.argustelecom.box.nri.logicalresources.ip.address.IPAddressesViewState.IPAddressFilter.SUBNET;
import static ru.argustelecom.box.nri.logicalresources.ip.address.IPAddressesViewState.IPAddressFilter.TRANSFER_TYPE;

/**
 * Сосояние страницы поиска IP-адресов
 *
 * @author d.khekk
 * @since 11.12.2017
 */
@Named("ipAddressesViewState")
@PresentationState
@Getter
@Setter
public class IPAddressesViewState extends FilterViewState {

	private static final long serialVersionUID = 1L;

	@FilterMapEntry(NAME)
	private String name;

	@FilterMapEntry(STATE)
	private IPAddressState state;

	@FilterMapEntry(IS_PRIVATE)
	private Boolean isPrivate;

	@FilterMapEntry(IS_STATIC)
	private Boolean isStatic;

	@FilterMapEntry(TRANSFER_TYPE)
	private IpTransferType transferType;

	@FilterMapEntry(SUBNET)
	private IPSubnet subnet;

	@FilterMapEntry(PURPOSE)
	private IPAddressPurpose purpose;

	class IPAddressFilter {
		static final String NAME = "NAME";
		static final String STATE = "STATE";
		static final String IS_PRIVATE = "IS_PRIVATE";
		static final String IS_STATIC = "IS_STATIC";
		static final String TRANSFER_TYPE = "TRANSFER_TYPE";
		static final String SUBNET = "SUBNET";
		static final String PURPOSE = "PURPOSE";

		/**
		 * Приватный конструктор
		 */
		private IPAddressFilter() {
		}
	}
}
