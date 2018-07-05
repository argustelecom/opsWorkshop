package ru.argustelecom.box.inf.page.outcome.param;

import static com.google.common.base.Preconditions.checkArgument;

import ru.argustelecom.box.inf.page.outcome.OutcomeParam;

import com.google.common.base.Strings;

public abstract class AbstractOutcomeParam implements OutcomeParam {

	private String name;

	public AbstractOutcomeParam(String name) {
		String trimmedName = Strings.nullToEmpty(name).trim();
		checkArgument(!Strings.isNullOrEmpty(name));
		this.name = trimmedName;
	}

	public String getName() {
		return name;
	}

	public abstract String getValue();

	@Override
	public String toUriString() {
		return getName() + "=" + getValue();
	}
}
