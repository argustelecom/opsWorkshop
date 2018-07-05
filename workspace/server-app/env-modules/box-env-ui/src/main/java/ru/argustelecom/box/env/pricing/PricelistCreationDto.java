package ru.argustelecom.box.env.pricing;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
import java.util.List;

import ru.argustelecom.box.env.party.model.CustomerSegment;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.model.role.Owner;

/**
 * DTO для диалога создания прайс-листа
 */
@Getter
@Setter
@NoArgsConstructor
public class PricelistCreationDto {

	private String name;
	private Date validFrom;
	private Date validTo;
	private List<CustomerSegment> segments;
	private CustomerType customerType;
	private Customer customer;
	private Owner owner;

	@Builder
	public PricelistCreationDto(String name, Date validFrom, Date validTo, List<CustomerSegment> segments,
								CustomerType customerType, Customer customer, Owner owner) {
		this.name = name;
		this.validFrom = validFrom;
		this.validTo = validTo;
		this.segments = segments;
		this.customerType = customerType;
		this.customer = customer;
		this.owner = owner;
	}
}