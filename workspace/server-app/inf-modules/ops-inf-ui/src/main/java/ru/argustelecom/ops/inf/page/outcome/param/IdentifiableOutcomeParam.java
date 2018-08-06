package ru.argustelecom.ops.inf.page.outcome.param;

import static com.google.common.base.Preconditions.checkNotNull;

import ru.argustelecom.system.inf.convert.PersistentIdentifiableConverter;
import ru.argustelecom.system.inf.modelbase.Identifiable;

public class IdentifiableOutcomeParam extends AbstractOutcomeParam {

	private Identifiable identifiable;

	public IdentifiableOutcomeParam(String name, Identifiable identifiable) {
		super(name);
		this.identifiable = checkNotNull(identifiable);
	}

	public static IdentifiableOutcomeParam of(String name, Identifiable identifiable) {
		return new IdentifiableOutcomeParam(name, identifiable);
	}

	public Identifiable getIdentifiable() {
		return identifiable;
	}

	@Override
	public String getValue() {
		return new PersistentIdentifiableConverter().getAsString(null, null, identifiable);
	}
}
