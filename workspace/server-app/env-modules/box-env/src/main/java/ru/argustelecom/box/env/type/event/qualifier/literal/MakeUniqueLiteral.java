package ru.argustelecom.box.env.type.event.qualifier.literal;

import javax.enterprise.util.AnnotationLiteral;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.argustelecom.box.env.type.event.qualifier.MakeUnique;
import ru.argustelecom.box.env.type.event.qualifier.UniqueMode;
import ru.argustelecom.box.env.type.model.Type;

@RequiredArgsConstructor
public class MakeUniqueLiteral extends AnnotationLiteral<MakeUnique> implements MakeUnique {
	@NonNull
	private Class<? extends Type> typeClass;

	@NonNull
	private UniqueMode mode;

	@Override
	public Class<? extends Type> typeClass() {
		return typeClass;
	}

	@Override
	public UniqueMode mode() {
		return mode;
	}

	private static final long serialVersionUID = 6216575668184122238L;
}
