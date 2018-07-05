package ru.argustelecom.box.env.numerationpattern;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.bill.model.BillType;
import ru.argustelecom.box.env.contract.model.AbstractContractType;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.numerationpattern.model.BillNumerationPattern;
import ru.argustelecom.box.env.numerationpattern.model.BillNumerationPattern.BillNumerationPatternEntityQuery;
import ru.argustelecom.box.env.numerationpattern.model.ContractNumerationPattern;
import ru.argustelecom.box.env.numerationpattern.model.ContractNumerationPattern.ContractNumerationPatternQuery;
import ru.argustelecom.box.env.numerationpattern.model.NumerationPattern;
import ru.argustelecom.box.env.numerationpattern.model.NumerationPattern.NumerationPatternQuery;
import ru.argustelecom.box.env.numerationpattern.statement.Statement;
import ru.argustelecom.box.inf.service.Repository;

@Repository
public class NumerationPatternRepository {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequenceService;

	public ContractNumerationPattern createContractNumerationPattern(String className, String pattern,
			List<Statement> statements, AbstractContractType abstractContractType) {
		ContractNumerationPattern contractNumerationPattern = new ContractNumerationPattern(
				idSequenceService.nextValue(ContractNumerationPattern.class));

		fillCommonFields(contractNumerationPattern, className, pattern, statements);
		contractNumerationPattern.setContractType(abstractContractType);

		em.persist(contractNumerationPattern);

		return contractNumerationPattern;
	}

	public NumerationPattern createNumerationPattern(String className, String pattern, List<Statement> statements) {
		NumerationPattern numerationPattern = new NumerationPattern(
				idSequenceService.nextValue(NumerationPattern.class));

		fillCommonFields(numerationPattern, className, pattern, statements);

		em.persist(numerationPattern);

		return numerationPattern;
	}

	public BillNumerationPattern createBillNumerationPattern(String className, String pattern,
			List<Statement> statements, BillType billType) {
		BillNumerationPattern billNumerationPattern = new BillNumerationPattern(
				idSequenceService.nextValue(BillNumerationPattern.class));

		fillCommonFields(billNumerationPattern, className, pattern, statements);
		billNumerationPattern.setBillType(billType);

		em.persist(billNumerationPattern);

		return billNumerationPattern;
	}

	public void editNumerationPattern(NumerationPattern numerationPattern, String pattern, List<Statement> statements) {
		fillCommonFields(numerationPattern, numerationPattern.getClassName(), pattern, statements);
	}

	// TODO сделать общий метод, принимающий в качестве арумента экземпляр Type
	public List<ContractNumerationPattern> findByClassAndType(Class<?> clazz, AbstractContractType contractType) {
		ContractNumerationPatternQuery query = new ContractNumerationPatternQuery();
		//@formatter:off
		query.and(
			query.className().equal(clazz.getName()),
				query.criteriaBuilder().or(
						query.contractType().isNull(),
						query.contractType().equal(contractType))
		);
		//@formatter:on
		return query.getResultList(em);
	}

	public List<BillNumerationPattern> findByClassAndType(Class<?> clazz, BillType billType) {
		BillNumerationPatternEntityQuery query = new BillNumerationPatternEntityQuery();
		//@formatter:off
		query.and(
				query.className().equal(clazz.getName()),
				query.criteriaBuilder().or(
						query.billType().isNull(),
						query.billType().equal(billType))
		);
		//@formatter:on
		return query.getResultList(em);
	}

	public NumerationPattern findByClass(Class<?> clazz) {
		NumerationPatternQuery query = new NumerationPatternQuery();
		query.criteriaQuery().where(query.className().equal(clazz.getName()));
		return query.getSingleResult(em, false);
	}

	public List<NumerationPattern> findAllNumerationPatterns() {
		return new NumerationPattern.NumerationPatternQuery().getResultList(em);
	}

	public boolean canBeDeleted(String seq) {
		NumerationPatternQuery numerationPatternQuery = new NumerationPatternQuery();
		numerationPatternQuery.criteriaQuery()
				.where(numerationPatternQuery.byStatement(String.format("addSeq %s", seq)));
		return numerationPatternQuery.getResultList(em).isEmpty();
	}

	private void fillCommonFields(NumerationPattern numerationPattern, String className, String pattern,
			List<Statement> statements) {
		numerationPattern.setClassName(className);
		numerationPattern.setPattern(pattern);
		numerationPattern.setStatements(statements);
	}
}
