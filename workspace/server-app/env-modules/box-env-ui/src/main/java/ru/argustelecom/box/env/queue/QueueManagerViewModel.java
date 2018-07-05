package ru.argustelecom.box.env.queue;

import static com.google.common.base.Preconditions.checkState;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import ru.argustelecom.box.env.queue.nls.QueueMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.queue.api.QueueManager;
import ru.argustelecom.box.inf.queue.api.QueueManagerStatus;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@PresentationModel
public class QueueManagerViewModel extends ViewModel{

	private static final long serialVersionUID = 3219574356678606500L;

	private static final long AWAIT_TERMINATION_TIMEOUT = 61000;

	@Inject
	private QueueManager queueManager;

	@Override
	@PostConstruct
	protected void postConstruct() {
	}
	
	public void startup() {
		checkState(isCanStartup());

		queueManager.startup();
	}

	public void shutdown() {
		checkState(isCanShutdown());

		queueManager.shutdown();
		queueManager.awaitTermination(AWAIT_TERMINATION_TIMEOUT);
	}

	public boolean isCanStartup() {
		return queueManager.getStatus().equals(QueueManagerStatus.INACTIVE);
	}

	public boolean isCanShutdown() {
		return queueManager.getStatus().equals(QueueManagerStatus.ACTIVE);
	}

	public String getQueueStatus() {
		QueueMessagesBundle messages = LocaleUtils.getMessages(QueueMessagesBundle.class);

		switch (queueManager.getStatus()) {
		case ACTIVE:
			return messages.stateActive();
		case ACTIVATING:
			return messages.stateActivating();
		case DEACTIVATING:
			return messages.stateDeactivating();
		case INACTIVE:
			return messages.stateInactive();
		default:
			throw new SystemException("Unsupported queue state");
		}
	}
}
