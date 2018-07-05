package ru.argustelecom.box.env.contract.model;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;
import static ru.argustelecom.box.env.contract.ContractUtils.checkCustomerForContract;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.document.model.Document;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.order.model.Order;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.report.api.Printable;
import ru.argustelecom.box.env.type.model.Type;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuerySimpleFilter;

@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "contract")
public abstract class AbstractContract<T extends AbstractContractType> extends Document<T>
		implements LifecycleObject<ContractState>, Printable {

	private static final long serialVersionUID = 4333695282908738858L;

	@OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ContractEntry> entries = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@ManyToOne(fetch = FetchType.LAZY)
	private Order order;

	@Enumerated(EnumType.STRING)
	private ContractState state;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected AbstractContract() {
		super();
	}

	/**
	 * Конструктор предназначен для инстанцирования спецификацией. Не делай этот конструктор публичным. Не делай других
	 * публичных конструкторов. Экземпляры спецификаций должны инстанцироваться сугубо спецификацией для обеспечения
	 * корректной инициализации пользовательских свойств или отношений между спецификацией и ее экземпляром.
	 * 
	 * @param id
	 *            - идентификатор экземпляра спецификации, должен быть получен при помощи соответствующего генератора
	 *            через сервис {@link IdSequenceService}
	 * 
	 * @see IdSequenceService
	 * @see Type#createInstance(Class, Long)
	 */
	protected AbstractContract(Long id) {
		super(id);
	}

	public String getFullName() {
		StringBuilder fullNameBuilder = new StringBuilder(
				format("%s %s с %s", getType().getObjectName(), getDocumentNumber(), getValidFrom()));
		if (getValidTo() != null)
			fullNameBuilder.append(format("по %s", getValidTo()));
		return fullNameBuilder.toString();
	}

	public Location getFirstAddressFromEntries() {
		List<Location> allAddresses = new ArrayList<>();
		getEntries().forEach(entry -> allAddresses.addAll(entry.getLocations()));
		allAddresses.sort(Comparator.comparing(BusinessObject::getId));
		return !allAddresses.isEmpty() ? allAddresses.get(0) : null;
	}

	@Override
	@Access(AccessType.PROPERTY)
	@ManyToOne(targetEntity = AbstractContractType.class, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "contract_type_id")
	public T getType() {
		return super.getType();
	}

	/**
	 * Возвращает клиента, для которого заключен текущий договор / дополнительное соглашение к договору
	 * 
	 * @return
	 */
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = checkCustomerForContract(this, customer);
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	@Override
	public ContractState getState() {
		return state;
	}

	@Override
	public void setState(ContractState state) {
		this.state = state;
	}

	public abstract Date getValidFrom();

	public abstract Date getValidTo();

	/**
	 * Проверяет, присутствует ли в текущем договоре указанная позиция договора
	 * 
	 * @param entry
	 *            - позиция договора
	 * 
	 * @return true, если позиция присутствует в договоре
	 */
	public boolean hasEntry(ContractEntry entry) {
		return entry != null && Objects.equals(this, entry.getContract());
	}

	/**
	 * Возвращает немодифицируемую коллекцию позиций текущего договора
	 * 
	 * @return коллекцию позиций договора или пустую коллекцию, если в договоре еще нет позиций
	 */
	public List<ContractEntry> getEntries() {
		return unmodifiableList(entries);
	}

	/**
	 * Возвращает немодифицируемую коллекцию {@linkplain ProductOfferingContractEntry позиций на основании продуктового
	 * предложения} текущего договора.
	 */
	public List<ProductOfferingContractEntry> getProductOfferingEntries() {
		return unmodifiableList(entries.stream().filter(e -> e instanceof ProductOfferingContractEntry)
				.map(e -> (ProductOfferingContractEntry) e).collect(Collectors.toList()));
	}

	/**
	 * Возвращает немодифицируемую коллекцию {@linkplain OptionContractEntry позиций на основании опции} текущего
	 * договора.
	 */
	public List<OptionContractEntry> getOptionEntries() {
		return unmodifiableList(entries.stream().filter(e -> e instanceof OptionContractEntry)
				.map(e -> (OptionContractEntry) e).collect(Collectors.toList()));
	}

	/**
	 * Возвращает модифицируемую коллекцию позиций договора. Только для внутреннего пользования
	 */
	protected List<ContractEntry> entries() {
		return entries;
	}

	/**
	 * Добавляет в договор указанную позицию, если она еще не присутствует в договоре. Возвращает true в случае
	 * успешного добавления позиции и false, если такая позиция уже пристутствует в договоре
	 * 
	 * @param entry
	 *            - позиция для добавления
	 * 
	 * @return true при успешном добавлении позиции
	 */
	public boolean addEntry(ContractEntry entry) {
		if (!hasEntry(entry)) {
			if (entry.getContract() != null) {
				entry.getContract().removeEntry(entry);
			}

			entry.setContract(this);
			entries.add(entry);
			return true;
		}
		return false;
	}

	/**
	 * Удаляет позицию из договора если такая позиция присутствует в договоре. Если удаленная позиция не будет добавлена
	 * в договор по окончании реквеста, то она будет физически удалена из системы
	 * 
	 * @param entry
	 *            - позиция для удаления
	 * 
	 * @return true при успешном удалении позиции
	 */
	public boolean removeEntry(ContractEntry entry) {
		if (hasEntry(entry)) {
			entry.setContract(null);
			entries.remove(entry);
			return true;
		}
		return false;
	}

	@Override
	public AbstractContractRdo createReportData() {
		return new AbstractContractRdo(getId(), getValidFrom(), getValidTo(), getDocumentNumber(),
				getCustomer().createReportData(), getFirstAddressFromEntries().createReportData(),
				getPropertyValueMap());
	}

	public abstract static class AbstractContractQuery<T extends AbstractContractType, I extends AbstractContract<T>>
			extends DocumentQuery<T, I> {

		private EntityQueryEntityFilter<I, Customer> customer;
		private EntityQueryEntityFilter<I, Order> order;
		private EntityQuerySimpleFilter<I, ContractState> state;
		private Join<I, T> contractTypeJoin;

		public AbstractContractQuery(Class<I> entityClass) {
			super(entityClass);
			customer = createEntityFilter(AbstractContract_.customer);
			order = createEntityFilter(AbstractContract_.order);
			state = createFilter(AbstractContract_.state);
		}

		@Override
		protected EntityQueryEntityFilter<I, ? super T> createTypeFilter() {
			return createEntityFilter(Contract_.type);
		}

		public EntityQueryEntityFilter<I, Customer> customer() {
			return customer;
		}

		public EntityQueryEntityFilter<I, Order> order() {
			return order;
		}

		public EntityQuerySimpleFilter<I, ContractState> state() {
			return state;
		}

		public Predicate byCustomerType(CustomerType customerType) {
			return criteriaBuilder().equal(contractTypeJoin().get(AbstractContractType_.customerType),
					createParam(AbstractContractType_.customerType, customerType));
		}

		public Join<I, T> contractTypeJoin() {
			if (contractTypeJoin == null) {
				contractTypeJoin = root().join(AbstractContract_.type.getName(), JoinType.INNER);
			}
			return contractTypeJoin;
		}
	}
}
