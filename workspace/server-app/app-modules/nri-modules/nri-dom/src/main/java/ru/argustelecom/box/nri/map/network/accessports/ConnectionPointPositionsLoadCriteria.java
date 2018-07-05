package ru.argustelecom.box.nri.map.network.accessports;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.address.model.Region;
import ru.argustelecom.box.nri.resources.model.ResourceState;
import ru.argustelecom.box.nri.resources.spec.model.ResourceSpecification;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Критерии (фильтры), определяющие какие точки (порты или точки подключения абонентов) загрузить.
 * 
 */
public class ConnectionPointPositionsLoadCriteria {

	private Region region;
	@Getter
	@Setter
	private ResourceSpecification rs;
	private Set<ResourceState> objectStates;

	public ConnectionPointPositionsLoadCriteria(Region region,
												ResourceSpecification spec,
												Collection<ResourceState> objectStates) {
		this.region = region;
		this.rs = checkNotNull(spec);
		this.objectStates = new HashSet<>(checkNotNull(objectStates));
	}
	/**
	 * Загружать и учитывать только точки в указанных статусах.
	 */
	@NotNull
	public Set<ResourceState> getObjectStates() {
		return objectStates;
	}

}
