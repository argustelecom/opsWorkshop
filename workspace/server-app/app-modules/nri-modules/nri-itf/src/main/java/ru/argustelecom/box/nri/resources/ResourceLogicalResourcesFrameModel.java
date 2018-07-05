package ru.argustelecom.box.nri.resources;

import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;
import ru.argustelecom.box.nri.logicalresources.LogicalResourceDto;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberAppService;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberDto;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberPoolAppService;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberPoolDto;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecification;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecificationRepository;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceAppService;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDto;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Iterables.isEmpty;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

/**
 * Контроллер номеров
 * @author a.wisniewski
 * @since 31.10.2017
 */
@Named(value = "resourceLogicalResFM")
@PresentationModel
@Getter
@Setter
public class ResourceLogicalResourcesFrameModel implements Serializable {

	private static final long serialVersionUID = 4248571668488742577L;

	/**
	 * Ресурс, с которым работаем
	 */
	private ResourceInstanceDto resource;

	/**
	 * Добавляемый номер
	 */
	private PhoneNumberDto newPhoneNumber;

	/**
	 * Выбранный пул в диалоге добавления номера
	 */
	private PhoneNumberPoolDto selectedPool;

	/**
	 * Список возможных пулов
	 */
	private List<PhoneNumberPoolDto> availablePools;

	/**
	 * Номера, выбранные в таблице
	 */
	private List<LogicalResourceDto> selectedPhoneNumbers;

	/**
	 * Лениво загружаемые номера пулов (чтобы не загружать два раза номера одного пула)
	 */
	private Map<Long, List<PhoneNumberDto>> lazyPoolNumbers = new HashMap<>();

	/**
	 * Сервис ресурсов
	 */
	@Inject
	private ResourceInstanceAppService resService;

	/**
	 * возможные телефонные спеки
	 */
	@Getter
	@Setter
	private List<PhoneNumberSpecification> possiblePhoneSpecs;

	/**
	 * Репозиторий спецификаций телефонныъ номеров
	 */
	@Inject
	private PhoneNumberSpecificationRepository phoneNumberSpecificationRepository;

	/**
	 * Сервис доступа к телефонным номерам
	 */
	@Inject
	private PhoneNumberAppService phoneService;

	/**
	 * Сервис пулов номеров
	 */
	@Inject
	private PhoneNumberPoolAppService poolService;

	/**
	 * Начальный номер для добавления из рэнжа
	 */
	private String from;

	/**
	 * Конечный номер для добавления из рэнжа
	 */
	private String to;

	/**
	 * Спецификация номера для добавления из ренжа
	 */
	private PhoneNumberSpecification phoneSpec;

	/**
	 * Галочка, добавлять ли все номера из пула
	 */
	private boolean allPoolPhoneNumbers;

	/**
	 * Показывать ли диалог подтверждения добавления номеров
	 */
	private boolean renderConfirmDialog;

	/**
	 * Список телефонов, которые мы хотим добавить в ресурс. Содержат как свободные, так и занятые ресурсы
	 */
	private List<PhoneNumberDto> phoneNumbersToAdd;

	/**
	 * Событие перед рендером фрагмента
	 * @param resource ресурс, для которого рендерится фрагмент
	 */
	public void preRender(ResourceInstanceDto resource) {
		this.resource = resource;
	}

	/**
	 * Инициализация
	 */
	public void init() {
		if (possiblePhoneSpecs == null)
			possiblePhoneSpecs = phoneNumberSpecificationRepository.getAllSpecs();
		from = null;
		to = null;
	}

	/**
	 * Удалить выбранные номера
	 */
	public void removeSelected() {
		List<Long> phoneNumbersToDelete = selectedPhoneNumbers.stream().map(LogicalResourceDto::getId).collect(toList());
		phoneService.removeFromResource(phoneNumbersToDelete);
		resource.getLogicalResources().removeAll(selectedPhoneNumbers);
	}

