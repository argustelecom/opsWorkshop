package ru.argustelecom.box.nri.tp;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.Validate;
import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.commodity.model.ServiceSpec;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.box.integration.nri.TechPossibility;
import ru.argustelecom.box.nri.building.BuildingElementRepository;
import ru.argustelecom.box.nri.building.model.BuildingElement;
import ru.argustelecom.box.nri.coverage.ResourceInstallationRepository;
import ru.argustelecom.box.nri.coverage.model.ResourceInstallation;
import ru.argustelecom.box.nri.resources.model.ParameterValue;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;
import ru.argustelecom.box.nri.resources.spec.model.ResourceSpecification;
import ru.argustelecom.box.nri.schema.ResourceSchemaRepository;
import ru.argustelecom.box.nri.schema.model.ResourceSchema;
import ru.argustelecom.box.nri.schema.requirements.resources.comparators.IParameterDataTypeComparator;
import ru.argustelecom.box.nri.schema.requirements.resources.model.RequiredItem;
import ru.argustelecom.box.nri.schema.requirements.resources.model.RequiredParameterValue;
import ru.argustelecom.box.nri.tp.nls.TechnicalPossibilityASMessagesBundle;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Сервис для определения технической возможности
 * Created by s.kolyada on 31.08.2017.
 */
