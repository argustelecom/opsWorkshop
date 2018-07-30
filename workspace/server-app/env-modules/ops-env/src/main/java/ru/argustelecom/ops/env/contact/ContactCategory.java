package ru.argustelecom.ops.env.contact;

import static ru.argustelecom.ops.inf.nls.LocaleUtils.format;

import java.lang.reflect.ParameterizedType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.ops.env.contact.nls.ContactMessagesBundle;
import ru.argustelecom.ops.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

/**
 * Категории контанктов.
 */
@AllArgsConstructor(access = AccessLevel.MODULE)
public enum ContactCategory {

	//@formatter:off
	EMAIL  ("mailto:", "icon-email", EmailContact.class),
	PHONE  ("callto:", "fa fa-phone", PhoneContact.class),
	SKYPE  ("skype:", "fa fa-skype", SkypeContact.class),
	CUSTOM ("", "fa fa-male", CustomContact.class);
	//@formatter:on

	@Getter
	private String prefix;
	@Getter
	private String icon;
	@Getter
	private Class<? extends Contact<?>> contactClass;

	public String getName() {

		ContactMessagesBundle messages = LocaleUtils.getMessages(ContactMessagesBundle.class);
		switch (this) {
		case EMAIL:
			return messages.contactTypeEmail();
		case PHONE:
			return messages.contactTypePhone();
		case SKYPE:
			return messages.contactTypeSkype();
		case CUSTOM:
			return messages.contactTypeCustom();
		default:
			throw new SystemException("Unsupported ContactCategory");
		}
	}

	public Class<?> contactValueClass() {
		if (contactClass.getGenericSuperclass() instanceof ParameterizedType) {
			ParameterizedType genericSuperclass = (ParameterizedType) contactClass.getGenericSuperclass();
			if (genericSuperclass.getActualTypeArguments().length != 0) {
				return (Class<?>) genericSuperclass.getActualTypeArguments()[0];
			}
		}
		throw new SystemException(format("Unable to determine value type for {0}", contactClass.getName()));
	}

	public boolean isAssignableFrom(Object value) {
		return isAssignableFrom(value.getClass());
	}

	public boolean isAssignableFrom(Class<?> valueClass) {
		return contactValueClass().isAssignableFrom(valueClass);
	}
}