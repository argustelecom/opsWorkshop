package ru.argustelecom.box.env.type;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static ru.argustelecom.box.env.type.TypePropertyGroupDataTableModel.of;
import static ru.argustelecom.box.env.type.model.TypeProperty.SUPPORT_UNIQUE_PROPERTY_CLASSES;
import static ru.argustelecom.box.inf.nls.LocaleUtils.getMessages;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.tuple.Pair;
import org.primefaces.context.RequestContext;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.type.model.Ordinal;
import ru.argustelecom.box.env.type.model.Type;
import ru.argustelecom.box.env.type.model.TypeProperty;
import ru.argustelecom.box.env.type.model.TypePropertyGroup;
import ru.argustelecom.box.env.type.nls.TypeMessagesBundle;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.exception.BusinessException;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "typePropertyFm")
@PresentationModel
public class TypePropertyFrameModel implements Serializable {

	@Inject
	private CurrentType currentType;

	@Inject
	private TypeFactory typeFactory;

	@Getter
	@Setter
	private TypePropertyGroup to;
	@Getter
	private Type type;
	private List<TypePropertyGroupDataTableModel> models;

	public List<TypePropertyGroupDataTableModel> getModels() {
		if (currentType.getValue() != null && currentType.changed(type)) {
			this.type = currentType.getValue();
			models = type.getPropertyGroups().stream().collect(
					() -> Lists.<TypePropertyGroupDataTableModel> newArrayList(
							of(null, Lists.newArrayList(type.getPropertiesWithoutGroup()))),
					(container, group) -> addToSortedList(container,
							of(group, Lists.newArrayList(group.getProperties())), modelComparator()),
					List::addAll);
		} else if (currentType.getValue() == null) {
			clear();
		}
		return models;
	}

	public void onGroupRemove(TypePropertyGroup group) {
		checkNotNull(group);

		if (group.getProperties().isEmpty()) {
			typeFactory.removePropertyGroup(currentType.getValue(), group);
			models.remove(findModelByGroup(group));
		} else {
			TypeMessagesBundle typeMb = getMessages(TypeMessagesBundle.class);
			Notification.error(typeMb.unableToRemoveTypePropertyGroupSummary(),
					typeMb.unableToRemoveTypePropertyGroupDetail());
		}
	}

	public void onPropertiesMove(String formId) {
		TypePropertyGroupDataTableModel toModel = findModelByGroup(to);
		models.stream().filter(model -> !Objects.equals(toModel, model) && model.hasSelectedProperties())
				.forEach(fromModel -> {
					type.moveProperties(fromModel.getGroup(), toModel.getGroup(), fromModel.getSelectedProperties());
					toModel.getProperties().addAll(fromModel.getSelectedProperties());
					fromModel.getProperties().removeAll(fromModel.getSelectedProperties());
					fromModel.getSelectedProperties().clear();
					updateTable(fromModel, formId);
				});
		Ordinal.normalize(toModel.getProperties());
		updateTable(toModel, formId);
	}

	public void onPropertiesRemove(TypePropertyGroupDataTableModel model) {
		TypeProperty<?> propertyToRemove = model.getPropertyToRemove();
		boolean isMassRemove = model.getSelectedProperties().contains(propertyToRemove);
		if (isMassRemove) {
			model.getSelectedProperties().forEach(property -> typeFactory.removeProperty(type, property));
			model.getProperties().removeAll(model.getSelectedProperties());
			model.getSelectedProperties().clear();
		} else {
			typeFactory.removeProperty(type, propertyToRemove);
			model.getProperties().remove(propertyToRemove);
		}
		model.setPropertyToRemove(null);
	}

	public Callback<TypePropertyGroup> getAddGroupCallback() {
		return group -> addToSortedList(models, of(group, Lists.newArrayList(group.getProperties())),
				modelComparator());
	}

	public Runnable getEditGroupCallback() {
		return () -> models.sort(modelComparator());
	}

	public Callback<TypeProperty<?>> getAddPropertyCallback() {
		return property -> {
			TypePropertyGroupDataTableModel model = findModelByGroup(property.getGroup());
			addToSortedList(model.getProperties(), property, Comparator.comparing(TypeProperty::getOrdinalNumber));
			updateTable(model, null);
		};
	}

	public Callback<Pair<TypePropertyGroup, TypeProperty<?>>> getEditPropertyCallback() {
		return pair -> {
			TypePropertyGroup oldGroup = pair.getLeft();
			TypeProperty<?> editedProperty = pair.getRight();
			TypePropertyGroup currentGroup = editedProperty.getGroup();
			if (!Objects.equals(oldGroup, currentGroup)) {
				TypePropertyGroupDataTableModel oldGroupModel = findModelByGroup(oldGroup);
				oldGroupModel.getProperties().remove(editedProperty);
				oldGroupModel.getSelectedProperties().remove(editedProperty);
				getAddPropertyCallback().execute(editedProperty);
				updateTable(oldGroupModel, null);
			} else {
				updateTable(findModelByGroup(currentGroup), null);
			}
		};
	}

	public String supportsUniqueMessage(TypeProperty<?> property) {
		TypeMessagesBundle messages = getMessages(TypeMessagesBundle.class);
		if (SUPPORT_UNIQUE_PROPERTY_CLASSES.contains(property.getClass())) {
			return property.isUnique() ? messages.unique() : messages.notUnique();
		}
		return messages.notSupportUnique();
	}

	// FIXME Определение поддерживаемости фильтруемости свойст через TypeUtils
	// FIXME Использование в качестве сообщения unique, notUnique, notSupportUnique (переименовать ключи)
	public String supportsFilteringMessage(TypeProperty<?> property) {
		TypeMessagesBundle messages = getMessages(TypeMessagesBundle.class);
		if (TypeUtils.supportFilteringByProp(property.getType())) {
			return property.isFiltered() ? messages.unique() : messages.notUnique();
		}
		return messages.notSupportUnique();
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private <T> void addToSortedList(List<T> sortedList, T elementToAdd, Comparator<T> comparator) {
		sortedList.add(Math.abs(Collections.binarySearch(sortedList, elementToAdd, comparator) + 1), elementToAdd);
	}

	private TypePropertyGroupDataTableModel findModelByGroup(TypePropertyGroup group) {
		return models.stream().filter(currentModel -> Objects.equals(currentModel.getGroup(), group)).findFirst()
				.orElseThrow(() -> new BusinessException("Cant find model for current group"));
	}

	private static Comparator<TypePropertyGroupDataTableModel> modelComparator() {
		return Comparator.comparing(
				(TypePropertyGroupDataTableModel model) -> ofNullable(model.getGroup())
						.map(TypePropertyGroup::getOrdinalNumber).orElse(null),
				Comparator.nullsFirst(Comparator.naturalOrder()));
	}

	private void updateTable(TypePropertyGroupDataTableModel model, String formId) {
		formId = Optional.ofNullable(formId).orElse("type_property_form");
		RequestContext.getCurrentInstance().update(format("%s-property_table_%s", formId, models.indexOf(model)));
	}

	private void clear() {
		type = null;
		models = null;
	}

	private static final long serialVersionUID = 7562557645653254487L;
}