@ApplicationService
public class TechnicalPossibilityAppService implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Репозиторий доступа к элементам строений
	 */
	@Inject
	private BuildingElementRepository elementRepository;

	/**
	 * Репозиторий доступа к информации об инсталляциях ресурсов
	 */
	@Inject
	private ResourceInstallationRepository installationRepository;

	/**
	 * Репозиторий к схемам подключений
	 */
	@Inject
	private ResourceSchemaRepository schemaRepository;


	/**
	 * Проверить техническую возможность(ТВ)
	 * @param serviceSpecification спецификация службы
	 * @param location расположение (квартира или дом)
	 * @return информаци о ТВ
	 */
	public TechPossibility checkPossibility(ServiceSpec serviceSpecification, Location location) {
		Validate.isTrue(location != null, LocaleUtils.getMessages(TechnicalPossibilityASMessagesBundle.class).addressIsNull());
		Validate.isTrue(serviceSpecification != null, LocaleUtils.getMessages(TechnicalPossibilityASMessagesBundle.class).serviceSpecIsNull());

		Building building;
		if (location instanceof Building) {
			building = (Building) location;
		} else {
			building = (Building) EntityManagerUtils.initializeAndUnproxy(location.getParent());
		}

		// проверяем, есть ли вообще у нас информация об элементе строения
		// с заданным адресом. если нет, значит мы не можем определеить наличие ТВ
		TechPossibility result = TechPossibility.NOT_ENOUGH_DATA;
		BuildingElement buildingStructure = elementRepository.findElementByLocation(building);
		if (buildingStructure == null) {
			return result;
		}

		// проверим, есть ли инсталляции в данном доме
		// если нет, значит по даному адресу мы не присутствуем
		result = TechPossibility.NOT_AVAILABLE;
		List<ResourceInstallation> allInstallations = installationRepository.findAllInstallationsByBuilding(buildingStructure);
		if (CollectionUtils.isEmpty(allInstallations)) {
			return result;
		}

		/**
		 * проверим, есть ли ресурсы, в зону покрытия которых входит элемент, который мы проверяем
		 * если таких нет, то сообщаем, что мы присуствуем в здании, но искомый элемент не входит в нашу заявленную
		 * зону покрытия
		 */
		result = TechPossibility.AVAILABLE_IN_BUILDING;
		List<ResourceInstallation> coveringElementInstallations
				= installationRepository.findInstallationsCoveringBuildingElement(location);
		if (CollectionUtils.isEmpty(coveringElementInstallations)) {
			return result;
		}

		// разу мы здесь, значит мы присутсвуем в здании, а так же есть некоторые ресурсы, которые покрывают
		// искомый элемент
		result = TechPossibility.COVERED_BY_SOME_RESOURCES;

		// проверка параметров ресурсов в чью зону покрытия входит искомый элемент строения

		// получаем схемы подключения для данной спецификации услуги
		// если таковых нет - то проверять нечего
		List<ResourceSchema> schemas = schemaRepository.findByServiceSpecification(serviceSpecification);
		if (CollectionUtils.isEmpty(schemas)) {
			return result;
		}

		// собираем в один список все спецификации и ресурсы, в зоне охвата которых находится проверяемый адрес
		Map<ResourceSpecification, List<ResourceInstance>> coveringResourceSpecifications = new HashMap<>();
		for (ResourceInstallation installation : coveringElementInstallations) {
			collectAllResourceSpecifications(installation.getResource(), coveringResourceSpecifications);
		}

		// верифицируем все доступные схемы подключения
		Map<ResourceSchema, ResourceSchemaVerificationResult> schemaVerificationResults = new HashMap<>();
		for (ResourceSchema schema : schemas) {
			schemaVerificationResults.put(schema, processSchema(schema, coveringResourceSpecifications));
		}

		// проверяем результат верификации схем - если есть хоть одна, которая прошла верификацию с параметрами
		// то сообщаем, что есть возможность подключения. иначе остаётся предыдущий статус - вхождение в зону охвата
		for (ResourceSchemaVerificationResult verificationResult : schemaVerificationResults.values()) {
			if (ResourceSchemaVerificationResult.PASSED_WITH_PARAMETERS.equals(verificationResult)) {
				result = TechPossibility.FULL_AVAILABILITY;
				break;
			}
		}

		return result;
	}

	/**
	 * Собрать в один список спецификации текущего ресурса и всех его дочерних ресурсов
	 * @param instance ресурс
	 * @return список всех спецификаций ресурса в рамках ресурса
	 */
	private void collectAllResourceSpecifications(ResourceInstance instance,
							Map<ResourceSpecification, List<ResourceInstance>> coveringResourceSpecifications) {
		coveringResourceSpecifications.computeIfAbsent(instance.getSpecification(), k -> new ArrayList<>())
				.add(instance);

		instance.getChildren().forEach(res -> collectAllResourceSpecifications(res, coveringResourceSpecifications));
	}

	/**
	 * Проверить схему подключения
	 * @param schema схема подключения
	 * @param coveringResourceSpecification спецификации ресурсов, в чью зону охвата мы входим
	 * @return результат проверки ТВ подкчлюения схемы
	 */
	private ResourceSchemaVerificationResult processSchema(ResourceSchema schema,
														   Map<ResourceSpecification, List<ResourceInstance>> coveringResourceSpecification) {
		ResourceSchemaVerificationResult result = ResourceSchemaVerificationResult.PASSED_WITH_PARAMETERS;
		for (RequiredItem item : schema.getRequirements()) {
			result = result.split(processRequirement(item, coveringResourceSpecification));

			if (ResourceSchemaVerificationResult.NOT_PASSED.equals(result)) {
				return result;
			}
		}
		return result;
	}

	/**
	 * Обработать конкретное требование
	 * @param requirement требование
	 * @param coveringResourceSpecifications список спецификаций, в зоне покрытия которых мы находимся
	 * @return результат проверки ТВ подкчлюения
	 */
	private ResourceSchemaVerificationResult processRequirement(RequiredItem requirement,
																Map<ResourceSpecification, List<ResourceInstance>> coveringResourceSpecifications) {
		if (!coveringResourceSpecifications.keySet().contains(requirement.getResourceSpecification())) {
			return ResourceSchemaVerificationResult.NOT_PASSED;
		}

		ResourceSchemaVerificationResult result = ResourceSchemaVerificationResult.PASSED_WITH_PARAMETERS;

		// проверить параметры ресурса
		for (ResourceInstance resource : coveringResourceSpecifications.get(requirement.getResourceSpecification())) {
			// переопределяем результат для каждого ресурса, тк иначе отрицательное значение от проверки
			// предыдущего ресурса может перекрыть успех текущего
			result = ResourceSchemaVerificationResult.PASSED_WITH_PARAMETERS;
			result = result.split(processParameterRequirements(requirement.getRequiredParameters(), resource));

			// верифицируем дочерние узлы
			for (RequiredItem subItem : requirement.getChildren()) {
				result = result.split(processSubResourcesRequirement(subItem, resource));
			}

			if (ResourceSchemaVerificationResult.PASSED_WITH_PARAMETERS.equals(result)) {
				break;
			}
		}

		return result;
	}

	/**
	 * Обработать конкретное требование
	 * @param requirement требование
	 * @param resource ресурс, который проверяется
	 * @return результат проверки ТВ подкчлюения
	 */
	private ResourceSchemaVerificationResult processSubResourcesRequirement(RequiredItem requirement,
																			ResourceInstance resource) {
		ResourceSchemaVerificationResult result = ResourceSchemaVerificationResult.PASSED_WITH_PARAMETERS;

		// найдём дочерние ресуры с заданной спецификаией
		List<ResourceInstance> subResources = findSubResourcesBySpecification(resource, requirement.getResourceSpecification());

		// если не нашлось - значит не удовлетворям требованиям
		if (CollectionUtils.isEmpty(subResources)) {
			return ResourceSchemaVerificationResult.NOT_PASSED;
		}

		// проверяем параметры
		for (ResourceInstance subRes : subResources) {
			// переопределяем результат для каждого ресурса, тк иначе отрицательное значение от проверки
			// предыдущего ресурса может перекрыть успех текущего
			result = ResourceSchemaVerificationResult.PASSED_WITH_PARAMETERS;
			result = result.split(processParameterRequirements(requirement.getRequiredParameters(), subRes));

			// если неудовлетворяем требованиям к параметрам, то нет смысла проверять дочерние узлы, поэтому выходим
			if (!ResourceSchemaVerificationResult.PASSED_WITH_PARAMETERS.equals(result)) {
				continue;
			}

			// проверка дочерних требований, если таковые имеются
			for (RequiredItem subItem : requirement.getChildren()) {
				result = result.split(processSubResourcesRequirement(subItem, subRes));

				// выходим, как только нашли подходящий по всем параметрам ресурс
				if (ResourceSchemaVerificationResult.PASSED_WITH_PARAMETERS.equals(result)) {
					break;
				}
			}
			if (ResourceSchemaVerificationResult.PASSED_WITH_PARAMETERS.equals(result)) {
				break;
			}
		}
		return result;
	}

	/**
	 * Найти все вложенные ресурсы с указанной спецификацией
	 * @param resource ресурс
	 * @param specification спецификация ресурса
	 * @return список ресурсов, удовлетворяющих спецификации
	 */
	private List<ResourceInstance> findSubResourcesBySpecification(ResourceInstance resource,
																   ResourceSpecification specification) {
		return resource.flattened()
				.filter(r -> specification.equals(r.getSpecification()))
				.collect(Collectors.toList());
	}

	/**
	 * Обработать требования к параметрам ресурса
	 * @param requiredParameterValues
	 * @param resourceInstance
	 * @return
	 */
	private ResourceSchemaVerificationResult processParameterRequirements(List<RequiredParameterValue> requiredParameterValues,
																		 ResourceInstance resourceInstance) {
		ResourceSchemaVerificationResult res = ResourceSchemaVerificationResult.PASSED_WITH_PARAMETERS;
		for (RequiredParameterValue requiredParam : requiredParameterValues) {
			ParameterValue value = resourceInstance.getParameterValues()
					.stream()
					.filter(v -> requiredParam.getParameterSpecification().equals(v.getSpecification()))
					.findFirst()
					.orElse(null);

			if (value == null) {
				return ResourceSchemaVerificationResult.PASSED;
			}

			IParameterDataTypeComparator comparator = value.getSpecification().getDataType().comparator();
			if (!comparator.compare(requiredParam.getCompareAction(), value.getValue(), requiredParam.getRequiredValue())) {
				res = ResourceSchemaVerificationResult.PASSED;
				break;
			}
		}

		return res;
	}

	/**
	 * Результат проверки возможности подключения схемы
	 */
	private enum ResourceSchemaVerificationResult {
		/**
		 * Невозможно подключить по схеме
		 */
		NOT_PASSED,

		/**
		 * Возможно подключение, без проверки параметров
		 */
		PASSED,

		/**
		 * Возможно подключение
		 */
		PASSED_WITH_PARAMETERS;

		/**
		 * Объединяет результаты проверки. Возвращается более низкое по приоритету.
		 * @param result результат
		 * @return результат 2 проверок
		 */
		public ResourceSchemaVerificationResult split(ResourceSchemaVerificationResult result) {
			return this.compareTo(result) > 0 ? result : this;
		}
	}
}
