package ru.argustelecom.box.env.commodity;

import static java.util.stream.Collectors.toList;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.findList;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.commodity.model.Goods;
import ru.argustelecom.box.env.commodity.model.Goods.GoodsQuery;
import ru.argustelecom.box.env.commodity.model.GoodsSpec;
import ru.argustelecom.box.env.commodity.model.GoodsType;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.commodity.model.Service.ServiceQuery;
import ru.argustelecom.box.env.commodity.model.ServiceSpec;
import ru.argustelecom.box.env.commodity.model.ServiceState;
import ru.argustelecom.box.env.commodity.model.ServiceType;
import ru.argustelecom.box.env.contract.model.ProductOfferingContractEntry;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.type.TypeFactory;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;

/**
 * Репозиторий для работы с {@linkplain ru.argustelecom.box.env.commodity.model.Service услугами} и
 * {@linkplain ru.argustelecom.box.env.commodity.model.Goods товарами}.
 */
@Repository
public class CommodityRepository implements Serializable {

	private static final long serialVersionUID = -1275098629863258631L;

	public static final String FIND_SERVICES_BY_CUSTOMER = "CommodityRepository.findServicesByCustomer";

	@PersistenceContext
	private transient EntityManager em;

	@Inject
	private TypeFactory typeFactory;

	/**
	 * Создает независимый экземпляр услуги.
	 */
	public Service createService(ServiceType type, ProductOfferingContractEntry subject) {
		Service instance = typeFactory.createInstance(type, Service.class);

		instance.setState(ServiceState.INACTIVE);
		instance.setSubject(subject);

		em.persist(instance);
		return instance;
	}

	/**
	 * Создает экземпляр услуги по ее спецификации.
	 */
	public Service createServiceBySpec(ServiceSpec serviceSpec, ProductOfferingContractEntry subject) {
		Service instance = typeFactory.createInstanceByProto(serviceSpec, Service.class);

		instance.setState(ServiceState.INACTIVE);
		instance.setSubject(subject);

		em.persist(instance);
		return instance;
	}

	/**
	 * Создает независимый экземпляр товара.
	 */
	public Goods createGoods(GoodsType type) {
		Goods instance = typeFactory.createInstance(type, Goods.class);

		em.persist(instance);
		return instance;
	}

	/**
	 * Создает экземпляр товара по его спецификации.
	 */
	public Goods createGoodsBySpec(GoodsSpec goodsSpec) {
		Goods instance = typeFactory.createInstanceByProto(goodsSpec, Goods.class);

		em.persist(instance);
		return instance;
	}

	/**
	 * Возвращает список всех услуг.
	 */
	public List<Service> findAllServices() {
		return new ServiceQuery<>(Service.class).getResultList(em);
	}

	/**
	 * Возвращает список всех товаров.
	 */
	public List<Goods> findAllGoods() {
		return new GoodsQuery<>(Goods.class).getResultList(em);
	}

	/**
	 * Поиск экземпляров услуг по клиенту. услуг.
	 *
	 * @param customer
	 *            ссылка клиента, которому предоставляются услуги
	 *
	 * @return коллекция экземпляров найденных услуг
	 */
	//@formatter:off
	@SuppressWarnings("unchecked")
	@NamedNativeQuery(name = FIND_SERVICES_BY_CUSTOMER,
			query = "SELECT\n" +
					"  s.id\n" +
					"FROM\n" +
					"  system.commodity s,\n" +
					"  system.contract_entry ce,\n" +
					"  system.contract c\n" +
					"WHERE\n" +
					"  s.subject_id = ce.id\n" +
					"  AND ce.contract_id = c.id\n" +
					"  AND c.customer_id = :customer_id\n" +
					"  AND s.dtype = 'Service'"
	)
	//@formatter:on
	public List<Service> findBy(Customer customer) {
		List<BigInteger> ids = em.createNamedQuery(FIND_SERVICES_BY_CUSTOMER)
				.setParameter("customer_id", customer.getId()).getResultList();
		return findList(em, Service.class, ids.stream().map(BigInteger::longValue).collect(toList()));
	}

	public List<Service> findBy(Customer customer, ServiceType serviceType) {
		return findBy(customer).stream()
				.filter(service -> service.getType().getId().equals(serviceType.getId())).collect(toList());
	}

	public List<Service> findBy(ServiceType serviceType) {
		ServiceQuery<Service> query = new ServiceQuery<>(Service.class);

		query.and(query.serviceType().equal(serviceType));

		return query.getResultList(em);
	}

}