package ru.argustelecom.box.env.numerationpattern;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.bill.model.BillType;
import ru.argustelecom.box.env.contract.model.AbstractContractType;
import ru.argustelecom.box.env.numerationpattern.model.BillNumerationPattern;
import ru.argustelecom.box.env.numerationpattern.model.ContractNumerationPattern;
import ru.argustelecom.box.env.numerationpattern.model.NumerationPattern;
import ru.argustelecom.box.env.numerationpattern.statement.Statement;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class NumerationPatternAppService {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private NumerationPatternRepository repository;

	public ContractNumerationPattern createContractNumerationPattern(String className, String pattern,
			List<Statement> statements, Long typeId) {
		AbstractContractType abstractContractType = typeId != null ? em.find(AbstractContractType.class, typeId) : null;
		return repository.createContractNumerationPattern(className, pattern, statements, abstractContractType);
	}

	public BillNumerationPattern createBillNumerationPattern(String className, String pattern, List<Statement> statements,
			Long typeId) {
		BillType billType = typeId != null ? em.find(BillType.class, typeId) : null;
		return repository.createBillNumerationPattern(className, pattern, statements, billType);
	}

	public NumerationPattern createNumerationPattern(String className, String pattern, List<Statement> statements) {
		return repository.createNumerationPattern(className, pattern, statements);
	}

	public void editNumerationPattern(Long id, String pattern, List<Statement> statements) {
		NumerationPattern numerationPattern = em.find(NumerationPattern.class, id);
		repository.editNumerationPattern(numerationPattern, pattern, statements);
	}

	public void deleteNumerationPattern(Long id) {
		em.remove(em.find(NumerationPattern.class, id));
	}

}
