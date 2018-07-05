package ru.argustelecom.box.nri.service;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.commodity.model.ServiceSpec;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;

/**
 * Модель представлений списка спецификаций услуг
 *
 * @author d.khekk
 * @since 06.10.2017
 */
@Named(value = "serviceSpecificationsVM")
@PresentationModel
public class ServiceSpecificationsViewModel extends ViewModel {

	@Inject
	private ServiceSpecificationRepository repository;

	/**
	 * Список всех спецификаций услуг
	 */
	@Getter
	private List<ServiceSpec> allSpecifications;

	/**
	 * Список выбранных типов
	 */
	@Getter
	@Setter
	private List<ServiceSpec> selectedSpecifications;

	/**
	 * Инициализация
	 */
	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
	}

	/**
	 * Получить все спецификации
	 * @return список спецификаций
	 */
	public List<ServiceSpec> getSpecifications() {
		allSpecifications = Optional.ofNullable(allSpecifications)
				.orElseGet(repository::findAll);
		return allSpecifications;
	}
}
