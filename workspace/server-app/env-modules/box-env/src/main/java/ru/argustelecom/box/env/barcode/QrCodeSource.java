package ru.argustelecom.box.env.barcode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class QrCodeSource {

	private List<SourceItem> items = new ArrayList<>();

	/**
	 * Добавляет значение в <b>source</b>, если значение с таким <b>keyword</b> уже есть, то перезаписывает его
	 * значения.
	 */
	public void put(String keyword, String value, boolean required) {
		if (!validParams(keyword, value))
			return;

		SourceItem item = new SourceItem(keyword, value, required);
		Optional<SourceItem> oldItem = items.stream().filter(si -> si.equals(item)).findFirst();
		if (!oldItem.isPresent())
			items.add(item);
		else {
			oldItem.get().setValue(item.getValue());
			oldItem.get().setRequired(item.isRequired());
		}
	}

	public void remove(SourceItem item) {
		items.remove(item);
	}

	/**
	 * Проверяет есть ли в <b>source</b> значение с указанным <b>keyword</b>
	 * 
	 * @param keyword
	 *            <b>keyword</b> по которому осуществляется поиск.
	 */
	public boolean contain(String keyword) {
		return items.stream().anyMatch(item -> item.getKeyword().equals(keyword));
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private boolean validParams(String keyword, String value) {
		return !(keyword == null || value == null || keyword.isEmpty() || value.isEmpty());
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public List<SourceItem> getItems() {
		return Collections.unmodifiableList(items);
	}

	// *****************************************************************************************************************
	// Inner classes
	// *****************************************************************************************************************

	public class SourceItem {

		private String keyword;
		private String value;
		private boolean required;

		public SourceItem(String keyword, String value, boolean required) {
			//FIXME сделать decode (Apache Commons, Google Guava ???)
			this.keyword = keyword.trim();
			this.value = value.trim();
			this.required = required;
		}

		public String getKeyword() {
			return keyword;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value.trim();
		}

		public boolean isRequired() {
			return required;
		}

		public void setRequired(boolean required) {
			this.required = required;
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder().append(getKeyword()).toHashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null || getClass() != obj.getClass())
				return false;

			SourceItem other = (SourceItem) obj;
			return new EqualsBuilder().append(this.getKeyword(), other.getKeyword()).isEquals();
		}

	}

}