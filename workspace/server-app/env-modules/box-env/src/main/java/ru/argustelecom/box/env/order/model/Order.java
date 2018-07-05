package ru.argustelecom.box.env.order.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AssociationOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

import ru.argustelecom.box.env.activity.attachment.model.AttachmentContext;
import ru.argustelecom.box.env.activity.attachment.model.HasAttachments;
import ru.argustelecom.box.env.activity.comment.model.CommentContext;
import ru.argustelecom.box.env.activity.comment.model.HasComments;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.commodity.model.CommodityType;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.order.lifecycle.OrderLifecycle;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.CustomerTypeInstance;
import ru.argustelecom.box.env.party.model.CustomerTypeInstance_;
import ru.argustelecom.box.env.party.model.role.ContactPersons;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.model.role.Customer_;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.env.pricing.model.ProductOffering;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapperDef;
import ru.argustelecom.box.publang.crm.model.IOrder;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryDateFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuerySimpleFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;

@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "orders", uniqueConstraints = {
		@UniqueConstraint(name = "uc_order", columnNames = { "number", "customer_id" }) })
@EntityWrapperDef(name = IOrder.WRAPPER_NAME)
public class Order extends BusinessObject implements LifecycleObject<OrderState>, HasComments, HasAttachments {

	private static final long serialVersionUID = -6921954206660277262L;

	public static final int MAX_NUMBER_LENGTH = 32;

	@Column(nullable = false, length = MAX_NUMBER_LENGTH)
	private String number;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date dueDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date closeDate;

	@Enumerated(EnumType.STRING)
	private OrderState state;

