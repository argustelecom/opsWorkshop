package ru.argustelecom.box.env.company;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.party.model.Company;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class CompanyDataAppService implements Serializable {

	private static final long serialVersionUID = -9211104040009765839L;

	@PersistenceContext
	private EntityManager em;

	public void renameCompany(Long companyId, String legalName, String brandName) {
		checkArgument(companyId != null, "companyId is required");

		Company company = em.find(Company.class, companyId);
		company.setLegalName(legalName);
		company.setBrandName(brandName);
	}

}