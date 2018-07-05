package ru.argustelecom.box.env.saldo.imp.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.stl.Money;

@Getter
@Setter
public class SaldoRegisterItem extends RegisterItem {

	@Element(orderNumber = 3)
	private String accountNumber;

	@Element(orderNumber = 4)
	private Money sum;

	@Element(orderNumber = 9)
	private String paymentDocNumber;

	@Element(orderNumber = 10, dateFormat = "dd/MM/yyyy")
	private Date paymentDocDate;

	public SaldoRegisterItem(String rowData) {
		super(rowData);
	}

}