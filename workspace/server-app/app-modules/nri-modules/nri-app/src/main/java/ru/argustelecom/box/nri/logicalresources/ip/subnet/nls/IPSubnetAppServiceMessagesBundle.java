package ru.argustelecom.box.nri.logicalresources.ip.subnet.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface IPSubnetAppServiceMessagesBundle {

	@Message("Подсеть уже существует")
	String subnetExists();

	@Message("Подсеть не должна входить в 127.0.0.0/8")
	String shouldNotBeLocalhost();

	@Message("Подсеть не должна входить в 0.0.0.0/8")
	String shouldNotBeBroadcast();

	@Message("Адрес создаваемой подсети является конфигурационным")
	String addressIsConfigurational();

	@Message("Адрес создаваемой подсети зарезервирован")
	String addressIsReserved();

	@Message("Адрес создаваемой подсети занят")
	String addressIsOccupied();

	@Message("Адрес создаваемой подсети забронирован")
	String addressIsBooked();

	@Message("Широковещательный адрес создаваемой подсети является конфигурационным")
	String broadcastAddressIsConfigurational();

	@Message("Широковещательный адрес создаваемой подсети зарезервирован")
	String broadcastAddressIsReserved();

	@Message("Широковещательный адрес создаваемой подсети занят")
	String broadcastAddressIsOccupied();

	@Message("Широковещательный адрес создаваемой подсети забронирован")
	String broadcastAddressIsBooked();

	@Message("Удаление подсети запрещено, т.к. входящие в нее IP-адреса используются в бизнес-процессах")
	String deletionIsProhibitedDueUsedIps();
}
