package ru.argustelecom.box.env.filter;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.Serializable;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;

import ru.argustelecom.box.env.filter.model.FilterParam;
import ru.argustelecom.box.env.filter.model.ListFilterPreset;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class ListFilterPresetAppService implements Serializable {

	private static final long serialVersionUID = 8786301716881999503L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private ListFilterPresetRepository repository;

	public ListFilterPreset createListFilterPreset(String name, Long ownerId, String page,
			Set<FilterParam> filterParams) {
		checkArgument(StringUtils.isNotBlank(name));
		checkArgument(ownerId != null);
		checkArgument(StringUtils.isNotBlank(page));
		checkArgument(filterParams != null && !filterParams.isEmpty());

		Employee owner = em.getReference(Employee.class, ownerId);
		return repository.create(name, owner, page, filterParams);
	}

	public void saveListFilterPreset(Long listFilterPresetId, Set<FilterParam> filterParams) {
		checkArgument(listFilterPresetId != null);
		checkArgument(filterParams != null && !filterParams.isEmpty());

		ListFilterPreset listFilterPreset = em.getReference(ListFilterPreset.class, listFilterPresetId);
		repository.save(listFilterPreset, filterParams);
	}

	public void removeListFilterPreset(Long listFilterPresetId) {
		checkArgument(listFilterPresetId != null);

		ListFilterPreset listFilterPreset = em.getReference(ListFilterPreset.class, listFilterPresetId);
		repository.remove(listFilterPreset);
	}

	public ListFilterPreset findByName(String name, Long ownerId, String page) {
		return repository.findByNameOwnerPage(name, em.getReference(Employee.class, ownerId), page);
	}

}
