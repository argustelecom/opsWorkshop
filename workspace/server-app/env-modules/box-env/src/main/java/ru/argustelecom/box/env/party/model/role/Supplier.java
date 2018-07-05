package ru.argustelecom.box.env.party.model.role;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

import ru.argustelecom.box.env.party.model.Company;
import ru.argustelecom.box.env.party.model.Company_;
import ru.argustelecom.box.env.party.model.Party;
import ru.argustelecom.box.env.party.model.PartyRole;

/**
 * Роль поставщик - внешний-оператор связи (контрагент). Поставщик может быть создан только на основании
 * {@linkplain Company участника - организации}.
 * <p>
 * <a href="http://boxwiki.argustelecom.ru:10753/pages/viewpage.action?pageId=6294625">Описание в Confluence</a>
 * </p>
 */
@Entity
@Access(AccessType.FIELD)
public class Supplier extends PartyRole {

	protected Supplier() {
	}

	public Supplier(Long id) {
		super(id);
	}

	@Override
	public Company getParty() {
		return (Company) super.getParty();
	}

	@Override
	public void setParty(Party party) {
		checkArgument(party instanceof Company,
				format("Unsupported type of party: '%s' for supplier", party.getClass().getSimpleName()));
		super.setParty(party);
	}

	public static class SupplierQuery<T extends Supplier> extends PartyRoleQuery<T> {

		private Join<Supplier, Company> companyJoin;

		public SupplierQuery(Class<T> entityClass) {
			super(entityClass);
		}

		private Join<Supplier, Company> companyJoin() {
			if (companyJoin == null)
				companyJoin = root().join(Supplier_.party.getName());
			return companyJoin;
		}

		public Predicate byLegalName(String legalName) {
			return criteriaBuilder().like(criteriaBuilder().upper(companyJoin().get(Company_.legalName)),
					createParam(Company_.legalName, contains(legalName)));
		}

		public Predicate byBrandName(String brandName) {
			return criteriaBuilder().like(criteriaBuilder().upper(companyJoin().get(Company_.brandName)),
					createParam(Company_.brandName, contains(brandName)));
		}

		private String contains(String value) {
			return String.format("%%%s%%", value.toUpperCase().trim());
		}

	}

	private static final long serialVersionUID = 8540086345490571878L;
}