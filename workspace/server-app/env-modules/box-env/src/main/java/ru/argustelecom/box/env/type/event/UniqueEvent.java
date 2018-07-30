package ru.argustelecom.box.env.type.event;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.argustelecom.box.env.type.model.TypeProperty;

@Getter
@RequiredArgsConstructor
public class UniqueEvent {
	@NonNull
	private TypeProperty<?> property;
}
