package ru.argustelecom.box.env.commodity.telephony;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.commodity.model.CommodityTypeGroup;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionType;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionType.TelephonyOptionTypeQuery;
import ru.argustelecom.box.env.type.TypeFactory;
import ru.argustelecom.box.inf.service.Repository;

/**
 * Репозиторий для работы с {@linkplain TelephonyOptionType опциями телефонии}.
 */
@Repository
public class TelephonyOptionTypeRepository implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private TypeFactory typeFactory;

	/**
	 * Создание экземпляра типа опции телефонии.
	 */
	public TelephonyOptionType create(String name, String keyword, CommodityTypeGroup group, String description) {
		checkNotNull(name);
		checkNotNull(group);

		TelephonyOptionType instance = typeFactory.createType(TelephonyOptionType.class);

		instance.setKeyword(keyword);
		instance.changeGroup(group);
		instance.setName(name);
		instance.setDescription(description);

		em.persist(instance);

		return instance;
	}

	private static final long serialVersionUID = 3269635121805194662L;

	public List<TelephonyOptionType> findAll() {
		return new TelephonyOptionTypeQuery(TelephonyOptionType.class).getResultList(em);
	}
}