package ru.argustelecom.box.nri.resources.lifecycle;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.RowEditEvent;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Контроллер диалога создания фазы и перехода ЖЦ
 * Created by s.kolyada on 09.11.2017.
 */
@PresentationModel
public class LifecyclePhaseAddDialogModel implements Serializable {

	private static final long serialVersionUID = -6080540983649523620L;

	/**
	 * Режим создания фазы ЖЦ
	 */
	@Getter
	@Setter
	private boolean phaseCreationMode = false;

	/**
	 * Новые входящие фазы
	 */
	@Getter
	private List<PhaseTransitionHolder> newIncomingPhases = new ArrayList<>();

	/**
	 * Последняя добавленная входящая фаза
	 */
	private PhaseTransitionHolder lastAddedNewIncomingPhase;

	/**
	 * Последняя добавленная исходящая фаза
	 */
	private PhaseTransitionHolder lastAddedNewOutcomingPhase;

	/**
	 * Новые исходящие фазы
	 */
	@Getter
	private List<PhaseTransitionHolder> newOutcomingPhases = new ArrayList<>();

	/**
	 * Входящая фаза
	 */
	@Getter
	@Setter
	private ResourceLifecyclePhaseDto incomingPhase;

	/**
	 * Исходящая фаза
	 */
	@Getter
	@Setter
	private ResourceLifecyclePhaseDto outcomingPhase;

	/**
	 * Имя новой фазы
	 */
	@Getter
	@Setter
	private String newPhaseName;

	/**
	 * Имя нового перехода
	 */
	@Getter
	@Setter
	private String newTransitionName;

	/**
	 * Все фазы
	 */
	@Getter
	@Setter
	private Set<ResourceLifecyclePhaseDto> phases = new HashSet<>();

	/**
	 * КОлбек создания фазы
	 */
	@Getter
	@Setter
	private Callback<ResourceLifecyclePhaseDto> phaseCallback;

	/**
	 * КОлбек созданяи перехода
	 */
	@Getter
	@Setter
	private Callback<ResourceLifecyclePhaseTransitionDto> transitionCallback;

	/**
	 * Сервис работы с ЖЦ
	 */
	@Inject
	private ResourceLifecycleAppService lifecycleAppService;

	/**
	 * ЖЦ
	 */
	@Setter
	private ResourceLifecycleDto lifecycle;

	/**
	 * Очистить параметры
	 */
	public void clear() {
		incomingPhase = null;
		outcomingPhase = null;
		newPhaseName = null;
		newTransitionName = null;
		newIncomingPhases.clear();
		newOutcomingPhases.clear();
		lastAddedNewIncomingPhase = null;
		lastAddedNewOutcomingPhase = null;
	}

	/**
	 * Получить заголовок диалога
	 */
	public String getDialogHeader() {
		return phaseCreationMode ? "Создать новое состояние" : "Создать переход между состояниями";
	}

	/**
	 * Создать новый переход
	 */
	public void createNewPhaseTransition() {
		ResourceLifecyclePhaseTransitionDto transitionDto = buildTransition(incomingPhase, outcomingPhase, newTransitionName);
		transitionDto.setIncomingPhase(incomingPhase);
		transitionDto.setOutcomingPhase(outcomingPhase);
		clear();
		transitionCallback.execute(transitionDto);
	}

