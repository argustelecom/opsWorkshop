package ru.argustelecom.box.nri.logicalresources.ip.address;

import ru.argustelecom.box.env.BaseEQConvertibleDtoFilterModel;
import ru.argustelecom.box.nri.logicalresources.ip.address.lifecycle.IPAddressState;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddress;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Inject;
import javax.persistence.criteria.Predicate;
import java.util.function.Supplier;

/**
 * Создает запрос и строит предикаты для получения списка IP-адресов
 *
 * @author d.khekk
 * @since 11.12.2017
 */
@PresentationModel
public class IPAddressListFilterModel extends BaseEQConvertibleDtoFilterModel<IPAddress.IPAddressQuery> {

	private static final long serialVersionUID = 1L;

	/**
	 * Состояние вьюхи
	 */
	@Inject
	private IPAddressesViewState viewState;

	/**
	 * Запрос
	 */
	private IPAddress.IPAddressQuery ipAddressQuery = new IPAddress.IPAddressQuery();

	@Override
	public void buildPredicates(IPAddress.IPAddressQuery query) {
		query.criteriaQuery().where(query.state().notEqual(IPAddressState.DELETED));

		Predicate pName = query.name().likeIgnoreCase(nullsafeLike(viewState.getName()));
		Predicate pState = query.state().equal(viewState.getState());
		Predicate pPurpose = query.purpose().equal(viewState.getPurpose());
		Predicate pIsStatic = query.isStatic().equal(viewState.getIsStatic());
		Predicate pIsPrivate = query.isPrivate().equal(viewState.getIsPrivate());
		Predicate pTransferType = query.transferType().equal(viewState.getTransferType());
		Predicate pSubnet = query.subnet().equal(viewState.getSubnet());

		addPredicate(pName);
		addPredicate(pState);
		addPredicate(pPurpose);
		addPredicate(pIsStatic);
		addPredicate(pIsPrivate);
		addPredicate(pTransferType);
		addPredicate(pSubnet);
	}

	@Override
	public IPAddress.IPAddressQuery getEntityQuery(boolean isNew) {
		if (isNew) {
			ipAddressQuery = new IPAddress.IPAddressQuery();
			return ipAddressQuery;
		} else
			return ipAddressQuery;
	}

	@Override
	public Supplier<IPAddress.IPAddressQuery> entityQuerySupplier() {
		return IPAddress.IPAddressQuery::new;
	}

	/**
	 * null-safe like
	 *
	 * @param value значение
	 * @return значение. подходящее для запроса like, либо null если на входе ничего не было
	 */
	private String nullsafeLike(String value) {
		return value == null ? null : "%" + value + "%";
	}
}
