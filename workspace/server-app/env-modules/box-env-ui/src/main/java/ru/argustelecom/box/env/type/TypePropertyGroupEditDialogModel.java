package ru.argustelecom.box.env.type;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.type.model.TypePropertyGroup;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
@Named("typePropertyGroupEditDm")
public class TypePropertyGroupEditDialogModel implements Serializable {

	@Inject
	private CurrentType currentType;

	@Inject
	private TypeFactory typeFactory;

	private TypePropertyGroup group;

	@Getter
	@Setter
	private TypePropertyGroupDto groupDto = new TypePropertyGroupDto();

	@Setter
	private Callback<TypePropertyGroup> addGroupCallback;

	@Setter
	private Runnable editGroupCallback;

	@Getter
	private OrdinalWrapper wrapper = new OrdinalWrapper();

	public void onAction() {
		if (isEditMode()) {
			onGroupEdit();
		} else {
			onGroupCreate();
		}
	}

	public void onGroupCreate() {
		addGroupCallback.execute(groupDto.getOrdinalNumber() == null
				? typeFactory.createPropertyGroup(currentType.getValue(), groupDto.getName())
				: typeFactory.createPropertyGroup(currentType.getValue(), groupDto.getName(),
						groupDto.getOrdinalNumber()));
		clear();
	}

	public void onGroupEdit() {
		group.setName(groupDto.getName());
		group.changeOrdinalNumber(groupDto.getOrdinalNumber());
		editGroupCallback.run();
		clear();
	}

	public void setGroup(TypePropertyGroup group) {
		this.group = group;
		groupDto.setName(group.getName());
		groupDto.setOrdinalNumber(group.getOrdinalNumber());
		wrapper.setOrdinal(group);
	}

	public void clear() {
		groupDto.setName(null);
		groupDto.setOrdinalNumber(null);
		group = null;
	}

	public boolean isEditMode() {
		return group != null;
	}

	private static final long serialVersionUID = -8545397583822860101L;
}
