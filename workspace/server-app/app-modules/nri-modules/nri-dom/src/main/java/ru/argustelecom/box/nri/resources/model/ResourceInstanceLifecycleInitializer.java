package ru.argustelecom.box.nri.resources.model;

import ru.argustelecom.box.nri.resources.lifecycle.model.ResourceLifecycle;

import javax.persistence.PrePersist;

/**
 * Инициализация начального статуса ресурса
 * Created by s.kolyada on 20.11.2017.
 */
public class ResourceInstanceLifecycleInitializer {

	/**
	 * Выставляет статус ресурса по умолчанию при первом сохранении экземпляра ресурса
	 * @param resourceInstance ресурс
	 */
	@PrePersist
	public void initDefaultLifecyclePhase(ResourceInstance resourceInstance) {
		ResourceLifecycle lifecycle = resourceInstance.getSpecification().getLifecycle();
		if (lifecycle == null || lifecycle.getInitialPhase() == null) {
			return;
		}
		resourceInstance.setCurrentLifecyclePhase(lifecycle.getInitialPhase());
	}

}
