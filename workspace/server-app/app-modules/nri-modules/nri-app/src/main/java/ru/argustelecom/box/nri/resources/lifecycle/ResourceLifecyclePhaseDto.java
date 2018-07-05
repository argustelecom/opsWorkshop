package ru.argustelecom.box.nri.resources.lifecycle;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import ru.argustelecom.box.env.dto2.ConvertibleDto;
import ru.argustelecom.box.nri.resources.lifecycle.model.ResourceLifecyclePhase;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * ДТО фазы ЖЦ
 * Created by s.kolyada on 07.11.2017.
 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
public class ResourceLifecyclePhaseDto extends ConvertibleDto implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Идентификтор
	 */
	private Long id;

	/**
	 * Название фазы
	 */
	@Setter
	private String phaseName;

	/**
	 * Исходящие переходы
	 */
	private Set<ResourceLifecyclePhaseTransitionDto> outcomingPhases = new HashSet<>();

	/**
	 * Координата х
	 */
	private String x;

	/**
	 * Коорджината у
	 */
	private String y;

	/**
	 * Конструктор
	 * @param id иджентификатор
	 * @param phaseName имя
	 * @param outcomingPhases исходящие переходы
	 * @param x кооржината х
	 * @param y координата у
	 */
	@Builder
	public ResourceLifecyclePhaseDto(Long id, String phaseName, Set<ResourceLifecyclePhaseTransitionDto> outcomingPhases,
									 String x, String y) {
		this.id = id;
		this.phaseName = phaseName;
		this.x = x;
		this.y = y;
		if (CollectionUtils.isEmpty(outcomingPhases)) {
			this.outcomingPhases = new HashSet<>();
		} else {
			this.outcomingPhases = outcomingPhases;
		}
	}

	@Override
	public Class<ResourceLifecyclePhase> getEntityClass() {
		return ResourceLifecyclePhase.class;
	}

	@Override
	public Class<ResourceLifecyclePhaseDtoTranslator> getTranslatorClass() {
		return ResourceLifecyclePhaseDtoTranslator.class;
	}
}
