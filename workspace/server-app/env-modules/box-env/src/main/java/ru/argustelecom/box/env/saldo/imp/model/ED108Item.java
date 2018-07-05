package ru.argustelecom.box.env.saldo.imp.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import ru.argustelecom.box.env.stl.Money;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ED108Item extends RegisterItem {

	@Element(orderNumber = 6)
	private String accountNumber;

	@Element(orderNumber = 11)
	private Money sum;

	private String paymentDocNumber;

	private Date paymentDocDate;

	@Element(orderNumber = 1, dateFormat = "dd-MM-yyyy")
	private LocalDate paymentDate;

	@Element(orderNumber = 2, dateFormat = "H-mm-ss")
	private LocalTime paymentTime;

	public ED108Item(String rowData) {
		super(rowData);
	}

}