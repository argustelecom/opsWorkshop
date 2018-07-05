package ru.argustelecom.box.env.contract.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;

import lombok.Getter;
import ru.argustelecom.box.inf.modelbase.BusinessDirectory;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuerySimpleFilter;

/**
 * Правила соответствия участников и ролей в договоре
 * 
 * Правила устанавливают соответствие стороны договора и роли участника (PartyRole). Определяют, участники с какой ролью
 * будут доступны в договоре в качестве поставщика, агента, клиента
 *
 */

@Entity
@Access(AccessType.FIELD)
@Table(schema = "system")
@Getter
@Immutable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "ru.argustelecom.readonly-cache-region")
public class ContractRoleRules extends BusinessDirectory {

	private static final long serialVersionUID = -5010708017506321860L;

	protected ContractRoleRules() {
		super();
	}

	@Enumerated(EnumType.STRING)
	private ContractCategory contractCategory;

	@Enumerated(EnumType.STRING)
	private ContractRoleType roleType;

	@Enumerated(EnumType.STRING)
	private ContractRole role;

	public static class ContractRoleRulesQuery extends EntityQuery<ContractRoleRules> {

		EntityQuerySimpleFilter<ContractRoleRules, ContractCategory> contractCategory;
		EntityQuerySimpleFilter<ContractRoleRules, ContractRoleType> roleType;

		public ContractRoleRulesQuery() {
			super(ContractRoleRules.class);
			contractCategory = createFilter(ContractRoleRules_.contractCategory);
			roleType = createFilter(ContractRoleRules_.roleType);
		}

		public EntityQuerySimpleFilter<ContractRoleRules, ContractCategory> contractCategory() {
			return contractCategory;
		}

		public EntityQuerySimpleFilter<ContractRoleRules, ContractRoleType> roleType() {
			return roleType;
		}

	}
}
