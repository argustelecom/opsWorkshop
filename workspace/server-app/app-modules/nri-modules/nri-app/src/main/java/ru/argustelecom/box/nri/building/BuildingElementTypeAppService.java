package ru.argustelecom.box.nri.building;

import ru.argustelecom.box.env.address.model.LocationLevel;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.box.nri.building.model.BuildingElement;
import ru.argustelecom.box.nri.building.model.BuildingElementType;
import ru.argustelecom.box.nri.building.nls.BuildingElementTypeASMessageBundle;
import ru.argustelecom.system.inf.exception.BusinessExceptionWithoutRollback;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

/**
 * Сервис для работы с типами элементов зданий
 * Created by s.kolyada on 23.08.2017.
 */
@ApplicationService
public class BuildingElementTypeAppService implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Транслятор типа элемента в дто
	 */
	@Inject
	private BuildingElementTypeDtoTranslator translator;

	/**
	 * Репозиторий доступа
	 */
	@Inject
	private BuildingElementTypeRepository repository;

	/**
	 * Репозиторий доступа к элементам строений
	 */
	@Inject
	private BuildingElementRepository buildingRepository;

	/**
	 * Найти все возможные типы элементов
	 *
	 * @return список всех типов
	 */
	public List<BuildingElementTypeDto> findAllElementTypes() {
		return repository.findAll().stream()
				.map(translator::translate)
				.sorted(comparing(BuildingElementTypeDto::getName))
				.collect(toList());
	}

	/**
	 * Найти тип элемента строения
	 *
	 * @param elementTypeId айди типа элемента
	 * @return найденный тип
	 */
	public BuildingElementTypeDto findElementType(@Nonnull Long elementTypeId) {
		return translator.translate(repository.findOne(elementTypeId));
	}

	/**
	 * Обновить тип элемента строения
	 *
	 * @param id   id типа элемента
	 * @param name Имя типа элемента
	 * @param icon иконка
	 * @return Обновленный тип элемента строения
	 */
	public BuildingElementTypeDto updateElementType(@Nonnull Long id, @Nonnull String name, BuildingElementTypeIcon icon) {
		return translator.translate(repository.update(id, name, icon));
	}

	/**
	 * Создать новый тип элемента строений
	 *
	 * @param name  Имя типа
	 * @param level Уровень из модели адресов
	 * @param icon  Иконка
	 * @return Созданный тип элемента строений
	 */
	public BuildingElementTypeDto createElementType(@Nonnull String name, LocationLevel level,
													BuildingElementTypeIcon icon) {
		return translator.translate(repository.create(name, level, icon));
	}

	/**
	 * Найти все типы строений для данного уровня
	 *
	 * @param level уровень элемента
	 * @return список типов строений
	 */
	public List<BuildingElementTypeDto> findAllByLevel(LocationLevel level) {
		if (level != null) {
			return repository.findAllByLevel(level).stream()
					.map(translator::translate).collect(toList());
		} else
			return new ArrayList<>();
	}

	/**
	 * Удалить тип элемента строения с предварительной проверкой
	 *
	 * @param elementTypeId айди типа элемента
	 * @throws BusinessExceptionWithoutRollback будет выброшено в случае использования типа
	 *                                          в каком-либо элементе строения
	 */
	public void removeElementType(@Nonnull Long elementTypeId) throws BusinessExceptionWithoutRollback {
		BuildingElementType elementType = repository.findOne(elementTypeId);
		List<BuildingElement> allByElementType = buildingRepository.findAllByElementType(elementType);
		if (isEmpty(allByElementType))
			remove(elementTypeId);
		else {
			BuildingElementTypeASMessageBundle messages = LocaleUtils.getMessages(BuildingElementTypeASMessageBundle.class);
			throw new BusinessExceptionWithoutRollback(messages.canNotDeleteType() + " " + elementType.getName()
					+messages.because());
		}
	}

	/**
	 * Удалить тип элемента строения без проверок
	 *
	 * @param elementTypeId айди типа элемента
	 */
	public void remove(@Nonnull Long elementTypeId) {
		repository.remove(elementTypeId);
	}
}
