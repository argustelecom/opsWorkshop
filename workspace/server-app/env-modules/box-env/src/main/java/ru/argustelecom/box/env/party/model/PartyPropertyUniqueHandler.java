package ru.argustelecom.box.env.party.model;

import static com.google.common.collect.ImmutableList.of;
import static ru.argustelecom.box.env.type.event.qualifier.UniqueMode.DISABLE;
import static ru.argustelecom.box.env.type.event.qualifier.UniqueMode.ENABLE;

import java.util.List;

import javax.enterprise.event.Observes;

import ru.argustelecom.box.env.type.BaseTypePropertyUniqueHandler;
import ru.argustelecom.box.env.type.event.UniqueEvent;
import ru.argustelecom.box.env.type.event.qualifier.MakeUnique;
import ru.argustelecom.box.env.type.model.TypeInstance;
import ru.argustelecom.box.inf.service.DomainService;

@DomainService
public class PartyPropertyUniqueHandler extends BaseTypePropertyUniqueHandler {

	@Override
	public void handleEnable(@Observes @MakeUnique(typeClass = PartyType.class, mode = ENABLE) UniqueEvent event) {
		super.handleEnable(event);
	}

	@Override
	public void handleDisable(@Observes @MakeUnique(typeClass = PartyType.class, mode = DISABLE) UniqueEvent event) {
		super.handleDisable(event);
	}

	@Override
	protected List<Class<? extends TypeInstance<?>>> getInstanceClasses() {
		return of(PartyTypeInstance.class);
	}
}
