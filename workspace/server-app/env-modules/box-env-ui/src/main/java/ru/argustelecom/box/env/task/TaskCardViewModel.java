package ru.argustelecom.box.env.task;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import lombok.Getter;
import ru.argustelecom.box.env.billing.subscription.SubscriptionRepository;
import ru.argustelecom.box.env.commodity.telephony.TelephonyOptionAppService;
import ru.argustelecom.box.env.service.ServiceDto;
import ru.argustelecom.box.env.service.ServiceDtoTranslator;
import ru.argustelecom.box.env.task.model.TaskState;
import ru.argustelecom.box.inf.login.EmployeePrincipal;
import ru.argustelecom.box.inf.page.outcome.OutcomeConstructor;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@PresentationModel
public class TaskCardViewModel extends ViewModel {

	private static final long serialVersionUID = -321589733529648562L;

	@Inject
	private CurrentTask currentTask;

	@Inject
	private TaskDtoTranslator taskDtoTranslator;

	@Inject
	private OutcomeConstructor outcomeConstructor;

	@Inject
	private SubscriptionRepository subscriptionRepository;

	@Inject
	private TelephonyOptionAppService telephonyOptionAs;

	@Inject
	private TelephonyOptionDtoTranslator telephonyOptionDtoTr;

	@Inject
	private ServiceDtoTranslator serviceDtoTranslator;

	private EmployeePrincipal employeePrincipal;

	@Getter
	private TaskDto task;

	@Getter
	private TaskInfoDto taskInfo;

	@Getter
	private List<ServiceDto> services = new ArrayList<>();

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
		employeePrincipal = checkNotNull(EmployeePrincipal.instance());
		refresh();
	}

	public String formatAddresses(TaskInfoDto taskInfo) {
		return taskInfo.getAddresses().stream().collect(Collectors.joining(",\n"));
	}

	public Callback<List<TaskDto>> getAssignCallback() {
		return (selectedTasks -> refresh());
	}

	public Callback<TaskDto> getResolveCallback() {
		return (selectedTask -> {
			try {
				String outcome = outcomeConstructor.construct(TaskListViewModel.VIEW_ID);
				ExternalContext ex = FacesContext.getCurrentInstance().getExternalContext();
				ex.redirect(ex.getRequestContextPath() + outcome);
			} catch (IOException e) {
				throw new SystemException(e);
			}
		});
	}

	public void refresh() {
		task = taskDtoTranslator.translate(currentTask.getValue());
		taskInfo = task.getTaskInfoDto();

		services = serviceDtoTranslator
				.translate(subscriptionRepository.findServicesBySubscription(currentTask.getValue().getSubscription()));
	}

	public boolean canAssign() {
		return !task.getState().equals(TaskState.RESOLVED);
	}

	public boolean canResolve() {
		if (task.getState().equals(TaskState.RESOLVED)) {
			return false;
		}
		if (task.getAssignee() == null) {
			return false;
		}
		if (!task.getAssignee().getId().equals(employeePrincipal.getEmployeeId())) {
			return false;
		}

		return true;

	}

}
