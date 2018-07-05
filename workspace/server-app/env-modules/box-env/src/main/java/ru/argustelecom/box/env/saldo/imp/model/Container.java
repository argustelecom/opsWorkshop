package ru.argustelecom.box.env.saldo.imp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Container {

	private ResultType type;
	private Set<ItemError> errors = new HashSet<>();
	private List<RegisterItem> items = new ArrayList<>();

	public Container(ResultType type, Set<ItemError> errors) {
		this.type = type;
		this.errors = errors;
	}

	public Container(ResultType type, List<RegisterItem> items, Set<ItemError> errors) {
		this.type = type;
		this.items = items;
		this.errors = errors;
	}

	public String getErrorsDescription() {
		StringBuilder description = new StringBuilder();
		errors.forEach(error -> description.append(error.getDescription()).append(". "));
		return description.toString();
	}

	public void addItem(RegisterItem item) {
		items.add(item);
	}

	public void removeItem(RegisterItem item) {
		items.remove(item);
	}

	public List<String> getInitialData() {
		return items.stream().map(RegisterItem::getRowData).collect(Collectors.toList());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		Container other = (Container) obj;
		return new EqualsBuilder().append(this.getType(), other.getType()).append(this.getErrors(), other.getErrors())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getType()).append(getErrors()).toHashCode();
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public ResultType getType() {
		return type;
	}

	public Set<ItemError> getErrors() {
		return Collections.unmodifiableSet(errors);
	}

	public List<RegisterItem> getItems() {
		return Collections.unmodifiableList(items);
	}

}