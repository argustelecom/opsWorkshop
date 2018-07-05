package ru.argustelecom.box.env.customer;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.contact.EmailContactDto;

@Getter
@Setter
@EqualsAndHashCode(of = { "customerId" })
@NoArgsConstructor
@AllArgsConstructor
public abstract class CustomerDataDto {

	private Long customerId;
	private String typeName;
	private boolean vip;
	private EmailContactDto mainEmail;

}