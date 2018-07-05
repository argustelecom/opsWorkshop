package ru.argustelecom.box.env.billing.bill;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.argustelecom.box.env.billing.bill.model.AggDataHolder;
import ru.argustelecom.box.env.billing.bill.model.RawDataHolder;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class DataHolder {
	@NonNull
	private RawDataHolder rawDataHolder;
	@NonNull
	private AggDataHolder aggDataHolder;
}
