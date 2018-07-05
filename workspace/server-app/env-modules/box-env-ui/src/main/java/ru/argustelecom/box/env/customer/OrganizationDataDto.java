package ru.argustelecom.box.env.customer;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.company.CompanyDataDto;
import ru.argustelecom.box.env.contact.EmailContactDto;

@Getter
@Setter
@NoArgsConstructor
public class OrganizationDataDto extends CustomerDataDto {

	private CompanyDataDto companyData;

	@Builder
	public OrganizationDataDto(Long customerId, String typeName, boolean vip, EmailContactDto mainEmail, Long companyId,
			String legalName, String brandName) {
		super(customerId, typeName, vip, mainEmail);
		this.companyData = new CompanyDataDto(companyId, legalName, brandName);
	}

}