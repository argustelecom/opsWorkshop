package ru.argustelecom.box.nri.resources.lifecycle;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.dto2.ConvertibleDto;
import ru.argustelecom.box.nri.resources.lifecycle.model.ResourceLifecyclePhaseTransition;

import java.io.Serializable;

/**
 * ДТО перехода между фазами ЖЦ
 * Created by s.kolyada on 07.11.2017.
 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
public class ResourceLifecyclePhaseTransitionDto extends ConvertibleDto implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Идентификтор
	 */
	@Setter
	private Long id;

	/**
	 * Исходящая фаза
	 */
	@Setter
	private ResourceLifecyclePhaseDto incomingPhase;

	/**
	 * Итоговая фаза
	 */
	@Setter
	private ResourceLifecyclePhaseDto outcomingPhase;

	/**
	 * Название перехода
	 */
	@Setter
	private String comment;

	/**
	 * Конструктор
	 * @param id идентификатор
	 * @param incomingPhase фаза из
	 * @param outcomingPhase ваза в
	 * @param comment название
	 */
	@Builder
	public ResourceLifecyclePhaseTransitionDto(Long id, ResourceLifecyclePhaseDto incomingPhase,
											   ResourceLifecyclePhaseDto outcomingPhase, String comment) {
		this.id = id;
		this.outcomingPhase = outcomingPhase;
		this.incomingPhase = incomingPhase;
		this.comment = comment;
	}

	@Override
	public Class<ResourceLifecyclePhaseTransition> getEntityClass() {
		return ResourceLifecyclePhaseTransition.class;
	}

	@Override
	public Class<ResourceLifecyclePhaseTransitionDtoTranslator> getTranslatorClass() {
		return ResourceLifecyclePhaseTransitionDtoTranslator.class;
	}
}
