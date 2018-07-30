package ru.argustelecom.box.env.security;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.security.model.Role;
import ru.argustelecom.box.inf.page.outcome.OutcomeConstructor;
import ru.argustelecom.box.inf.page.outcome.param.IdentifiableOutcomeParam;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Inject;
import javax.validation.constraints.Size;
import java.io.Serializable;

@PresentationModel
public class RoleCreateDialogModel implements Serializable {

	public static final String VIEW_ID = "/views/env/security/RoleListView.xhtml";

	@Inject
	private RoleRepository roleRepository;

	@Inject
	private OutcomeConstructor outcome;

	@Size(max = Role.OBJECT_NAME_LENGTH)
	@Getter
	@Setter
	private String newRoleName;

	@Size(max = Role.DESCRIPTION_LENGTH)
	@Getter
	@Setter
	private String newRoleDescription;

	public String submitNewRole() {
		Role newRole = roleRepository.createRole(newRoleName, newRoleDescription);
		clearNewRoleParams();

		return outcome.construct(RoleCardViewModel.VIEW_ID, IdentifiableOutcomeParam.of("role", newRole));
	}

	public void cancelNewRole() {
		clearNewRoleParams();
	}

	private void clearNewRoleParams() {
		newRoleName = null;
		newRoleDescription = null;
	}

	private static final long serialVersionUID = -5045604216821820184L;
}
