package ru.argustelecom.box.publang.billing.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;

import ru.argustelecom.box.env.mediation.nls.MediationMessagesBundle;
import ru.argustelecom.system.inf.exception.SystemException;

import static ru.argustelecom.box.inf.nls.LocaleUtils.getMessages;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = IResult.TYPE_NAME)
public enum IResult {
	OK, WARN, ERROR;
	public static final String TYPE_NAME = "iResult";

	public String getName() {
		MediationMessagesBundle messages = getMessages(MediationMessagesBundle.class);
		switch(this) {
			case OK:
				return messages.resultOk();
			case WARN:
				return messages.resultWarn();
			case ERROR:
				return messages.resultError();
			default:
				throw new SystemException("Unsupported Result");
		}
	}
}
