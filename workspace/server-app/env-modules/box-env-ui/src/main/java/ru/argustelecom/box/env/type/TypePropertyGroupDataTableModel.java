package ru.argustelecom.box.env.type;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.primefaces.event.ReorderEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.type.model.Ordinal;
import ru.argustelecom.box.env.type.model.TypeProperty;
import ru.argustelecom.box.env.type.model.TypePropertyGroup;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

public class TypePropertyGroupDataTableModel extends LazyDataModel<TypeProperty<?>> {

	private static final int OFFSET = 1;

	@Getter
	private TypePropertyGroup group;
	@Getter
	private List<TypeProperty<?>> properties;
	@Getter
	@Setter
	private List<TypeProperty<?>> selectedProperties = Lists.newArrayList();
	@Getter
	@Setter
	private TypeProperty<?> propertyToRemove;

	private EntityConverter converter = new EntityConverter();

	public static TypePropertyGroupDataTableModel of(TypePropertyGroup group,
			List<TypeProperty<?>> properties) {
		return new TypePropertyGroupDataTableModel(group, properties);
	}

	private TypePropertyGroupDataTableModel(TypePropertyGroup group, List<TypeProperty<?>> properties) {
		this.group = group;
		this.properties = properties;
	}

	@Override
	public List<TypeProperty<?>> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		return load(first, pageSize, Collections.singletonList(new SortMeta(null, sortField, sortOrder, null)),
				filters);
	}

	@Override
	public List<TypeProperty<?>> load(int first, int pageSize, List<SortMeta> multiSortMeta,
			Map<String, Object> filters) {
		setRowCount(properties.size());
		properties.sort(Ordinal.comparator());
		return properties;
	}

	@Override
	public TypeProperty<?> getRowData(String rowKey) {
		return properties.stream().filter(property -> Objects.equals(converter.convertToString(property), rowKey))
				.findFirst().orElse(null);
	}

	@Override
	public String getRowKey(TypeProperty<?> object) {
		return converter.convertToString(object);
	}

	public boolean hasSelectedProperties() {
		return CollectionUtils.isNotEmpty(selectedProperties);
	}

	public void onRowReorder(ReorderEvent event) {
		properties.get(event.getFromIndex()).changeOrdinalNumber(event.getToIndex() + OFFSET);
	}

	private static final long serialVersionUID = 2709049206623375305L;
}
