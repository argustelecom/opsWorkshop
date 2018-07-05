package ru.argustelecom.box.env.billing.bill.model;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@MappedSuperclass
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BillDataContainer<T> implements Serializable {

	private String asJson;

	protected BillDataContainer(T dataHolder) {
		setDataHolder(dataHolder);
	}

	/**
	 * Агрегированное представление сырых данных, по которым формируются расчёты для счёта.
	 */
	@Transient
	private T dataHolder;

	/**
	 * Перезаписывает данные по счёту, которые храняться в Json.
	 *
	 * @param dataHolder
	 *            враппер с данными по счёту.
	 */
	public void setDataHolder(T dataHolder) {
		this.dataHolder = dataHolder;
		asJson = DataMapper.marshal(dataHolder);
	}

	public T getDataHolder() {
		if (dataHolder == null) {
			dataHolder = DataMapper.unmarshal(asJson, getDataHolderClass());
		}
		return dataHolder;
	}

	protected abstract Class<T> getDataHolderClass();

	private static final long serialVersionUID = 6867794612355086755L;
}
