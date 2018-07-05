package ru.argustelecom.box.env.saldo.export;

import static ru.argustelecom.box.env.saldo.export.model.SaldoExportIssueState.EXPORTED;
import static ru.argustelecom.box.env.saldo.export.model.SaldoExportIssueState.WAITING;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.fromLocalDateTime;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.toLocalDateTime;

import java.io.Serializable;
import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import com.google.common.collect.Range;

import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.saldo.export.model.SaldoExportEvent;
import ru.argustelecom.box.env.saldo.export.model.SaldoExportEventState;
import ru.argustelecom.box.env.saldo.export.model.SaldoExportEventType;
import ru.argustelecom.box.env.saldo.export.model.SaldoExportIssue;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;

@Repository
public class SaldoExportIssueRepository implements Serializable {

	private static final long serialVersionUID = -4964439177234160711L;

	private static final String GENERATE_NEXT_NUMBER = "SaldoExportIssueRepository.nextNumber";
	private static final String RESET_NUMBER_GENERATOR = "SaldoExportIssueRepository.resetNumberGenerator";
	private static final String FIND_ALL_ISSUES = "SaldoExportIssueRepository.findAllIssues";
	public static final String FIND_LAST_ISSUE = "SaldoExportIssueRepository.findLastIssue";

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequenceService;

	@Inject
	private SaldoExportParamRepository sepr;

	public SaldoExportIssue createIssue(@NotNull Date exportDate) {
		SaldoExportIssue instance = new SaldoExportIssue(idSequenceService.nextValue(SaldoExportIssue.class));

		instance.setCreationDate(new Date());
		instance.setNumber(nextNumber());
		instance.setState(WAITING);
		instance.setExportDate(exportDate);

		Range<LocalDateTime> range = sepr.getParam().getRange(toLocalDateTime(exportDate));
		instance.setFrom(fromLocalDateTime(range.lowerEndpoint()));
		instance.setTo(fromLocalDateTime(range.upperEndpoint()));

		em.persist(instance);
		return instance;
	}

	public void saveFile(@NotNull SaldoExportIssue issue, @NotNull Blob file) {
		issue.setFile(file);
		issue.setState(EXPORTED);
		em.merge(issue);
	}

	public void createEvent(@NotNull SaldoExportIssue issue, @NotNull Date executedDate,
			@NotNull SaldoExportEventType type, @NotNull SaldoExportEventState state, String description) {
		SaldoExportEvent instance = new SaldoExportEvent(idSequenceService.nextValue(SaldoExportEvent.class));

		instance.setExecutedDate(executedDate);
		instance.setType(type);
		instance.setState(state);
		instance.setDescription(description);

		issue.addEvent(instance);
		em.merge(issue);
	}

	@NamedQuery(name = FIND_ALL_ISSUES, query = "from SaldoExportIssue i order by i.exportDate desc")
	public List<SaldoExportIssue> findAllIssues() {
		return em.createNamedQuery(FIND_ALL_ISSUES, SaldoExportIssue.class).getResultList();
	}

	@NamedQuery(name = FIND_LAST_ISSUE, query = "from SaldoExportIssue i where i.state = 'WAITING' order by i.exportDate desc")
	public SaldoExportIssue findLastIssue() {
		try {
			return em.createNamedQuery(FIND_LAST_ISSUE, SaldoExportIssue.class).setMaxResults(1).getSingleResult();
		} catch (NoResultException ignore) {
		}
		return null;
	}

	@NamedNativeQuery(name = RESET_NUMBER_GENERATOR, query = "ALTER SEQUENCE system.gen_saldo_export_number RESTART WITH 10000")
	public void resetNumberGenerator() {
		em.createNamedQuery(RESET_NUMBER_GENERATOR).executeUpdate();
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	@NamedNativeQuery(name = GENERATE_NEXT_NUMBER, query = "select nextval('system.gen_saldo_export_number')")
	private Long nextNumber() {
		return Long.valueOf(em.createNamedQuery(GENERATE_NEXT_NUMBER).getSingleResult().toString());
	}

}