package ru.argustelecom.box.env.billing.invoice.model;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static lombok.AccessLevel.MODULE;
import static ru.argustelecom.box.inf.nls.LocaleUtils.getMessages;

import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.box.env.billing.invoice.nls.ChargeJobMessagesBundle;
import ru.argustelecom.system.inf.exception.SystemException;

@AllArgsConstructor(access = MODULE)
public enum JobDataType {
	//@formatter:off
	REGULAR 	("REGULAR", ChargeJobMessagesBundle::regular),
	SUITABLE 	("SUITABLE", ChargeJobMessagesBundle::suitable),
	UNSUITABLE 	("UNSUITABLE", ChargeJobMessagesBundle::unsuitable);
	//@formatter:on

	@Getter
	private String name;
	private Function<ChargeJobMessagesBundle, String> nameGetter;

	public static JobDataType getByString(String jobName) {
		//@formatter:off
		return stream(values())
				.filter(type -> type.getName().equals(jobName))
				.findFirst()
				.orElseThrow(() -> new SystemException(format("Unsupported JobDataType: %s", jobName)));
		//@formatter:on
	}

	public String nameToShow() {
		return nameGetter.apply(getMessages(ChargeJobMessagesBundle.class));
	}
}
