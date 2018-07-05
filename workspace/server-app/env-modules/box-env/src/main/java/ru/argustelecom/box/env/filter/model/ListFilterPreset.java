package ru.argustelecom.box.env.filter.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.argustelecom.box.env.filter.FilterParamMapper;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;

@Entity
@Table(schema = "system")
@Access(AccessType.FIELD)
public class ListFilterPreset extends BusinessObject {

	private static final long serialVersionUID = 4275175048010419030L;

	@Column(nullable = false, length = 64)
	private String name;

	@ManyToOne
	@JoinColumn(name = "owner_id")
	private Employee owner;

	@Column(length = 64)
	private String page;

	@Type(type = "jsonb")
	private JsonNode filterParamsAsJson;

	protected ListFilterPreset() {
		super();
	}

	public ListFilterPreset(Long id, String name, Employee owner, String page) {
		super(id);
		this.name = name;
		this.owner = owner;
		this.page = page;
	}

	public Set<FilterParam> getFilterParams() {
		Set<FilterParam> filterParams = new HashSet<>();
		getRootNode().elements()
				.forEachRemaining(node -> filterParams.add(FilterParamMapper.deserialize(node.textValue())));
		return Collections.unmodifiableSet(filterParams);
	}

	public void setFilterParams(Set<FilterParam> filterParams) {
		filterParamsAsJson = JsonNodeFactory.instance.objectNode();
		ObjectNode rootNode = getRootNode();
		filterParams.forEach(filter -> {
			rootNode.put(filter.getName(), FilterParamMapper.serialize(filter));
		});
	}

	private ObjectNode getRootNode() {
		return (ObjectNode) filterParamsAsJson;
	}

	public String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	public Employee getOwner() {
		return owner;
	}

	protected void setOwner(Employee owner) {
		this.owner = owner;
	}

	public String getPage() {
		return page;
	}

	protected void setPage(String page) {
		this.page = page;
	}

	protected JsonNode getFilterParamsAsJson() {
		return filterParamsAsJson;
	}

	protected void setFilterParamsAsJson(JsonNode filterParamsAsJson) {
		this.filterParamsAsJson = filterParamsAsJson;
	}

	public static class ListFilterPresetQuery extends EntityQuery<ListFilterPreset> {

		EntityQueryStringFilter<ListFilterPreset> name = createStringFilter(ListFilterPreset_.name);
		EntityQueryStringFilter<ListFilterPreset> page = createStringFilter(ListFilterPreset_.page);
		EntityQueryEntityFilter<ListFilterPreset, Employee> owner = createEntityFilter(ListFilterPreset_.owner);

		public ListFilterPresetQuery() {
			super(ListFilterPreset.class);
		}

		public EntityQueryStringFilter<ListFilterPreset> name() {
			return name;
		}

		public EntityQueryStringFilter<ListFilterPreset> page() {
			return page;
		}

		public EntityQueryEntityFilter<ListFilterPreset, Employee> owner() {
			return owner;
		}

	}
}
