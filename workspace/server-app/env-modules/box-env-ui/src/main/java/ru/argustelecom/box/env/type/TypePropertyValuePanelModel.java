package ru.argustelecom.box.env.type;

import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsFirst;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static javax.faces.application.FacesMessage.SEVERITY_ERROR;
import static ru.argustelecom.box.inf.nls.LocaleUtils.getMessages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.type.model.SupportUniqueProperty;
import ru.argustelecom.box.env.type.model.TypeInstance;
import ru.argustelecom.box.env.type.model.TypeInstanceDescriptor;
import ru.argustelecom.box.env.type.model.TypeProperty;
import ru.argustelecom.box.env.type.model.TypePropertyAccessor;
import ru.argustelecom.box.env.type.model.TypePropertyGroup;
import ru.argustelecom.box.env.type.nls.TypeMessagesBundle;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
@Named(value = "typePropertyValuePm")
public class TypePropertyValuePanelModel implements Serializable {

	private static final long serialVersionUID = -1366373353714525533L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private TypeFactory tf;

	@Inject
	private TypeInstanceService typeInstanceSrv;

	private transient Map<TypeInstance<?>, List<TypePropertyGroupPanelDto>> accessorsCache = new HashMap<>();

	public boolean hasAccessors(TypeInstance<?> instance) {
		List<TypePropertyGroupPanelDto> accessors = getAccessors(instance);
		return accessors != null && !accessors.isEmpty();
	}

	public List<TypePropertyGroupPanelDto> getAccessors(TypeInstance<?> instance) {
		List<TypePropertyGroupPanelDto> result = accessorsCache.get(instance);
		if (result == null) {
			result = createAndCache(instance);
		}
		return result;
	}

	public List<TypePropertyAccessor<?>> getAccessorsWithoutGroupSeparation(TypeInstance<?> instance) {

		if (instance == null) {
			return Collections.emptyList();
		}

		List<TypePropertyGroupPanelDto> groups = getAccessors(instance);
		List<TypePropertyAccessor<?>> result = new ArrayList<>();

		for (TypePropertyGroupPanelDto group : groups) {
			result.addAll(group.getAccessors());
		}

		return result;
	}

	public TypePropertyGroupPanelDto getAccessors(TypeInstance<?> instance, TypePropertyGroup group) {
		return getAccessors(instance).stream().filter(groupDto -> group.getId().equals(groupDto.getId())).findFirst()
				.orElse(null);
	}

	private List<TypePropertyGroupPanelDto> createAndCache(TypeInstance<?> instance) {
		List<TypePropertyAccessor<?>> accessors = tf.createAccessors(instance);

		Map<TypePropertyGroup, List<TypePropertyAccessor<?>>> groupAccessorsMap = groupAccessors(accessors);
		List<TypePropertyGroupPanelDto> result = createTypePropertyGroupDto(accessors, groupAccessorsMap);
		accessorsCache.put(instance, result);

		return result;
	}

	private List<TypePropertyGroupPanelDto> createTypePropertyGroupDto(List<TypePropertyAccessor<?>> accessors,
			Map<TypePropertyGroup, List<TypePropertyAccessor<?>>> groupAccessorsMap) {
		List<TypePropertyGroupPanelDto> result = groupAccessorsMap.keySet().stream()
				.map(createGroupDto(groupAccessorsMap)).collect(toList());
		result.add(new TypePropertyGroupPanelDto(getAccessorsWithoutGroup(accessors)));
		result.sort(comparing(TypePropertyGroupPanelDto::getOrdinalNumber, nullsFirst(Comparator.naturalOrder())));
		return result;
	}

	private List<TypePropertyAccessor<?>> getAccessorsWithoutGroup(List<TypePropertyAccessor<?>> accessors) {
		return accessors.stream().filter(a -> a.getProperty().getGroup() == null)
				.sorted(comparing(a -> a.getProperty().getOrdinalNumber())).collect(toList());
	}

	private Function<TypePropertyGroup, TypePropertyGroupPanelDto> createGroupDto(
			Map<TypePropertyGroup, List<TypePropertyAccessor<?>>> groupAccessorsMap) {
		return key -> new TypePropertyGroupPanelDto(key.getId(), key.getObjectName(), key.getOrdinalNumber(),
				groupAccessorsMap.get(key));
	}

	private Map<TypePropertyGroup, List<TypePropertyAccessor<?>>> groupAccessors(
			List<TypePropertyAccessor<?>> accessors) {

		Map<TypePropertyGroup, List<TypePropertyAccessor<?>>> result = accessors.stream()
				.filter(accessor -> accessor.getProperty().getGroup() != null)
				.collect(groupingBy(accessor -> accessor.getProperty().getGroup()));

		result.values().forEach(v -> v.sort(comparing(a -> a.getProperty().getOrdinalNumber())));
		return result;
	}

	@SuppressWarnings("unchecked")
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		TypePropertyAccessor<?> accessor = (TypePropertyAccessor<?>) component.getAttributes()
				.get(AttributeKey.accessor.name());
		TypeProperty<?> property = accessor.getProperty();
		TypeInstance<?> instance = accessor.getInstance();
		// Необходимо для загрузки изменений, если уникальность свойства было изменено в параллельном Conversation (в
		// другой вкладке)
		em.refresh(property);

		boolean supports = instance.getType().getClass().isAnnotationPresent(SupportUniqueProperty.class)
				&& instance.getClass().isAnnotationPresent(TypeInstanceDescriptor.class);
		boolean isValueUnique = supports && typeInstanceSrv.isValueUnique(instance, property, value);

		if (supports && property.isUnique() && !isValueUnique) {
			TypeMessagesBundle messages = getMessages(TypeMessagesBundle.class);
			String summary = messages.isPropertyValueUniqueSummary();
			String detail = messages.isPropertyValueUniqueDetail(property.getObjectName());
			throw new ValidatorException(new FacesMessage(SEVERITY_ERROR, summary, detail));
		}
	}

	private enum AttributeKey {
		accessor
	}
}