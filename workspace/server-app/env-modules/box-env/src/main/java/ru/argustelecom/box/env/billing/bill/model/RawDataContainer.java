package ru.argustelecom.box.env.billing.bill.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.NoArgsConstructor;

/**
 * Value Object для хранения сырых данных счёта ({@link RawDataHolder}) в формате JSON. Инкапсулирует в себе работу с
 * сохранёнными данными.
 */
@Embeddable
@Access(AccessType.FIELD)
@AttributeOverride(name = "asJson", column = @Column(name = "raw_data"))
@NoArgsConstructor
public class RawDataContainer extends BillDataContainer<RawDataHolder> {

	protected RawDataContainer(RawDataHolder dataHolder) {
		super(dataHolder);
	}

	public static RawDataContainer of(RawDataHolder data) {
		return new RawDataContainer(data);
	}

	@Override
	protected Class<RawDataHolder> getDataHolderClass() {
		return RawDataHolder.class;
	}

	private static final long serialVersionUID = 5301215641901670662L;

}