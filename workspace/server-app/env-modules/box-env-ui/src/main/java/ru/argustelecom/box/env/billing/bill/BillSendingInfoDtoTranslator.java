package ru.argustelecom.box.env.billing.bill;

import java.util.Optional;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.bill.model.Bill;
import ru.argustelecom.box.env.billing.bill.model.BillSendingInfo;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class BillSendingInfoDtoTranslator {

	@Inject
	private BillSendingInfoRepository billSendingInfoRp;

	public BillSendingInfoDto translate(Bill bill) {
		Optional<BillSendingInfo> sendingInfo = Optional.ofNullable(billSendingInfoRp.findSendingInfo(bill));
		return sendingInfo.map(
				billSendingInfo -> new BillSendingInfoDto(billSendingInfo.getSendingDate(), billSendingInfo.getEmail()))
				.orElseGet(BillSendingInfoDto::new);
	}

}