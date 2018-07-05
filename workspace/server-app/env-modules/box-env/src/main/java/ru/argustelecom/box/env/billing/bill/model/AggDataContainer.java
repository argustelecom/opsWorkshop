package ru.argustelecom.box.env.billing.bill.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@Access(AccessType.FIELD)
@AttributeOverride(name = "asJson", column = @Column(name = "agg_data"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AggDataContainer extends BillDataContainer<AggDataHolder> {

	protected AggDataContainer(AggDataHolder dataHolder) {
		super(dataHolder);
	}

	public static AggDataContainer of(AggDataHolder dataHolder) {
		return new AggDataContainer(dataHolder);
	}

	@Override
	protected Class<AggDataHolder> getDataHolderClass() {
		return AggDataHolder.class;
	}

	private static final long serialVersionUID = 847253575474896791L;
}
