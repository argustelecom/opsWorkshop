package ru.argustelecom.box.env.billing.bill.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.inf.modelbase.SequenceDefinition;

@Entity
@SequenceDefinition(name = "system.gen_bill_raw_data_id")
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BillRawData extends BusinessObject {

	public BillRawData(Long id) {
		super(id);
	}

	public BillRawData(Long id, RawDataContainer rawDataContainer) {
		super(id);
		this.rawDataContainer = rawDataContainer;
	}

	@Getter
	@Embedded
	private RawDataContainer rawDataContainer;

	private static final long serialVersionUID = -2768635980316829182L;
}
