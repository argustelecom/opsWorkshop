package ru.argustelecom.box.env.company;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = { "companyId" })
public class CompanyDataDto {

	private Long companyId;
	private String legalName;
	private String brandName;

}