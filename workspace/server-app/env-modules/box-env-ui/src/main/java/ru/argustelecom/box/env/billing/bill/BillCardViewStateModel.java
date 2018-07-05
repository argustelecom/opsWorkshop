package ru.argustelecom.box.env.billing.bill;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.billing.bill.model.Bill;
import ru.argustelecom.box.env.billing.bill.model.BillHistoryItem;
import ru.argustelecom.system.inf.modelbase.Identifiable;
import ru.argustelecom.system.inf.page.PresentationState;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

@Getter
@Setter
@PresentationState
public class BillCardViewStateModel implements Serializable {

	private final static EntityConverter entityConverter = new EntityConverter();

	private Bill bill;
	private BillHistoryItem billHistoryItem;


	public String getReference(Identifiable value) {
		return entityConverter.convertToString(value);
	}

	private static final long serialVersionUID = 1708890552248711192L;
}
