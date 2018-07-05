package ru.argustelecom.box.nri.logicalresources.ip.address;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddress;
import ru.argustelecom.system.inf.page.PresentationState;

import javax.inject.Named;
import java.io.Serializable;

/**
 * Состояние страницы IP-адреса
 *
 * @author d.khekk
 * @since 11.12.2017
 */
@PresentationState
@Getter
@Setter
@Named(value = "ipAddressViewState")
public class IPAddressViewState implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * IP-адрес
	 */
	private IPAddress ipAddress;
}
