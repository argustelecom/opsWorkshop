package ru.argustelecom.box.env.type.event;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.argustelecom.box.env.type.model.TypeInstance;

@Getter
@RequiredArgsConstructor
public class TypeInstanceEvent {
	@NonNull
	private TypeInstance<?> instance;
}
