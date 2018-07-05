package ru.argustelecom.box.env.saldo.export;

import static ru.argustelecom.box.inf.chrono.ChronoUtils.fromLocalDateTime;
import static ru.argustelecom.box.inf.queue.api.QueueProducer.Priority.MEDIUM;

import java.time.LocalDateTime;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.saldo.export.model.SaldoExportIssue;
import ru.argustelecom.box.env.saldo.export.model.SaldoExportParam;
import ru.argustelecom.box.env.saldo.export.queue.SaldoExportContext;
import ru.argustelecom.box.env.saldo.export.queue.SaldoExportQueueHandler;
import ru.argustelecom.box.inf.queue.api.QueueProducer;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@Named(value = "saldoExportVM")
@PresentationModel
public class SaldoExportViewModel extends ViewModel {

	private static final long serialVersionUID = 1737299160932873687L;

	@Inject
	private SaldoExportHistoryFrameModel historyFrameModel;

	@Inject
	private SaldoExportParamRepository sepr;

	@Inject
	private SaldoExportIssueRepository seir;

	@Inject
	private QueueProducer queueProducer;

	private SaldoExportParam param;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
		initParam();
	}

	public void startExport() {
		removeOldIssue();
		scheduleNewIssue();

		param.setWorking(true);
		em.merge(param);
		historyFrameModel.resetIssues();
	}

	public void stopExport() {
		removeOldIssue();

		param.setWorking(false);
		em.merge(param);
		historyFrameModel.resetIssues();
	}

	public Callback<SaldoExportParam> getCallback() {
		return param -> {
			this.param = param;
			if (param.getWorking()) {
				em.refresh(param);
				startExport();
			}
		};
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private void initParam() {
		param = sepr.getParam();
	}

	private void removeOldIssue() {
		SaldoExportIssue lastIssue = seir.findLastIssue();
		if (lastIssue != null) {
			queueProducer.remove(lastIssue);
			em.remove(lastIssue);
		}
	}

	private void scheduleNewIssue() {
		LocalDateTime nextExportDate = param.getExportDate(LocalDateTime.now());
		SaldoExportIssue newSaldoExportIssue = seir.createIssue(fromLocalDateTime(nextExportDate));
		SaldoExportContext newContext = new SaldoExportContext(newSaldoExportIssue);
		queueProducer.schedule(newSaldoExportIssue, null, MEDIUM, newSaldoExportIssue.getExportDate(),
				SaldoExportQueueHandler.HANDLER_NAME, newContext);
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public SaldoExportParam getParam() {
		return param;
	}

}