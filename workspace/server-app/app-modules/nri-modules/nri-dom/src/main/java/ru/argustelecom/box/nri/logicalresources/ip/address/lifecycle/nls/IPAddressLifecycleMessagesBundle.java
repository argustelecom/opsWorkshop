	package ru.argustelecom.box.nri.logicalresources.ip.address.lifecycle.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Локализация для ЖЦ IP-адреса
 * Created by s.kolyada on 16.02.2018.
 */
@MessageBundle(projectCode = "")
public interface IPAddressLifecycleMessagesBundle {

	@Message("Занять")
	String occupy();

	@Message("Освободить")
	String unlock();

	@Message("Вывести из обращения")
	String delete();

	/**
	 *
	 * @return
	 */
	@Message("Жизненный цикл IP-адреса")
	String lifecycleName();
}
