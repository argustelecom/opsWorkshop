package ru.argustelecom.box.nri.resources.lifecycle;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Контроллер диалога просмотра фазы ЖЦ физического ресурса
 * Created by s.kolyada on 09.11.2017.
 */
@PresentationModel
public class LifecyclePhaseViewDialogModel implements Serializable {

	private static final long serialVersionUID = -6080540983649523620L;

	/**
	 * последняя добавленная фаза ЖЦ
	 */
	@Getter
	private ResourceLifecyclePhaseTransitionDto lastAddedNewOutcomingPhase;

	/**
	 * Исходящие переходы текущей фазы
	 */
	@Getter
	private List<ResourceLifecyclePhaseTransitionDto> outcomingPhases = new LinkedList<>();

	/**
	 * Остальные фазы ЖЦ
	 */
	@Getter
	private Set<ResourceLifecyclePhaseDto> phases;

	/**
	 * Сервис работы с ЖЦ
	 */
	@Inject
	private ResourceLifecycleAppService lifecycleAppService;

	/**
	 * Отображаемая фаза ЖЦ
	 */
	@Getter
	private ResourceLifecyclePhaseDto selectedPhase;

	/**
	 * Фаза замещающая в ресурсах удаляемую фазу
	 */
	@Getter
	@Setter
	private ResourceLifecyclePhaseDto substitutingPhase;

	/**
	 * Выставить фазу ЖЦ
	 * @param selectedPhase фаза ЖЦ
	 */
	public void setSelectedPhase(ResourceLifecyclePhaseDto selectedPhase) {
		this.selectedPhase = selectedPhase;
		this.outcomingPhases = new LinkedList<>(selectedPhase.getOutcomingPhases());
	}

	/**
	 * Очистить параемтры
	 */
	public void clear() {
		outcomingPhases.clear();
		lastAddedNewOutcomingPhase = null;
		substitutingPhase = null;
	}

	/**
	 * Добавить переход в новую фазу
	 */
	public void addNewOutcomingPhase() {
		ResourceLifecyclePhaseDto outcomingPhase = null;
		if (!CollectionUtils.isEmpty(phases)) {
			outcomingPhase = phases.stream().findFirst().get();
		}
		lastAddedNewOutcomingPhase = ResourceLifecyclePhaseTransitionDto.builder()
				.incomingPhase(selectedPhase)
				.comment("")
				.outcomingPhase(outcomingPhase)
				.build();
		outcomingPhases.add(lastAddedNewOutcomingPhase);
	}

