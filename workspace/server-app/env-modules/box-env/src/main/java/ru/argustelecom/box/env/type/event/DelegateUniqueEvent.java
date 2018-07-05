package ru.argustelecom.box.env.type.event;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.argustelecom.box.env.type.model.TypeInstance;
import ru.argustelecom.box.env.type.model.TypeProperty;

@Getter
@RequiredArgsConstructor
public class DelegateUniqueEvent {
	@NonNull
	private Class<? extends TypeInstance<?>> instanceClass;

	@NonNull
	private TypeProperty<?> property;
}
