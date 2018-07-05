package ru.argustelecom.box.nri.logicalresources.ip.address;

import lombok.Getter;
import ru.argustelecom.box.env.EQConvertibleDtoLazyDataModel;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddress;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddress_;
import ru.argustelecom.box.nri.logicalresources.ip.subnet.model.IPSubnet_;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * Ленивый список IP-адресов
 *
 * @author d.khekk
 * @since 11.12.2017
 */
public class IPAddressesList extends EQConvertibleDtoLazyDataModel<IPAddress, IPAddressDtoTmp, IPAddress.IPAddressQuery, IPAddressesList.IPAddressSort> {

	/**
	 * Транслятор дто
	 */
	@Getter
	@Inject
	private IPAddressDtoTranslatorTmp dtoTranslator;

	/**
	 * Фильтры
	 */
	@Getter
	@Inject
	private IPAddressListFilterModel filterModel;

	/**
	 * Сервис доступа в БД
	 */
	@PersistenceContext
	private EntityManager em;

	@PostConstruct
	private void postConstruct() {
		addPath(IPAddressSort.NAME, query -> query.root().get(IPAddress_.ipHash));
		addPath(IPAddressSort.STATE, query -> query.root().get(IPAddress_.state));
		addPath(IPAddressSort.SUBNET, query -> query.root().get(IPAddress_.subnet).get(IPSubnet_.name));
	}

	@Override
	protected Class<IPAddressSort> getSortableEnum() {
		return IPAddressSort.class;
	}

	@Override
	protected TypedQuery<IPAddress> getTypedQuery() {
		em.clear();
		return super.getTypedQuery();
	}

	/**
	 * Enum для сортировок по колонкам
	 */
	public enum IPAddressSort {
		NAME, STATE, SUBNET
	}
}
