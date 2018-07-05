package ru.argustelecom.box.publang.base.wrapper;

import java.io.Serializable;

import ru.argustelecom.box.publang.base.model.IEntity;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@EntityWrapperImplementer
public interface EntityWrapper extends Serializable {

	<E extends Identifiable> IEntity wrap(E entity);

	<I extends IEntity> Identifiable unwrap(I wrappedEntity);

}