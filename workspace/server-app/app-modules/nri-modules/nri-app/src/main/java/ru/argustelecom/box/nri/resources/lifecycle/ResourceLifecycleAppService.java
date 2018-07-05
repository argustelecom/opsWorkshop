package ru.argustelecom.box.nri.resources.lifecycle;

import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.box.nri.resources.ResourceInstanceRepository;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDto;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDtoTranslator;
import ru.argustelecom.box.nri.resources.lifecycle.model.ResourceLifecycle;
import ru.argustelecom.box.nri.resources.lifecycle.model.ResourceLifecyclePhase;
import ru.argustelecom.box.nri.resources.lifecycle.model.ResourceLifecyclePhaseRepository;
import ru.argustelecom.box.nri.resources.lifecycle.model.ResourceLifecyclePhaseTransition;
import ru.argustelecom.box.nri.resources.lifecycle.model.ResourceLifecyclePhaseTransitionRepository;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationDto;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationDtoTranslator;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationRepository;
import ru.argustelecom.box.nri.resources.spec.model.ResourceSpecification;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Сервис для работы с ЖЦ
 * Created by s.kolyada on 07.11.2017.
 */
@ApplicationService
public class ResourceLifecycleAppService implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Репозиторий доступа к ЖЦ
	 */
	@Inject
	private ResourceLifecycleRepository lifecycleRepository;

	/**
	 * Репозиторий доступа к переходам ЖЦ
	 */
	@Inject
	private ResourceLifecyclePhaseTransitionRepository transitionRepository;

	/**
	 * Транслятор переходов ЖЦ
	 */
	@Inject
	private ResourceLifecyclePhaseTransitionDtoTranslator transitionDtoTranslator;

	/**
	 * Репозиторий доступа к фазам ЖЦ
	 */
	@Inject
	private ResourceLifecyclePhaseRepository phaseRepository;

	/**
	 * Транслятор ЖЦ
	 */
	@Inject
	private ResourceLifecycleDtoTranslator lifecycleDtoTranslator;

	/**
	 * Транслятор фаз ЖЦ
	 */
	@Inject
	private ResourceLifecyclePhaseDtoTranslator phaseDtoTranslator;

	/**
	 * Транслятор спецификаций ресурсов
	 */
	@Inject
	private ResourceSpecificationDtoTranslator resourceSpecificationDtoTranslator;

	/**
	 * Репозиторий доступа к спекам ресурсов
	 */
	@Inject
	private ResourceSpecificationRepository resourceSpecificationRepository;

	/**
	 * Репозиторий доступа к ресурсам
	 */
	@Inject
	private ResourceInstanceRepository resourceRepository;

	/**
	 * Транслятор ресурсов
	 */
	@Inject
	private ResourceInstanceDtoTranslator resourceInstanceDtoTranslator;

	/**
	 * Получить все фазы ЖЦ
	 * @param lifecycleDto ЖЦ
	 * @return все фазы заданного ЖЦ
	 */
	public List<ResourceLifecyclePhaseDto> loadAllLifecyclePhases(ResourceLifecycleDto lifecycleDto) {
		ResourceLifecycle lifecycle = lifecycleRepository.findById(lifecycleDto.getId());
		return loadAllLifecyclePhases(lifecycle);
	}

	/**
	 * Получить все фазы ЖЦ
	 * @param lifecycle ЖЦ
	 * @return все фазы заданного ЖЦ
	 */
	public List<ResourceLifecyclePhaseDto> loadAllLifecyclePhases(ResourceLifecycle lifecycle) {
		List<ResourceLifecyclePhase> phases = lifecycleRepository.findAllPhases(lifecycle);
		Map<Long, ResourceLifecyclePhaseDto> dtoPhases = new HashMap<>(phases.size());
		for (ResourceLifecyclePhase phase : phases) {
			initResourceLifecyclePhaseLinks(phase, dtoPhases);
		}
		return new ArrayList<>(dtoPhases.values());
	}

	/**
	 * Добавить новый переход
	 * @param from из фазы
	 * @param to в фазу
	 * @param transitionName имя перехода
	 * @return созданный переход
	 */
	public ResourceLifecyclePhaseTransitionDto addNewTransition(ResourceLifecyclePhaseDto from,
																ResourceLifecyclePhaseDto to,
																String transitionName) {
		ResourceLifecyclePhase fromPhase = phaseRepository.findById(from.getId());
		ResourceLifecyclePhase toPhase = phaseRepository.findById(to.getId());
		ResourceLifecyclePhaseTransition transition = transitionRepository.createTransition(fromPhase, toPhase, transitionName);
		ResourceLifecyclePhaseTransitionDto transitionDto = transitionDtoTranslator.translate(transition);
		from.getOutcomingPhases().add(transitionDto);

		return transitionDto;
	}

	/**
	 * Проинициализировать связи фазы ЖЦ
	 * @param lifecyclePhase фаза ЖЦ
	 * @param graphNodes связи
	 * @return проинициализированная фаза
	 */
	private ResourceLifecyclePhaseDto initResourceLifecyclePhaseLinks(ResourceLifecyclePhase lifecyclePhase,
																	  Map<Long, ResourceLifecyclePhaseDto> graphNodes) {
		ResourceLifecyclePhaseDto phaseDto = phaseDtoTranslator.translate(lifecyclePhase);

		if (graphNodes.containsKey(phaseDto.getId())) {
			return graphNodes.get(phaseDto.getId());
		}

		graphNodes.put(phaseDto.getId(), phaseDto);

		for (ResourceLifecyclePhaseTransition outcoming : lifecyclePhase.getOutcomingPhases()) {
			ResourceLifecyclePhaseDto outcomingPhaseDto = initResourceLifecyclePhaseLinks(outcoming.getOutcomingPhase(), graphNodes);

			ResourceLifecyclePhaseTransitionDto outcomingDto = ResourceLifecyclePhaseTransitionDto.builder()
					.id(outcoming.getId())
					.outcomingPhase(outcomingPhaseDto)
					.comment(outcoming.getComment())
					.build();

			phaseDto.getOutcomingPhases().add(outcomingDto);
		}

		return phaseDto;
	}

	/**
	 * Создать фазу ЖЦ
	 * @param lifecycleDto ЖЦ
	 * @param newPhaseName имя новой фазы ЖЦ
	 * @return созданная фаза ЖЦ
	 */
	public ResourceLifecyclePhaseDto createPhase(ResourceLifecycleDto lifecycleDto, String newPhaseName) {
		ResourceLifecycle lifecycle = lifecycleRepository.findById(lifecycleDto.getId());
		ResourceLifecyclePhase phase = phaseRepository.createPhase(lifecycle, newPhaseName);

		return phaseDtoTranslator.translate(phase);
	}

	/**
	 * Сохранить координаты на графе ЖЦ
	 * @param data фаза ЖЦ
	 * @param x координата х
	 * @param y координата у
	 * @return фаза ЖЦ
	 */
	public ResourceLifecyclePhaseDto saveCoordinates(ResourceLifecyclePhaseDto data, String x, String y) {
		ResourceLifecyclePhase phase = phaseRepository.updateCoordinates(data.getId(), x, y);
		return phaseDtoTranslator.translate(phase);
	}

	/**
	 * Обновить фазу ЖЦ
	 * @param selectedPhase выбранная фаза ЖЦ
	 * @return обновлённая фаза ЖЦ
	 */
	public ResourceLifecyclePhaseDto updatePhase(ResourceLifecyclePhaseDto selectedPhase) {
		ResourceLifecyclePhase phase = phaseRepository.findById(selectedPhase.getId());
		phase.setPhaseName(selectedPhase.getPhaseName());
		phase = phaseRepository.savePhase(phase);
		return phaseDtoTranslator.translate(phase);
	}

	/**
	 * Удалить переход между фазами ЖЦ
	 * @param transitionDto удаляемый переход
	 */
	public void removeTransition(ResourceLifecyclePhaseTransitionDto transitionDto) {
		transitionRepository.removeTransition(transitionDto.getId());
	}

	/**
	 * Переименовать переход
	 * @param transitionDto переход
	 * @param comment новое имя
	 * @return обновлённый переход
	 */
	public ResourceLifecyclePhaseTransitionDto renameTransition(ResourceLifecyclePhaseTransitionDto transitionDto,
																String comment) {
		ResourceLifecyclePhaseTransition transition = transitionRepository.rename(transitionDto.getId(), comment);
		return transitionDtoTranslator.translate(transition);
	}

	/**
	 * Найти все спецификации ресурсов с заданным ЖЦ
	 * @param lifecycleDto ЖЦ
	 * @return список спецификаций
	 */
	public List<ResourceSpecificationDto> findResourceSpecificationsWithLifecycle(ResourceLifecycleDto lifecycleDto) {
		ResourceLifecycle lifecycle = lifecycleRepository.findById(lifecycleDto.getId());
		List<ResourceSpecification> specifications = lifecycleRepository.findResourceSpecificationsWithLifecycle(lifecycle);
		if (specifications == null) {
			return new ArrayList<>();
		}
		return specifications.stream().map(resourceSpecificationDtoTranslator::translate).collect(Collectors.toList());
	}

	/**
	 * Получить текущую фазу ЖЦ ресурса
	 * @param resourceInstanceDto ресурс
	 * @return фаза ЖЦ в которой находится ресурс
	 */
	public ResourceLifecyclePhaseDto getPhaseOfResource(ResourceInstanceDto resourceInstanceDto) {
		ResourceInstance resourceInstance = resourceRepository.findOne(resourceInstanceDto.getId());
		if (resourceInstance == null) {
			return null;
		}
		ResourceLifecyclePhase phase = resourceInstance.getCurrentLifecyclePhase();
		return phaseDtoTranslator.translate(phase);
	}

	/**
	 * Загрузить все возможные переходы в другие фазы ЖЦ для ресурса
	 * @param resourceInstanceDto песурс
	 * @return список возможных переходов
	 */
	public List<ResourceLifecyclePhaseTransitionDto> loadTransitions(ResourceInstanceDto resourceInstanceDto) {
		ResourceInstance resourceInstance = resourceRepository.findOne(resourceInstanceDto.getId());
		List<ResourceLifecyclePhaseTransitionDto> result = new ArrayList<>();
		if (resourceInstance == null) {
			return result;
		}
		ResourceLifecyclePhase phase = resourceInstance.getCurrentLifecyclePhase();
		if (phase != null) {
			for (ResourceLifecyclePhaseTransition transition : phase.getOutcomingPhases()) {
				ResourceLifecyclePhaseTransitionDto transitionDto = transitionDtoTranslator.translate(transition);
				transitionDto.setOutcomingPhase(phaseDtoTranslator.translate(transition.getOutcomingPhase()));
				result.add(transitionDto);
			}
		}
		return result;
	}

	/**
	 * Поддерживает ли ресурс ЖЦ
	 * @param currentResource ресурс
	 * @return истина если поддерживает, иначе ложь
	 */
	public boolean hasLifecycle(ResourceInstanceDto currentResource) {
		ResourceSpecification specification = resourceSpecificationRepository.findOne(currentResource.getSpecification().getId());
		if (specification == null) {
			return false;
		}
		return specification.getLifecycle() != null;
	}

	/**
	 * Изменить текущую фазу ЖЦ ресурса
	 * @param currentResource ресурс
	 * @param outcomingPhase новая фаза ЖЦ
	 * @return обновлённый ресурс
	 */
	public ResourceInstanceDto changeResourcePhase(ResourceInstanceDto currentResource, ResourceLifecyclePhaseDto outcomingPhase) {
		ResourceInstance resourceInstance = resourceRepository.findOne(currentResource.getId());
		ResourceLifecyclePhase newPhase = phaseRepository.findById(outcomingPhase.getId());

		return resourceInstanceDtoTranslator.translate(lifecycleRepository.updateResourcePhase(resourceInstance, newPhase));
	}

	/**
	 * Загрузить все возможные фазы для ресурса
	 * @param resourceInstanceDto ресурс
	 * @return список возможных фаз ЖЦ ресурса
	 */
	public List<ResourceLifecyclePhaseDto> loadAllPossiblePhasesForResource(ResourceInstanceDto resourceInstanceDto) {
		ResourceInstance resourceInstance = resourceRepository.findOne(resourceInstanceDto.getId());
		ResourceLifecycle lifecycle = resourceInstance.getSpecification().getLifecycle();

		if (lifecycle == null) {
			return new ArrayList<>();
		}

		return loadAllLifecyclePhases(lifecycle);
	}

	/**
	 * Удалить фазу ЖЦ
	 * @param phaseToRemove удаляемая фаза
	 * @param phaseToSubstitude новая фаза для ресурсов
	 */
	public void removePhase(ResourceLifecyclePhaseDto phaseToRemove, ResourceLifecyclePhaseDto phaseToSubstitude) {
		phaseRepository.remove(phaseToRemove.getId(), phaseToSubstitude.getId());
	}

	/**
	 * Проверка есть ли у фазы ЖЦ входящие переходы
	 * @param phaseDto фаза ЖЦ
	 * @return истина если есть,и наче ложь
	 */
	public boolean phaseHasAtLeastOneIncomingTransition(ResourceLifecyclePhaseDto phaseDto) {
		if (phaseDto == null || phaseDto.getId() == null) {
			return false;
		}
		ResourceLifecyclePhase phase = phaseRepository.findById(phaseDto.getId());
		return !phase.getIncomingPhases().isEmpty();
	}

	/**
	 * Обновить начальную фазу ЖЦ
	 * @param lifecycle жц
	 * @param initialPhase новая фаза
	 * @return обновлённый жц
	 */
	public ResourceLifecycleDto changeLifecycleInitailPhase(ResourceLifecycleDto lifecycle, ResourceLifecyclePhaseDto initialPhase) {
		return lifecycleDtoTranslator.translate(lifecycleRepository.updateInitialPhase(lifecycle.getId(), initialPhase.getId()));
	}
}
