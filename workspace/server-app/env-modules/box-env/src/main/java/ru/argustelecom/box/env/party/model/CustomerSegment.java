package ru.argustelecom.box.env.party.model;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import ru.argustelecom.box.inf.modelbase.BusinessDirectory;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;

@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "customer_segment")
public class CustomerSegment extends BusinessDirectory {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "customer_type_id", insertable = true, updatable = false)
	private CustomerType customerType;

	@Column(length = 256)
	private String description;

	protected CustomerSegment() {
	}

	public CustomerSegment(Long id, CustomerType customerType) {
		super(id);
		this.customerType = checkNotNull(customerType);
	}

	@Override
	@Access(AccessType.PROPERTY)
	@Column(name = "name", length = 64)
	public String getObjectName() {
		return super.getObjectName();
	}

	public CustomerType getCustomerType() {
		return customerType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static class CustomerSegmentQuery extends EntityQuery<CustomerSegment> {

		private EntityQueryEntityFilter<CustomerSegment, CustomerType> customerType;
		private EntityQueryStringFilter<CustomerSegment> name;

		{
			customerType = createEntityFilter(CustomerSegment_.customerType);
			name = createStringFilter(CustomerSegment_.objectName);
		}

		public CustomerSegmentQuery() {
			super(CustomerSegment.class);
		}

		public EntityQueryEntityFilter<CustomerSegment, CustomerType> customerType() {
			return customerType;
		}

		public EntityQueryStringFilter<CustomerSegment> name() {
			return name;
		}
	}

	private static final long serialVersionUID = 535366251472105288L;
}
