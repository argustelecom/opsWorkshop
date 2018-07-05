package ru.argustelecom.box.nri.logicalresources.ip.address.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.SubnetUtils;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.nri.logicalresources.ip.address.lifecycle.IPAddressState;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.nls.IPAddressEntityListenerMessagesBundle;
import ru.argustelecom.system.inf.exception.BusinessExceptionWithoutRollback;
import ru.argustelecom.system.inf.utils.CheckUtils;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import static ru.argustelecom.system.inf.utils.CheckUtils.checkArgument;

/**
 * Слушатель для сущности IP-адрес
 * Проверяет необходимые параметры и пересчитывает вычисляемые значения
 * Created by s.kolyada on 09.01.2018.
 */
public class IPAddressEntityListener {

	private static final long MinStaticAddressRangeLimit = IPAddress.ipToLong("224.0.0.0");
	private static final long MaxStaticAddressRangeLimit = IPAddress.ipToLong("239.255.255.255");

	@PrePersist
	@PreUpdate
	public void calculateAndVerifyState(IPAddress ipAddress) throws BusinessExceptionWithoutRollback {
		IPAddressEntityListenerMessagesBundle messages = LocaleUtils.getMessages(IPAddressEntityListenerMessagesBundle.class);
		checkArgument(ipAddress != null, messages.addressIsNull());
		checkArgument(StringUtils.isNotBlank(ipAddress.getName()), messages.nameIsNeeded());

		checkArgument(IPAddressState.DELETED.equals(ipAddress.getState()) || ipAddress.getSubnet() != null, messages.subnetworkDoesNotSet());//для удалённого адреса нам без разницы есть родитель или нет
		checkArgument(IPAddressState.DELETED.equals(ipAddress.getState()) || StringUtils.isNotBlank(ipAddress.getSubnet().getName()), messages.subnetworkNameDoesNotSet());

		//Для удалённых оставляем всё как есть
		if (!IPAddressState.DELETED.equals(ipAddress.getState())) {
			// пересчитаем параметры адреса
			ipAddress.calculateState();

			// проверим валидность параметров адреса
			verifyIpAddressState(ipAddress);

			calculateServiceAddresses(ipAddress);
		}
	}

	/**
	 * Валидация параметров
	 * @param ipAddress
	 * @throws BusinessExceptionWithoutRollback
	 */
	private void verifyIpAddressState(IPAddress ipAddress) throws BusinessExceptionWithoutRollback {
		// адреса из диапазона 224.0.0.0 – 239.255.255.255 могут быть только статическими
		if (!ipAddress.getIsStatic()) {
			long ipLongVal = ipAddress.getIpHash();
			if (MinStaticAddressRangeLimit <= ipLongVal
					&& ipLongVal <= MaxStaticAddressRangeLimit) {
				throw new BusinessExceptionWithoutRollback(LocaleUtils.getMessages(IPAddressEntityListenerMessagesBundle.class)
						.addressFromRangeCanBeStatic()){
					@Override
					public String toString() {
						return getLocalizedMessage();
					}
				};
			}
		}
	}

	/**
	 * Вычислить служебные адреса
	 * см. BOX-2194, BOX-2226
	 * @param ip адрес
	 */
	private void calculateServiceAddresses(IPAddress ip) {
		CheckUtils.checkArgument(ip != null,  LocaleUtils.getMessages(IPAddressEntityListenerMessagesBundle.class).addressIsNull());

		SubnetUtils subnet = new SubnetUtils(ip.getSubnet().getName());
		subnet.setInclusiveHostCount(true);
		SubnetUtils.SubnetInfo subnetInfo = subnet.getInfo();

		String lowestAddress = subnetInfo.getLowAddress();
		String highestAddress = subnetInfo.getHighAddress();

		if (lowestAddress.equals(ip.getName())||highestAddress.equals(ip.getName())) {
			ip.setPurpose(IPAddressPurpose.SERVICE);
		} else if (IPAddressPurpose.SERVICE.equals(ip.getPurpose())) {
			ip.setPurpose(IPAddressPurpose.NOT_SPECIFIED);
		}

	}
}