	@Column(nullable = false, length = 32)
	@Enumerated(EnumType.STRING)
	private OrderPriority priority;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "assignee_id", nullable = false)
	private Employee assignee;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	@OneToMany(mappedBy = "order")
	private List<AbstractContract> contracts = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "connection_address_id")
	private Location connectionAddress;

	@Column(length = 512)
	private String connectionAddressComment;

	@Embedded
	//@formatter:off
	@AssociationOverride(name = "persons", joinTable = @JoinTable(name = "order_contact_persons",
			joinColumns = @JoinColumn(name = "order_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "contact_person_id", referencedColumnName = "id")))
	//@formatter:on
	private ContactPersons contactPersons = new ContactPersons();

	@Version
	private Long version;

	//@formatter:off
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(schema = "system", name = "order_commodity_type",
			joinColumns = @JoinColumn(name = "order_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "commodity_type_id", referencedColumnName = "id"))
	//@formatter:on
	private List<CommodityType> requirements = new ArrayList<>();

	//@formatter:off
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(schema = "system", name = "order_product_offering",
			joinColumns = @JoinColumn(name = "order_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "pricelist_entry_id", referencedColumnName = "id"))
	//@formatter:on
	private List<ProductOffering> offers = new ArrayList<>();

	@OneToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "comment_context_id", insertable = true, updatable = false)
	private CommentContext commentContext;

	@OneToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "attachment_context_id", insertable = true, updatable = false)
	private AttachmentContext attachmentContext;

	protected Order() {
	}

	public Order(Long id) {
		super(id);
		setCreationDate(new Date());
		this.commentContext = new CommentContext(id);
		this.attachmentContext = new AttachmentContext(id);
	}

	@Override
	public String getObjectName() {
		return getNumber();
	}

	public void addRequirement(CommodityType requirement) {
		requirements.add(requirement);
	}

	public void removeRequirement(CommodityType requirement) {
		requirements.remove(requirement);
	}

	public void addOffer(ProductOffering offer) {
		offers.add(offer);
	}

	public void removeOffer(ProductOffering offer) {
		offers.remove(offer);
	}

	public void addContract(AbstractContract contract) {
		contracts.add(contract);
	}

	public void removeContract(AbstractContract contract) {
		contracts.remove(contract);
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	protected void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Date getCloseDate() {
		return closeDate;
	}

	public void setCloseDate(Date closeDate) {
		this.closeDate = closeDate;
	}

	@Override
	public OrderState getState() {
		return state;
	}

	@Override
	public void setState(OrderState state) {
		this.state = state;
	}

	@Transient
	private OrderLifecycle orderLifecycle;

	public OrderPriority getPriority() {
		return priority;
	}

	public void setPriority(OrderPriority priority) {
		this.priority = priority;
	}

	public Employee getAssignee() {
		return assignee;
	}

	public void setAssignee(Employee assignee) {
		this.assignee = assignee;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<AbstractContract> getContracts() {
		return Collections.unmodifiableList(contracts);
	}

	public List<AbstractContract> getUnmodifiableContracts() {
		return Collections.unmodifiableList(contracts);
	}

	public Location getConnectionAddress() {
		return connectionAddress;
	}

	public void setConnectionAddress(Location connectionAddress) {
		this.connectionAddress = connectionAddress;
	}

	public String getConnectionAddressComment() {
		return connectionAddressComment;
	}

	public void setConnectionAddressComment(String connectionAddressComment) {
		this.connectionAddressComment = connectionAddressComment;
	}

	public ContactPersons getContactPersons() {
		return contactPersons;
	}

	private List<CommodityType> getRequirements() {
		return requirements;
	}

	public List<CommodityType> getUnmodifiableRequirements() {
		return Collections.unmodifiableList(requirements);
	}

	private List<ProductOffering> getOffers() {
		return offers;
	}

	public List<ProductOffering> getUnmodifiableOffers() {
		return Collections.unmodifiableList(offers);
	}

	@Override
	public AttachmentContext getAttachmentContext() {
		return attachmentContext;
	}

	@Override
	public CommentContext getCommentContext() {
		return commentContext;
	}

	// *****************************************************************************************************************
	// Inner classes
	// *****************************************************************************************************************

	public static class OrderQuery extends EntityQuery<Order> {

		private Join<Order, CustomerTypeInstance> customerTypeInstanceJoin;

		private EntityQueryStringFilter<Order> number = createStringFilter(Order_.number);
		private EntityQueryEntityFilter<Order, Employee> assignee = createEntityFilter(Order_.assignee);
		private EntityQueryEntityFilter<Order, Customer> customer = createEntityFilter(Order_.customer);
		private EntityQuerySimpleFilter<Order, OrderState> state = createFilter(Order_.state);
		private EntityQuerySimpleFilter<Order, OrderPriority> priority = createFilter(Order_.priority);
		private EntityQueryDateFilter<Order> createFrom = createDateFilter(Order_.creationDate);
		private EntityQueryDateFilter<Order> createTo = createDateFilter(Order_.creationDate);
		private EntityQueryDateFilter<Order> dueDate = createDateFilter(Order_.dueDate);

		public OrderQuery() {
			super(Order.class);
		}

		public EntityQueryStringFilter<Order> number() {
			return number;
		}

		public EntityQueryEntityFilter<Order, Employee> assignee() {
			return assignee;
		}

		public EntityQueryEntityFilter<Order, Customer> customer() {
			return customer;
		}

		public EntityQuerySimpleFilter<Order, OrderState> state() {
			return state;
		}

		public EntityQuerySimpleFilter<Order, OrderPriority> priority() {
			return priority;
		}

		public EntityQueryDateFilter<Order> createFrom() {
			return createFrom;
		}

		public EntityQueryDateFilter<Order> createTo() {
			return createTo;
		}

		public EntityQueryDateFilter<Order> dueDate() {
			return dueDate;
		}

		public Predicate byCustomerType(CustomerType customerType) {
			return criteriaBuilder().equal(customerTypeJoin().get(CustomerTypeInstance_.type),
					createParam(CustomerTypeInstance_.type, customerType));
		}

		public Join<Order, CustomerTypeInstance> customerTypeJoin() {
			if (customerTypeInstanceJoin == null)
				customerTypeInstanceJoin = root().join(Order_.customer).join(Customer_.typeInstance.getName());
			return customerTypeInstanceJoin;
		}

	}

}