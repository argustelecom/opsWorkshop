package ru.argustelecom.box.nri.logicalresources.ip.address.lifecycle;

import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleAction;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleBuilder;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleFactory;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleRegistrant;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.nri.logicalresources.ip.address.lifecycle.nls.IPAddressLifecycleMessagesBundle;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddress;
import ru.argustelecom.system.inf.exception.SystemException;

import java.util.Date;

import static ru.argustelecom.box.nri.logicalresources.ip.address.lifecycle.IPAddressLifecycle.Routes.DELETE;
import static ru.argustelecom.box.nri.logicalresources.ip.address.lifecycle.IPAddressLifecycle.Routes.OCCUPY;
import static ru.argustelecom.box.nri.logicalresources.ip.address.lifecycle.IPAddressLifecycle.Routes.UNLOCK;
import static ru.argustelecom.box.nri.logicalresources.ip.address.lifecycle.IPAddressState.AVAILABLE;
import static ru.argustelecom.box.nri.logicalresources.ip.address.lifecycle.IPAddressState.DELETED;
import static ru.argustelecom.box.nri.logicalresources.ip.address.lifecycle.IPAddressState.OCCUPIED;

/**
 * Жизненный цикл IP-адреса
 *
 * @author d.khekk
 * @since 08.12.2017
 */
@LifecycleRegistrant
public class IPAddressLifecycle implements LifecycleFactory<IPAddressState, IPAddress> {

	/**
	 * Обновление даты смены статуса
	 */
	private LifecycleAction<IPAddressState, IPAddress> updateStateChangedDateAction =
			ctx -> ctx.getBusinessObject().setStateChangeDate(new Date());

	@Override
	public void buildLifecycle(LifecycleBuilder<IPAddressState, IPAddress> lifecycle) {
		lifecycle.keyword("IPAddressLifecycle");
		lifecycle.name(LocaleUtils.getMessages(IPAddressLifecycleMessagesBundle.class).lifecycleName());

//		@formatter:off
		lifecycle.route(UNLOCK, UNLOCK.getName())
				.from(OCCUPIED)
				.to(AVAILABLE)
				.execute(updateStateChangedDateAction)
				.end()
			.end();

		lifecycle.route(OCCUPY, OCCUPY.getName())
				.from(AVAILABLE)
				.to(OCCUPIED)
				.execute(updateStateChangedDateAction)
				.end()
			.end();

		lifecycle.route(DELETE, DELETE.getName())
				.from(AVAILABLE)
				.to(DELETED)
				.execute(updateStateChangedDateAction)
				.end()
			.end();
//		@formatter:on
	}

	/**
	 * Пути в графе ЖЦ
	 */
	public enum Routes {
		OCCUPY, UNLOCK, DELETE;

		public String getName() {
			IPAddressLifecycleMessagesBundle messages = LocaleUtils.getMessages(IPAddressLifecycleMessagesBundle.class);

			switch (this) {
				case OCCUPY:
					return messages.occupy();
				case UNLOCK:
					return messages.unlock();
				case DELETE:
					return messages.delete();
				default:
					throw new SystemException("Unsupported OrderLifecycle.Routes");
			}
		}
	}
}
