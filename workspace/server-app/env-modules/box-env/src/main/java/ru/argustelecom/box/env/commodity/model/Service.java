package ru.argustelecom.box.env.commodity.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.criteria.Join;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.contract.model.AbstractContract_;
import ru.argustelecom.box.env.contract.model.ContractEntry_;
import ru.argustelecom.box.env.contract.model.Contract_;
import ru.argustelecom.box.env.contract.model.ProductOfferingContractEntry;
import ru.argustelecom.box.env.contract.model.ProductOfferingContractEntry_;
import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.CustomerTypeInstance_;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.model.role.Customer_;
import ru.argustelecom.box.env.pricing.model.ProductOffering_;
import ru.argustelecom.box.env.product.model.AbstractProductType;
import ru.argustelecom.box.env.type.model.IndexTable;
import ru.argustelecom.box.env.type.model.InstanceTable;
import ru.argustelecom.box.env.type.model.TypeInstanceDescriptor;
import ru.argustelecom.box.env.type.model.TypeInstanceUniqueListener;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryNumericFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuerySimpleFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;

/**
 * Сущность описывающая экземпляр услуги.
 */
@Entity
@Access(AccessType.FIELD)
@EntityListeners(TypeInstanceUniqueListener.class)
//@formatter:off
@TypeInstanceDescriptor(
		indexTable = @IndexTable(
				schema = "system",
				table = "service_property_index"
		),
		instanceTable = @InstanceTable(
				schema = "system",
				table = "commodity"
		)
)
//@formatter:on
public class Service extends Commodity<ServiceType, ServiceSpec> implements LifecycleObject<ServiceState> {

	private static final long serialVersionUID = 7918044013147050699L;

	/**
	 * Состояние услуги.
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 64)
	private ServiceState state;

	/**
	 * Основание, по которому данная услуга предоставляется.
	 */
	@Getter
	@Setter
	@ManyToOne(targetEntity = ProductOfferingContractEntry.class, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "subject_id", nullable = false)
	private ProductOfferingContractEntry subject;

	protected Service() {
		super();
	}

	protected Service(Long id) {
		super(id);
	}

	@SuppressWarnings("rawtypes")
	public static class ServiceQuery<I extends Service> extends CommodityQuery<ServiceType, ServiceSpec, I> {

		private EntityQueryNumericFilter<I, Long> id;
		private EntityQueryEntityFilter<I, CommodityType> serviceType;
		private EntityQuerySimpleFilter<I, ServiceState> state;
		private EntityQueryEntityFilter<I, ProductOfferingContractEntry> subject;
		private EntityQueryEntityFilter<I, AbstractProductType> productType;
		private EntityQueryEntityFilter<I, AbstractContract> contract;
		private EntityQueryStringFilter<I> contractNumber;
		private EntityQueryEntityFilter<I, CustomerType> customerType;
		private EntityQueryEntityFilter<I, Customer> customer;
		private Join<ProductOfferingContractEntry, AbstractContract> contractEntryContractJoin;

		public ServiceQuery(Class<I> entityClass) {
			super(entityClass);
			id = createNumericFilter(Service_.id);
			serviceType = createEntityFilter(Service_.type);
			state = createFilter(Service_.state);
			subject = createEntityFilter(Service_.subject);
			productType = createEntityFilter(root().join(Service_.subject)
					.join(ProductOfferingContractEntry_.productOffering).join(ProductOffering_.productType),
					ProductOffering_.productType);
			contract = createEntityFilter(contractEntryContractJoin(), ContractEntry_.contract);
			contractNumber = createStringFilter(contractEntryContractJoin().get(AbstractContract_.documentNumber),
					AbstractContract_.documentNumber);
			customerType = createEntityFilter(contractEntryContractJoin().join(Contract_.customer)
					.join(Customer_.typeInstance).join(CustomerTypeInstance_.type), CustomerTypeInstance_.type);
			customer = createEntityFilter(contractEntryContractJoin().join(AbstractContract_.customer),
					AbstractContract_.customer);
		}

		public EntityQueryNumericFilter<I, Long> id() {
			return id;
		}

		public EntityQueryEntityFilter<I, CommodityType> serviceType() {
			return serviceType;
		}

		public EntityQuerySimpleFilter<I, ServiceState> state() {
			return state;
		}

		public EntityQueryEntityFilter<I, ProductOfferingContractEntry> subject() {
			return subject;
		}

		public EntityQueryEntityFilter<I, AbstractProductType> productType() {
			return productType;
		}

		public EntityQueryEntityFilter<I, AbstractContract> contract() {
			return contract;
		}

		public EntityQueryStringFilter<I> contractNumber() {
			return contractNumber;
		}

		public EntityQueryEntityFilter<I, CustomerType> customerType() {
			return customerType;
		}

		public EntityQueryEntityFilter<I, Customer> customer() {
			return customer;
		}

		private Join<ProductOfferingContractEntry, AbstractContract> contractEntryContractJoin() {
			if (contractEntryContractJoin == null) {
				contractEntryContractJoin = root().join(Service_.subject).join(ProductOfferingContractEntry_.contract);
			}
			return contractEntryContractJoin;
		}
	}
}