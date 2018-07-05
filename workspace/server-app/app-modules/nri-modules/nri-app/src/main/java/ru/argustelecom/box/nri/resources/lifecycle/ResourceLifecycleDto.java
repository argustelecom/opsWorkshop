package ru.argustelecom.box.nri.resources.lifecycle;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import ru.argustelecom.box.env.dto2.ConvertibleDto;
import ru.argustelecom.box.nri.resources.lifecycle.model.ResourceLifecycle;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * ДТО фазы ЖЦ
 * Created by s.kolyada on 02.11.2017.
 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
public class ResourceLifecycleDto extends ConvertibleDto implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Идентификтор
	 */
	private Long id;

	/**
	 * Имя
	 */
	private String name;

	/**
	 * Фазы текущего ЖЦ
	 */
	@Setter
	private Set<ResourceLifecyclePhaseDto> phases = new HashSet<>();

	@Setter
	private ResourceLifecyclePhaseDto initialPhase;

	/**
	 * Конструктор
	 * @param id идентификатор
	 * @param name имя
	 * @param phases фазы ЖЦ
	 * @param initialPhase начальная фаза ЖЦ
	 */
	@Builder
	public ResourceLifecycleDto(Long id, String name, Set<ResourceLifecyclePhaseDto> phases,
								ResourceLifecyclePhaseDto initialPhase) {
		this.id = id;
		this.name = name;
		if (CollectionUtils.isEmpty(phases)) {
			this.phases = Collections.emptySet();
		} else {
			this.phases = phases;
		}
		this.initialPhase = initialPhase;
	}

	@Override
	public Class<ResourceLifecycle> getEntityClass() {
		return ResourceLifecycle.class;
	}

	@Override
	public Class<ResourceLifecycleDtoTranslator> getTranslatorClass() {
		return ResourceLifecycleDtoTranslator.class;
	}
}
