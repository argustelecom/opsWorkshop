package ru.argustelecom.box.env.party;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.party.model.Party;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.party.model.role.Supplier;
import ru.argustelecom.box.inf.service.ApplicationService;

/**
 * Репозиторий для работы с {@linkplain Owner юридическими лицами компании}.
 */
@ApplicationService
public class SupplierAppService implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private SupplierRepository supplierRp;

	@Inject
	private OwnerRepository ownerRp;

	/**
	 * Возвращает всех внешних-операторов - контрагентов.
	 */
	public List<Supplier> findAll() {
		return supplierRp.findAll();
	}

	/**
	 * Возращает всех, кто может быть поставщиком: {@linkplain Supplier внешние-операторы} + {@linkplain Owner юр. лица
	 * компании}.
	 */
	public List<PartyRole> findAllPossibleSuppliers() {
		List<PartyRole> possibleSuppliers = new ArrayList<>();
		possibleSuppliers.addAll(findAll());
		possibleSuppliers.addAll(ownerRp.findAll());
		return possibleSuppliers;
	}

	/**
	 * Ищет поставщика по {@linkplain Party компании}.
	 *
	 * @param party
	 *            компания, которая является поставщиком.
	 * @return поставщика, в случае если по данной компании поставщик не найден - null
	 */
	public Supplier find(Party party) {
		return supplierRp.find(party);
	}

	/**
	 * Ищет поставщиков по имени компании.
	 *
	 * @param legalName
	 *            имя компании.
	 * @return коллекция поставщиков, в случае, если по переданному имени, поставщии не найдены - пустая коллекция
	 */
	public List<Supplier> find(String legalName) {
		return supplierRp.find(legalName);
	}

	/**
	 * Создаёт поставщика.
	 *
	 * @param legalName
	 *            официальное название компании поставщика.
	 * @param partyTypeId
	 *            идентификатор типа компании поставщика. Поставщика можно создать только с тем типом, который относится
	 *            к категории {@link PartyCategory#COMPANY}
	 */
	public Supplier create(String legalName, String brandName, Long partyTypeId) {
		checkNotNull(legalName);
		checkNotNull(brandName);
		checkNotNull(partyTypeId);
		return supplierRp.create(legalName, brandName, partyTypeId);
	}

	/**
	 * Удаляет поставщика.
	 *
	 * @param supplierId
	 *            идентификатор постащика, который должнен быть удален.
	 */
	public void remove(Long supplierId) {
		checkNotNull(supplierId);

		Supplier supplier = em.find(Supplier.class, supplierId);
		checkNotNull(supplier);

		em.remove(supplier);
	}

	/**
	 * Меняет официальное название компании поставщика
	 *
	 * @param supplierId
	 *            идентификатор поставщика
	 *
	 * @param name
	 *            официальное название компании поставщика.
	 */
	public void changeLegalName(Long supplierId, String name) {
		checkNotNull(supplierId);
		checkNotNull(name);

		Supplier supplier = em.find(Supplier.class, supplierId);
		checkNotNull(supplier);

		if (!name.equals(supplier.getParty().getLegalName())) {
			supplier.getParty().setLegalName(name);
		}
	}

	/**
	 * Меняет официальное название бренда компании поставщика
	 *
	 * @param supplierId
	 *            идентификатор поставщика
	 *
	 * @param name
	 *            название бренда компании поставщика.
	 */
	public void changeBrandName(Long supplierId, String name) {
		checkNotNull(supplierId);
		checkNotNull(name);

		Supplier supplier = em.find(Supplier.class, supplierId);
		checkNotNull(supplier);

		if (!name.equals(supplier.getParty().getBrandName())) {
			supplier.getParty().setBrandName(name);
		}
	}

	private static final long serialVersionUID = 6663491540877357648L;
}