package ru.argustelecom.box.env.type.model;

import static ru.argustelecom.system.inf.utils.CDIHelper.fireEvent;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

import ru.argustelecom.box.env.type.event.TypeInstanceEvent;
import ru.argustelecom.box.env.type.event.qualifier.literal.OnChangeLiteral;
import ru.argustelecom.box.env.type.event.qualifier.literal.OnRemoveLiteral;

public class TypeInstanceUniqueListener {
	@PostPersist
	@PostUpdate
	public void postUpdate(TypeInstance<?> instance) {
		fireEvent(new TypeInstanceEvent(instance), new OnChangeLiteral());
	}

	@PostRemove
	public void postRemove(TypeInstance<?> instance) {
		fireEvent(new TypeInstanceEvent(instance), new OnRemoveLiteral());
	}
}