	/**
	 * Получить доступные пулы
	 * @return доступные пулы
	 */
	public List<PhoneNumberPoolDto> getAvailablePools() {
		if (availablePools == null)
			availablePools = poolService.findAllLazy();
		return availablePools;
	}

	/**
	 * Получить номера, доступные в выбранном пуле
	 * @return номера, доступные в выбранном пуле
	 */
	public List<PhoneNumberDto> getAvailablePhoneNumbers() {
		if (selectedPool == null)
			return emptyList();
		return lazyPoolNumbers.computeIfAbsent(selectedPool.getId(),
					poolId -> poolService.getPhoneNumbersByPoolAndRes(poolId, resource.getId()))
				.stream()
				.filter(number -> number.getResource() == null)
				.filter(number -> !resource.containsLogicalResourceWithId(number.getId()))
				.collect(toList());
	}

	/**
	 * Добавить телефонные номера из выбранного пула
	 */
	public void addPhoneNumbersFromPool() {
		// если стоит галочка "добавить все номера пула", добавляем все
		if (allPoolPhoneNumbers) {
			phoneNumbersToAdd = phoneService.findPhoneNumbers(selectedPool);
		} else {
			PhoneNumberDto phoneNumberToAdd = phoneService.findPhoneNumberById(newPhoneNumber.getId());
			phoneNumbersToAdd = phoneNumberToAdd == null ? emptyList() : singletonList(phoneNumberToAdd);
		}
		addPhoneNumbersOrShowWarningDialog();
	}

	/**
	 * Добавить телефонные номера из указанного диапазона
	 */
	public void addPhoneNumbersFromRange() {
		phoneNumbersToAdd = phoneService.findPhoneNumbers(from, to);
		phoneNumbersToAdd = phoneNumbersToAdd.stream()
				.filter(number -> number.getResource() == null)
				.collect(toList());
		addPhoneNumbersOrShowWarningDialog();
	}

	/**
	 * Определяет ,выведется ли диалог подтверждения. либо сразу добавятся номера. Требуется, чтобы
	 * пресечь случаи попытки "тихого" угона номера у другого ресурса
	 */
	private void addPhoneNumbersOrShowWarningDialog() {
		List<PhoneNumberDto> occupiedPhoneNumbers = phoneNumbersToAdd.stream()
				.filter(this::belongsToOtherResource)
				.collect(toList());
		if (!isEmpty(occupiedPhoneNumbers)) {
			RequestContext.getCurrentInstance().execute("PF('addPhoneNumberConfirmDlg').show()");
		} else {
			addAllChosenPhoneNumbers();
			RequestContext.getCurrentInstance().execute("PF('addPhoneNumberDlg').hide()");
			RequestContext.getCurrentInstance().update("logical_resources_form");
		}
	}

	/**
	 * Добавить все выбранные номера в ресурс
	 */
	public void addAllChosenPhoneNumbers() {
		ResourceInstanceDto savedRes = resService.addPhoneNumbers(resource.getId(), phoneNumbersToAdd);
		resource.getLogicalResources().clear();
		resource.getLogicalResources().addAll(savedRes.getLogicalResources());
	}

	/**
	 * Добавить в ресурс только номера, которные не привязаны ни к каким ресурсам
	 */
	public void addOnlyFreePhoneNumbers() {
		List<PhoneNumberDto> phoneNumbersWithoutResource = phoneNumbersToAdd.stream()
				.filter(phone -> !belongsToOtherResource(phone))
				.collect(toList());
		if (!isEmpty(phoneNumbersWithoutResource)) {
			ResourceInstanceDto savedRes = resService.addPhoneNumbers(resource.getId(), phoneNumbersWithoutResource);
			resource.getLogicalResources().clear();
			resource.getLogicalResources().addAll(savedRes.getLogicalResources());
		}
	}

	/**
	 * Принадлежит ли номер какому-то другому ресурсу
	 * @param phone номер
	 * @return true, если да
	 */
	private boolean belongsToOtherResource(PhoneNumberDto phone) {
		return phone.getResource() != null && !resource.getId().equals(phone.getResource().getId());
	}
}
