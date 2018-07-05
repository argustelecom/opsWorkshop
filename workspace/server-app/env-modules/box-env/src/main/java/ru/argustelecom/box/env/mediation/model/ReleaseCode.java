package ru.argustelecom.box.env.mediation.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.function.Function;

import ru.argustelecom.box.env.mediation.nls.MediationMessagesBundle;

import static ru.argustelecom.box.inf.nls.LocaleUtils.getMessages;

@AllArgsConstructor
public enum ReleaseCode {
	//FIXME HOTFIX, нужно будет обсудить с Алексеем формат данных, предоставляемых предбиллингом
	ANSWERED(MediationMessagesBundle::answeredCode, "ANSWERED"),
	BUSY(MediationMessagesBundle::busyCode, "BUSY"),
	NO_ANSWER(MediationMessagesBundle::noAnswer, "NO ANSWER"),
	FAILED(MediationMessagesBundle::failCode, "FAILED");

	private Function<MediationMessagesBundle, String> nameGetter;

	@Getter
	private String nameToGetValue;

	public String getName() {
		return nameGetter.apply(getMessages(MediationMessagesBundle.class));
	}
}
