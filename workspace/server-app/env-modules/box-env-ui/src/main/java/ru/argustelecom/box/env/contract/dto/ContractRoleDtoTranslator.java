package ru.argustelecom.box.env.contract.dto;

import java.util.List;
import java.util.stream.Collectors;

import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class ContractRoleDtoTranslator implements DefaultDtoTranslator<ContractRoleDto, PartyRole> {

	@Override
	public ContractRoleDto translate(PartyRole partyRole) {
		return new ContractRoleDto(partyRole.getId(), partyRole.getObjectName());
	}

	public List<ContractRoleDto> translate(List<PartyRole> partyRoles) {
		return partyRoles.stream().map(this::translate).collect(Collectors.toList());
	}

}
