package ru.argustelecom.box.nri.resources;

import org.apache.commons.lang3.tuple.Pair;
import ru.argustelecom.box.env.BaseEQConvertibleDtoFilterModel;
import ru.argustelecom.box.env.address.LocationAppService;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.address.model.Location_;
import ru.argustelecom.box.nri.building.model.BuildingElement;
import ru.argustelecom.box.nri.building.model.BuildingElement_;
import ru.argustelecom.box.nri.coverage.model.ResourceInstallation_;
import ru.argustelecom.box.nri.resources.model.ParameterValue;
import ru.argustelecom.box.nri.resources.model.ParameterValue_;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;
import ru.argustelecom.box.nri.resources.model.ResourceInstance_;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Создает запрос и строит предикаты для получения списка ресурсов
 * @author a.wisniewski
 * @since 11.10.2017
 */
@PresentationModel
public class ResourceInstanceListFilterModel
		extends BaseEQConvertibleDtoFilterModel<ResourceInstance.ResourceInstanceQuery> {

	private static final long serialVersionUID = -3476855218729538611L;

	/**
	 * состояние вьюхи
	 */
	@Inject
	private ResourcesViewState viewState;

	/**
	 * сервис адресов
	 */
	@Inject
	private LocationAppService locationService;

	/**
	 * запрос
	 */
	private ResourceInstance.ResourceInstanceQuery resourceQuery = new ResourceInstance.ResourceInstanceQuery();

	@Override
	public void buildPredicates(ResourceInstance.ResourceInstanceQuery query) {
		// выбираем только то, что нам нужно, иначе хибернейт наплодит дополнительных запросов для ненужных данных
		query.criteriaQuery().multiselect(
				query.root().get(ResourceInstance_.id),
				query.root().get(ResourceInstance_.name),
				query.root().get(ResourceInstance_.specification),
				query.root().get(ResourceInstance_.status));

		List<Location> locations = locationService.getLocationsLike(viewState.getLocationString(), 1);
		Long locationId = locations.isEmpty() ? null : locations.get(0).getId();

		List<Pair<Long, String>> paramValues = viewState.getParamDescriptors().stream()
				.filter(desc -> desc.getParamSpec() != null && desc.getParamValue() != null)
				.map(desc -> Pair.of(desc.getParamSpec().getId(), desc.getParamValue()))
				.collect(toList());

		Predicate pIndependent = query.independent().isTrue();
		Predicate pName = query.name().likeIgnoreCase(nullsafeLike(viewState.getName()));
		Predicate pSpec = query.spec().likeIgnoreCase(nullsafeLike(viewState.getSpecificationName()));
		Predicate pResStatus = query.status().equal(viewState.getResourceStatus());
		Predicate pLocation = locationContainsResource(locationId, query);
		Predicate pContainsParams = resourceContainsParams(paramValues, query);

		Stream.of(pIndependent, pName, pSpec, pResStatus, pLocation, pContainsParams).forEach(this::addPredicate);
	}

	@Override
	public ResourceInstance.ResourceInstanceQuery getEntityQuery(boolean isNew) {
		if (isNew) {
			resourceQuery = new ResourceInstance.ResourceInstanceQuery();
			return resourceQuery;
		} else return resourceQuery;
	}

	@Override
	public Supplier<ResourceInstance.ResourceInstanceQuery> entityQuerySupplier() {
		return ResourceInstance.ResourceInstanceQuery::new;
	}

	/**
	 * проверяет ресурс на содержание заданных параметров с заданными значениями
	 * @param paramValues пары "id спецификации параметра - значение"
	 * @param query query
	 * @return предикат
	 */
	private Predicate resourceContainsParams(List<Pair<Long, String>> paramValues, ResourceInstance.ResourceInstanceQuery query) {
		if (isEmpty(paramValues))
			return null;

		CriteriaBuilder cb = query.criteriaBuilder();
		Root<ResourceInstance> resource = query.root();

		List<Predicate> paramsExists = new ArrayList<>();
		for (Pair<Long, String> paramValue : paramValues) {
			Subquery<ParameterValue> subquery = query.criteriaQuery().subquery(ParameterValue.class);
			Root<ParameterValue> resParam = subquery.from(ParameterValue.class);

			Predicate paramLikeThis_in_ResLikeThis = cb.and(
					cb.equal(resource, resParam.get(ParameterValue_.resource)),
					cb.equal(resParam.get(ParameterValue_.specification).get(ParameterValue_.id), paramValue.getKey()),
					cb.equal(resParam.get(ParameterValue_.value), paramValue.getValue()));

			paramsExists.add(cb.exists(subquery.select(resParam).where(paramLikeThis_in_ResLikeThis)));
		}

		return cb.and(paramsExists.toArray(new Predicate[0]));
	}

	/**
	 * Создает предикат, проверяющий, находится ли ресурс по указанному адресу
	 * @param locationId id Location'а, в котором будем искать ресурс
	 * @param query запрос ресурса.
	 * @return предикат
	 */
	private Predicate locationContainsResource(Long locationId, ResourceInstance.ResourceInstanceQuery query) {

		if (locationId == null)
			return null;

		CriteriaBuilder cb = query.criteriaBuilder();
		Root<ResourceInstance> root = query.root();
		From<BuildingElement, Location> location = root
				.join(ResourceInstance_.installation, JoinType.LEFT)
				.join(ResourceInstallation_.installedAt, JoinType.LEFT)
				.join(BuildingElement_.location, JoinType.LEFT);

		Predicate pLocation = cb.equal(location.get(Location_.id), locationId);
		From<Location, Location> parentLocation = location.join(Location_.parent);

		// допускаем вложение Location'ов не больше 6 раз
		for (int i = 0; i < 6; i++) {
			Predicate parentEqual = cb.equal(parentLocation.get(Location_.id), locationId);
			pLocation = cb.or(pLocation, parentEqual);
			parentLocation = parentLocation.join(Location_.parent, JoinType.LEFT);
		}
		return pLocation;
	}


	/**
	 * null-safe like
	 * @param value значение
	 * @return значение. подходящее для запроса like, либо null если на входе ничего не было
	 */
	private String nullsafeLike(String value) {
		return value == null ? null : "%" + value + "%";
	}
}
