package ru.argustelecom.box.env.billing.subscription;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.pricing.PricelistDto;
import ru.argustelecom.box.env.pricing.ProductOfferingDto;

@Getter
@Setter
@NoArgsConstructor
public class SubscriptionCreationDto {

	private SubjectCauseDto subjectCause;
	private PricelistDto pricelist;
	private ContractEntryDto contractEntry;
	private ProductOfferingDto productOffering;
	private Date validFrom;
	private Date validTo;

}