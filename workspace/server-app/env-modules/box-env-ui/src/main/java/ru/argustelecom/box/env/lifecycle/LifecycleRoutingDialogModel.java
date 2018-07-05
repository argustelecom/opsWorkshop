package ru.argustelecom.box.env.lifecycle;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;

import java.io.Serializable;
import java.util.List;

import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;

import lombok.Getter;
import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.executor.LifecycleExecutor;
import ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseId;
import ru.argustelecom.box.env.overall.nls.OverallMessagesBundle;
import ru.argustelecom.box.env.type.model.TypeInstance;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.validation.ValidationIssue;

@PresentationModel
@Named("lifecycleRoutingDlg")
@SuppressWarnings("rawtypes")
public class LifecycleRoutingDialogModel implements Serializable {

	private static final long serialVersionUID = 6352569818586175868L;

	@Getter
	private transient LifecycleExecutor executor;

	public void setExecutor(LifecycleExecutor executor) {
		if (executor != null) {
			checkArgument(executor.getCurrentPhaseId() != null);
			checkArgument(executor.getCurrentPhaseId().greaterOrEquals(LifecyclePhaseId.ROUTE_DEFINITION));
			checkArgument(executor.getCurrentPhaseId().lessOrEquals(LifecyclePhaseId.ROUTE_VALIDATION));
		}
		this.executor = executor;
	}

	protected LifecycleObject getBusinessObject() {
		return executor != null ? executor.getBusinessObject() : null;
	}

	protected LifecycleState getInitialState() {
		return executor != null ? executor.getBusinessObjectState() : null;
	}

	protected LifecycleState getFinalState() {
		return executor != null ? executor.getEndpoint().getDestination() : null;
	}

	public String getRouteName() {
		return executor != null ? executor.getRoute().getName() : "?";
	}

	public boolean isRenderVariablesFrame() {
		return executor != null && executor.getCurrentPhaseId() == LifecyclePhaseId.ROUTE_DEFINITION;
	}

	public boolean hasVariables() {
		return executor != null && !executor.getEndpoint().getVariables().isEmpty();
	}

	public TypeInstance getVariablesHolder() {
		return executor != null ? executor.getExecutionContext().getValuesHolder() : null;
	}

	public boolean isRenderValidationFrame() {
		return executor != null && executor.getCurrentPhaseId() == LifecyclePhaseId.ROUTE_VALIDATION;
	}

	public boolean isRenderBackToVariablesButton() {
		return isRenderValidationFrame() && !executor.getEndpoint().isSilent();
	}

	public boolean isRenderIgnoreWarningsCheckbox() {
		return isRenderValidationFrame() && executor.getValidationResult().hasWarnings()
				&& !executor.getValidationResult().hasErrors();
	}

	public boolean isIgnoreWarnings() {
		return isRenderValidationFrame() && executor.getExecutionContext().isIgnoreWarnings();
	}

	public void setIgnoreWarnings(boolean ignoreWarnings) {
		if (isRenderValidationFrame()) {
			executor.getExecutionContext().suppressWarnings(ignoreWarnings);
		}
	}

	public boolean isCommitRouteButtonDisabled() {
		boolean enabled = isRenderVariablesFrame()
				|| isRenderValidationFrame() && executor.getValidationResult().isSuccess(isIgnoreWarnings());

		return !enabled;

	}

	/**
	 * @return Если есть ошибки, то возвращается только их список. В противном случае возвращается список warnings +
	 *         infos
	 */
	public List<ValidationIssue> getValidationIssues() {
		if (!isRenderValidationFrame())
			return emptyList();
		return executor.getValidationResult().hasErrors() ? getErrorIssues() : getNonErrorIssues();
	}

	public String determineMessageStyle(ValidationIssue.Kind issueKind) {
		switch (issueKind) {
		case INFO:
			return "message-bg-info message-icon-info";
		case WARNING:
			return "message-bg-warn message-icon-warn";
		case ERROR:
			return "message-bg-error message-icon-error";
		default:
			return StringUtils.EMPTY;
		}
	}

	public String getIssueKindTitle(ValidationIssue.Kind issueKind) {
		OverallMessagesBundle messages = LocaleUtils.getMessages(OverallMessagesBundle.class);
		switch (issueKind) {
		case INFO:
		case WARNING:
			return messages.warning();
		case ERROR:
			return messages.error();
		default:
			return StringUtils.EMPTY;
		}
	}

	public void onBackToVariables() {
		if (isRenderBackToVariablesButton()) {
			executor.backToPreviousPhase();
			executor.getExecutionContext().suppressWarnings(false);
		}
	}

	public void onCancelRoute() {
		// DO NOTHING
	}

	public void onCommitRoute() {
		if (executor == null) {
			return;
		}

		if (executor.getCurrentPhaseId() == LifecyclePhaseId.ROUTE_DEFINITION) {
			executor.validateRoute();
			if (executor.getValidationResult().isEmpty()) {
				executor.finalizeRouting();
				hideDialog();
			}
		} else {
			checkState(executor.getCurrentPhaseId() == LifecyclePhaseId.ROUTE_VALIDATION);
			if (!isCommitRouteButtonDisabled()) {
				executor.finalizeRouting();
				hideDialog();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List<ValidationIssue> getErrorIssues() {
		return newArrayList(executor.getValidationResult().getErrors());
	}

	@SuppressWarnings("unchecked")
	private List<ValidationIssue> getNonErrorIssues() {
		List<ValidationIssue> nonErrorIssues = newArrayList(executor.getValidationResult().getWarnings());
		nonErrorIssues.addAll(executor.getValidationResult().getInfos());
		return nonErrorIssues;
	}

	private void hideDialog() {
		RequestContext.getCurrentInstance().execute("PF('lifecycleRoutingDlgVar').hide()");
		executor = null;
	}

}