	/**
	 * Создать новыую фазу
	 */
	public void createNewPhase() {
		if (CollectionUtils.isEmpty(newIncomingPhases) && CollectionUtils.isEmpty(newOutcomingPhases)) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,"Ошибка валидации",
							"У состояния должен быть хоть один входящий или исходящий переход"));
			FacesContext.getCurrentInstance().validationFailed();
			return;
		}

		ResourceLifecyclePhaseDto phaseDto = lifecycleAppService.createPhase(lifecycle, newPhaseName);
		phaseCallback.execute(phaseDto);

		for (PhaseTransitionHolder holder : newIncomingPhases) {
			ResourceLifecyclePhaseTransitionDto transitionDto = lifecycleAppService.addNewTransition(holder.phase, phaseDto, holder.name);
			transitionDto.setIncomingPhase(holder.phase);
			transitionDto.setOutcomingPhase(phaseDto);
			transitionCallback.execute(transitionDto);
		}

		for (PhaseTransitionHolder holder : newOutcomingPhases) {
			ResourceLifecyclePhaseTransitionDto transitionDto = lifecycleAppService.addNewTransition(phaseDto, holder.phase, holder.name);
			transitionDto.setIncomingPhase(phaseDto);
			transitionDto.setOutcomingPhase(holder.phase);
			transitionCallback.execute(transitionDto);
		}

		clear();
	}

	/**
	 * Собрать переход
	 * @param from из
	 * @param to в
	 * @param transitionName имя
	 * @return переход
	 */
	private ResourceLifecyclePhaseTransitionDto buildTransition(ResourceLifecyclePhaseDto from,
																ResourceLifecyclePhaseDto to, String transitionName) {
		return lifecycleAppService.addNewTransition(from, to, transitionName);
	}

	/**
	 * Добавит новую входную фазу
	 */
	public void addNewIncomingPhase() {
		lastAddedNewIncomingPhase = PhaseTransitionHolder.builder()
				.name("")
				.phase(phases.stream().findFirst().orElse(null))
				.build();
		newIncomingPhases.add(lastAddedNewIncomingPhase);
	}

	/**
	 * Добавить новую исходящую фазу
	 */
	public void addNewOutcomingPhase() {
		lastAddedNewOutcomingPhase = PhaseTransitionHolder.builder()
				.name("")
				.phase(phases.stream().findFirst().orElse(null))
				.build();
		newOutcomingPhases.add(lastAddedNewOutcomingPhase);
	}

	/**
	 * Событие подтвержддения редактирования входной фазы
	 * @param event событие
	 */
	public void acceptEditNewIncomingPhase(RowEditEvent event) {
		PhaseTransitionHolder holder = (PhaseTransitionHolder) event.getObject();

		if (!isValidPhase(newIncomingPhases, holder)) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,"Ошибка валидации",
							"Все параметры должны быть уникальными"));
			FacesContext.getCurrentInstance().validationFailed();
			return;
		}
		lastAddedNewIncomingPhase = null;
	}

	/**
	 * Событие подтвержддения редактирования выходной фазы
	 * @param event событие
	 */
	public void acceptEditNewOutcomingPhase(RowEditEvent event) {
		PhaseTransitionHolder holder = (PhaseTransitionHolder) event.getObject();

		if (!isValidPhase(newOutcomingPhases, holder)) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,"Ошибка валидации",
							"Все параметры должны быть уникальными"));
			FacesContext.getCurrentInstance().validationFailed();
			return;
		}
		lastAddedNewOutcomingPhase = null;
	}

	/**
	 * Событьие отмены редактирования входной фазы
	 */
	public void cancelEditNewIncomingPhase() {
		if (lastAddedNewIncomingPhase != null) {
			newIncomingPhases.remove(lastAddedNewIncomingPhase);
		}
	}

	/**
	 * Событьие отмены редактирования выходной фазы
	 */
	public void cancelEditNewOutcomingPhase() {
		if (lastAddedNewOutcomingPhase != null) {
			newOutcomingPhases.remove(lastAddedNewOutcomingPhase);
		}
	}

	/**
	 * Проверка валидности фазы
	 * @param phases все фазы
	 * @param editedPhase изменённая фаза
	 * @return истина если валидна, иначе ложь
	 */
	private boolean isValidPhase(List<PhaseTransitionHolder> phases, PhaseTransitionHolder editedPhase) {
		if (phases.size() == 1) {
			return true;
		}

		for (PhaseTransitionHolder phase : phases) {
			if (phase.equals(editedPhase)) {
				continue;
			}
			if (StringUtils.equalsIgnoreCase(phase.getName(), editedPhase.getName())
					|| phase.getPhase().equals(editedPhase.getPhase())) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Холдер переходов
	 */
	@Getter
	@Setter
	public static class PhaseTransitionHolder implements Serializable {

		private static final long serialVersionUID = -6080540983649523620L;

		private ResourceLifecyclePhaseDto phase;

		private String name;

		@Builder
		public PhaseTransitionHolder(ResourceLifecyclePhaseDto phase, String name) {
			this.phase = phase;
			this.name = name;
		}
	}
}
