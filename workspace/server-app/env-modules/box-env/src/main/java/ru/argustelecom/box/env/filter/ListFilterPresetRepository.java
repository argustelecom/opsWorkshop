package ru.argustelecom.box.env.filter;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;

import ru.argustelecom.box.env.filter.model.FilterParam;
import ru.argustelecom.box.env.filter.model.ListFilterPreset;
import ru.argustelecom.box.env.filter.model.ListFilterPreset.ListFilterPresetQuery;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.inf.service.Repository;

@Repository
public class ListFilterPresetRepository implements Serializable {

	private static final long serialVersionUID = 3999932190697390046L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService sequenceService;

	public ListFilterPreset create(String name, Employee owner, String page, Set<FilterParam> filterParams) {
		checkArgument(StringUtils.isNotBlank(name));
		checkArgument(owner != null);
		checkArgument(StringUtils.isNotBlank(page));
		checkArgument(filterParams != null && !filterParams.isEmpty());

		ListFilterPreset listFilterPreset = new ListFilterPreset(sequenceService.nextValue(ListFilterPreset.class),
				name, owner, page);
		listFilterPreset.setFilterParams(filterParams);
		em.persist(listFilterPreset);
		return listFilterPreset;
	}

	public ListFilterPreset findByNameAndOwner(String name, Employee owner) {
		checkArgument(StringUtils.isNotBlank(name));

		ListFilterPresetQuery query = new ListFilterPresetQuery();
		List<ListFilterPreset> result = query
				.and(query.name().equal(name), query.owner().equal(owner)).getResultList(em);
		checkState(result.size() < 2);
		return result.stream().findFirst().orElse(null);
	}

	public List<ListFilterPreset> findByPageAndOwner(String page, Employee owner) {
		checkArgument(page != null);
		checkArgument(owner != null);

		ListFilterPresetQuery query = new ListFilterPresetQuery();
		return query.and(query.page().equal(page)).and(query.owner().equal(owner)).getResultList(em);
	}

	public void save(ListFilterPreset listFilterPreset, Set<FilterParam> filterParams) {
		checkArgument(listFilterPreset != null);
		checkArgument(filterParams != null && !filterParams.isEmpty());

		listFilterPreset.setFilterParams(filterParams);
	}

	public void remove(ListFilterPreset listFilterPreset) {
		checkArgument(listFilterPreset != null);

		em.remove(listFilterPreset);
	}

	public ListFilterPreset findByNameOwnerPage(String name, Employee owner, String page) {
		checkArgument(StringUtils.isNotBlank(name));
		checkArgument(owner != null);

		ListFilterPresetQuery query = new ListFilterPresetQuery();
		query.and(query.name().equal(name), query.owner().equal(owner), query.page().equal(page));

		ListFilterPreset singleResult;
		try {
			singleResult = query.getSingleResult(em);
		} catch (NoResultException e) {
			singleResult = null;
		}

		return singleResult;
	}
}
