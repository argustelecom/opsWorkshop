package ru.argustelecom.box.env.contract.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.document.model.DocumentType;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;

//@formatter:off
@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "contract_type")
@AssociationOverrides({
	@AssociationOverride(
		name = "templates", 
		joinTable = @JoinTable(
			schema = "system", 
			name = "contract_templates", 
			joinColumns = @JoinColumn(name = "contract_type_id"),
			inverseJoinColumns = @JoinColumn(name = "contract_template_id")
		)
	) 
})//@formatter:on
public abstract class AbstractContractType extends DocumentType {

	private static final long serialVersionUID = 4877774641668504398L;

	@Getter
	@Setter
	@ManyToOne(optional = false)
	@JoinColumn(name = "customer_type_id")
	private CustomerType customerType;

	protected AbstractContractType() {
		super();
	}

	protected AbstractContractType(Long id) {
		super(id);
	}

	public static class AbstractContractTypeQuery<T extends AbstractContractType> extends TypeQuery<T> {

		private EntityQueryEntityFilter<T, CustomerType> customerType;

		public AbstractContractTypeQuery(Class<T> entityClass) {
			super(entityClass);
			customerType = createEntityFilter(AbstractContractType_.customerType);
		}

		public EntityQueryEntityFilter<T, CustomerType> customerType() {
			return customerType;
		}
	}
}