package ru.argustelecom.box.env.billing.bill;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BillSendingInfoDto {

	private Date date;
	private String email;

}