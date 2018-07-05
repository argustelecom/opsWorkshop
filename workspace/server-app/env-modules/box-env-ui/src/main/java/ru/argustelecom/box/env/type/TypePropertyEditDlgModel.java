package ru.argustelecom.box.env.type;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.measure.model.MeasureUnit;
import ru.argustelecom.box.env.type.model.SupportUniqueProperty;
import ru.argustelecom.box.env.type.model.TypeProperty;
import ru.argustelecom.box.env.type.model.TypePropertyGroup;
import ru.argustelecom.box.env.type.model.TypePropertyRef;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.dataaccess.cache.DirectoryCacheService;
import ru.argustelecom.system.inf.page.PresentationModel;

import static ru.argustelecom.box.env.type.model.TypeProperty.SUPPORT_UNIQUE_PROPERTY_CLASSES;

@Named(value = "typePropertyEditDm")
@PresentationModel
public class TypePropertyEditDlgModel implements Serializable {

	@Inject
	private CurrentType currentType;

	@Inject
	private TypeFactory tf;

	@Inject
	private DirectoryCacheService directoryCacheService;

	@Setter
	private Callback<Pair<TypePropertyGroup, TypeProperty<?>>> editPropertyCallback;

	@Getter
	private TypeProperty<?> property;

	@Getter
	@Setter
	private TypePropertyGroup selectedGroup;

	@Getter
	private OrdinalWrapper wrapper = new OrdinalWrapper();

	@Getter
	@Setter
	private boolean unique;

	private List<TypePropertyGroup> groupsWithoutCurrentPropertyGroup;

	public void clear() {
		property = null;
		selectedGroup = null;
		groupsWithoutCurrentPropertyGroup = null;
		unique = false;
	}

	public Collection<MeasureUnit> getPossibleMeasureUnits() {
		return directoryCacheService.getDirectoryObjects(MeasureUnit.class);
	}

	public void setProperty(TypeProperty<?> property) {
		this.property = property;
		wrapper.setOrdinal(property);
		unique = property.isUnique();
	}

	public void onEdit() {
		Pair<TypePropertyGroup, TypeProperty<?>> updatePair = ImmutablePair.of(property.getGroup(), property);
		if (!Objects.equals(selectedGroup, property.getGroup())) {
			currentType.getValue().moveProperties(property.getGroup(), selectedGroup, Lists.newArrayList(property));
		}
		if (SUPPORT_UNIQUE_PROPERTY_CLASSES.contains(property.getClass())) {
			if (unique) {
				tf.makePropertyUnique(currentType.getValue().getClass(), property);
				property.setDefaultValue(null);
			} else {
				tf.unmakePropertyUnique(currentType.getValue().getClass(), property);
			}
		}
		editPropertyCallback.execute(updatePair);
		clear();
	}

	public List<TypePropertyGroup> getGroups() {
		if (groupsWithoutCurrentPropertyGroup == null) {
			groupsWithoutCurrentPropertyGroup = Lists.newArrayList(currentType.getValue().getPropertyGroups());
			groupsWithoutCurrentPropertyGroup.remove(property.getGroup());
		}
		return groupsWithoutCurrentPropertyGroup;
	}

	public List<String> completelookupEntriesTheme() {
		return Collections.emptyList();
	}

	public String getSpecificParamsBlockStyle() {
		return TypePropertyRef.forClass(property.getClass()).toString();
	}

	private static final long serialVersionUID = 1L;
}
