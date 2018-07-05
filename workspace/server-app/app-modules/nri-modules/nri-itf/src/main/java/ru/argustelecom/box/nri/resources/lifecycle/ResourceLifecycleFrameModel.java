package ru.argustelecom.box.nri.resources.lifecycle;

import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDto;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Контроллер фрейма ЖЦ физического ресурса
 * Created by s.kolyada on 15.11.2017.
 */
@Named(value = "resourceLifecycleFrameModel")
@PresentationModel
public class ResourceLifecycleFrameModel implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Текущий ресурс
	 */
	private ResourceInstanceDto currentResource;

	/**
	 * Текущая фаза ЖЦ
	 */
	@Getter
	private ResourceLifecyclePhaseDto currentPhase;

	/**
	 * Сервис работы с ЖЦ
	 */
	@Inject
	private ResourceLifecycleAppService lifecycleAppService;

	/**
	 * Предварительная инициализация
	 * @param resourceInstanceDto ресурс
	 */
	public void preRender(ResourceInstanceDto resourceInstanceDto) {
		clear();

		currentResource = resourceInstanceDto;

		if (resourceInstanceDto == null) {
			return;
		}

		currentPhase = lifecycleAppService.getPhaseOfResource(resourceInstanceDto);
	}

	/**
	 * Получить все доступные переходы для ресурса
	 * @param resourceInstanceDto ресурс
	 * @return список доступных переходов
	 */
	public List<ResourceLifecyclePhaseTransitionDto> getRoutes(ResourceInstanceDto resourceInstanceDto) {
		List<ResourceLifecyclePhaseTransitionDto> outcomingRoutes = lifecycleAppService.loadTransitions(resourceInstanceDto);

		// если переходов нет, то зададим переходы во все статусы, что бы пользователь сам выбрал изначальное состояние
		if (CollectionUtils.isEmpty(outcomingRoutes)) {
			List<ResourceLifecyclePhaseDto> possiblePhases = lifecycleAppService.loadAllPossiblePhasesForResource(resourceInstanceDto);
			return possiblePhases.stream().map(p -> ResourceLifecyclePhaseTransitionDto.builder()
					.comment(p.getPhaseName())
					.outcomingPhase(p)
					.build()).collect(Collectors.toList());
		}

		return outcomingRoutes;
	}

	/**
	 * Имеет ли ресурс ЖЦ
	 * @return истина, если имеет, иначе ложь
	 */
	public boolean getHasLifecycle() {
		return lifecycleAppService.hasLifecycle(currentResource);
	}

	/**
	 * Изменить фазу ЖЦ ресурса
	 * @param transitionDto переход, по которому изменяется фаза
	 */
	public void changePhase(ResourceLifecyclePhaseTransitionDto transitionDto) {
		currentResource = lifecycleAppService.changeResourcePhase(currentResource, transitionDto.getOutcomingPhase());
		currentPhase = transitionDto.getOutcomingPhase();
	}

	/**
	 * Очистить параметры
	 */
	private void clear() {
		currentPhase = null;
		currentResource = null;
	}
}