	/**
	 * Событие завершения редактирования перехода
	 * @param event событие
	 */
	public void acceptEditNewOutcomingPhase(RowEditEvent event) {
		// получаеми инфу о переходе
		ResourceLifecyclePhaseTransitionDto holder = (ResourceLifecyclePhaseTransitionDto) event.getObject();

		// проверяем валидность, если не валидно, то заваливаем валидацию и сообщаем об этом
		if (!isValidPhaseTransition(outcomingPhases, holder)) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,"Ошибка валидации",
							"Все параметры должны быть уникальными"));
			FacesContext.getCurrentInstance().validationFailed();
			return;
		}

		// если это новая фаза, то сохраняем её
		if (lastAddedNewOutcomingPhase != null) {
			holder.setId(lifecycleAppService.addNewTransition(selectedPhase,
																holder.getOutcomingPhase(),
																holder.getComment()).getId());
		} else {
			// если это уже существующая фаза, то сохраняем её изменения
			lifecycleAppService.renameTransition(holder, holder.getComment());
		}

		// сбрасываем переменную что бы отличать новые созданные и редактируемые фазы
		lastAddedNewOutcomingPhase = null;
	}

	/**
	 * Событие отмены редактирования информации о переходе
	 */
	public void cancelEditNewOutcomingPhase() {
		// если отменили создание нового перехода, то удаляем его из списка переходов
		if (lastAddedNewOutcomingPhase != null) {
			outcomingPhases.remove(lastAddedNewOutcomingPhase);
			lastAddedNewOutcomingPhase = null;
		}
	}

	/**
	 * Проверка валидности переходы
	 * @param phases список всех переходов
	 * @param editedPhase отредактированный переход
	 * @return истина если валиен, иначе ложь
	 */
	private boolean isValidPhaseTransition(List<ResourceLifecyclePhaseTransitionDto> phases,
										   ResourceLifecyclePhaseTransitionDto editedPhase) {
		// если всего есть 1 переход, то заведомо валиден, тк список состоит только их валидируемого перехода
		if (phases.size() == 1) {
			return true;
		}

		// проверяем, что параметры не пересекаются с уже заданными
		// тк имена переходов и их итоговые состояния дб уникальными
		for (ResourceLifecyclePhaseTransitionDto phase : phases) {
			if (phase.equals(editedPhase)) {
				continue;
			}
			if (StringUtils.equalsIgnoreCase(phase.getComment(), editedPhase.getComment())
					|| phase.getOutcomingPhase().equals(editedPhase.getOutcomingPhase())) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Указать фазы ЖЦ
	 * @param phases
	 */
	public void setPhases(Set<ResourceLifecyclePhaseDto> phases) {
		this.phases = new HashSet<>(phases);
		// удаляем из общего списка отображаемую фазу, что бы не дать возможности создать переход на самого себя
		this.phases.remove(selectedPhase);
	}

	/**
	 * Удалить переход
	 * @param transitionDto переход
	 */
	public void deleteTransition(ResourceLifecyclePhaseTransitionDto transitionDto) {
		if (transitionDto == null) {
			return;
		}
		if (selectedPhase.getOutcomingPhases().size() == 1
				&& !lifecycleAppService.phaseHasAtLeastOneIncomingTransition(selectedPhase)) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,"Ошибка валидации",
							"Фаза ЖЦ должна содержать хотя бы один переход"));
			FacesContext.getCurrentInstance().validationFailed();
			return;
		}
		// если есть идентификатор, значит надо удалять переход из БД
		if (transitionDto.getId() != null) {
			lifecycleAppService.removeTransition(transitionDto);
		}
		// удаляем из текущих данных)
		selectedPhase.getOutcomingPhases().remove(transitionDto);
		outcomingPhases.remove(transitionDto);
		lastAddedNewOutcomingPhase = null;
	}

	/**
	 * Обновить информацию о фазе
	 */
	public void changePhaseInfo() {
		selectedPhase = lifecycleAppService.updatePhase(selectedPhase);
	}

	/**
	 * Удалить фазу ЖЦ
	 */
	public void removePhase() {
		if (selectedPhase == null) {
			return;
		}

		if (CollectionUtils.isEmpty(phases)) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,"Ошибка валидации",
							"ЖЦ должен содержать хотя бы одно состояние"));
			FacesContext.getCurrentInstance().validationFailed();
			return;
		}

		// если остаётся всего одна фаза ЖЦ, то переводим все ресурсы с удаляемой фазой в единственную оставшуюся фазу
		if (phases.size() == 1) {
			removePhase(selectedPhase, phases.stream().findFirst().get());
		} else {
			// иначе предлагаем пользователю самостоятельно выбрать фазу ЖЦ для ресурсов
			substitutingPhase = phases.stream().findFirst().get();
			RequestContext.getCurrentInstance().update("newPhaseSelectionDlg");
			RequestContext.getCurrentInstance().execute("PF('newPhaseSelectionDlg').show()");
		}
	}

	/**
	 * Удалить фазу после выбора замещающей фазы
	 */
	public void removeWithSubstitution() {
		removePhase(selectedPhase, substitutingPhase);
	}

	/**
	 * Удалить фазу с указанием новой фазы для ресурсов
	 * @param phaseToDelete удаляемая фаза
	 * @param newPhaseForResources замещающая фаза
	 */
	private void removePhase(ResourceLifecyclePhaseDto phaseToDelete, ResourceLifecyclePhaseDto newPhaseForResources) {
		lifecycleAppService.removePhase(selectedPhase, newPhaseForResources);
	}
}
