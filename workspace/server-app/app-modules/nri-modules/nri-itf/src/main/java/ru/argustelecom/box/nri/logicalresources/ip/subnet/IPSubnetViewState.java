package ru.argustelecom.box.nri.logicalresources.ip.subnet;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.nri.logicalresources.ip.subnet.model.IPSubnet;
import ru.argustelecom.system.inf.page.PresentationState;

import javax.inject.Named;
import java.io.Serializable;

/**
 * Состояние страницы IP-подсетей
 *
 * @author d.khekk
 * @since 13.12.2017
 */
@PresentationState
@Getter
@Setter
@Named(value = "ipSubnetViewState")
public class IPSubnetViewState implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * IP-подсеть
	 */
	private IPSubnet ipSubnet;
}
