package ru.argustelecom.box.env.billing.invoice;

import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.util.function.Function;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.invoice.model.ChargeJob;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJobState;
import ru.argustelecom.box.env.billing.invoice.model.Report;
import ru.argustelecom.box.env.billing.mediation.AssociateUsageInvoicesWithCallsService;
import ru.argustelecom.box.env.mediation.nls.MediationMessagesBundle;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.env.party.model.role.PartyRoleRepository;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.service.DomainService;
import ru.argustelecom.box.publang.billing.model.IReport;
import ru.argustelecom.system.inf.exception.SystemException;

import static java.util.Optional.*;
import static ru.argustelecom.box.env.billing.invoice.JobReportService.Result.getResult;
import static ru.argustelecom.box.inf.nls.LocaleUtils.*;

@DomainService
public class JobReportService implements Serializable {
	private static final long serialVersionUID = 2604920577598674721L;

	private static final String STAGE_OF_CONVERTATION = "conv_stage_1";
	private static final String STAGE_OF_ANALYSIS = "conv_stage_2";
	private static final String STAGE_OF_IDENTIFICATION = "conv_stage_3";
	private static final String STAGE_OF_CHARGING = "rating_stage";

	private static final String LINE_BREAK = "\n\n";

	@Inject
	private PartyRoleRepository partyRoleRp;

	@Inject
	private AssociateUsageInvoicesWithCallsService associateUsageInvoicesWithCallsSvc;

	public String createCommentHeader(ChargeJobState fromState, ChargeJobState toState) {
		MediationMessagesBundle messages = getMessages(MediationMessagesBundle.class);
		return format(messages.changeState(fromState.getName(), toState.getName()));
	}

	public String createCommentBody(ChargeJob chargeJob) {
		MediationMessagesBundle messages = getMessages(MediationMessagesBundle.class);
		StringBuilder resultComment = new StringBuilder();

		long countCalls = associateUsageInvoicesWithCallsSvc.countCallsByJob(chargeJob);
		long countUnsuitableCalls = associateUsageInvoicesWithCallsSvc.countUnsuitableCallsByJob(chargeJob);

		Result result = getResult(countCalls, countUnsuitableCalls);

		append(resultComment, messages.resultMsg(result.getName()));
		append(resultComment, messages.totalProcessed(String.valueOf(countCalls)));
		append(resultComment, messages.successProcessed(String.valueOf(countCalls - countUnsuitableCalls)));

		return resultComment.toString();
	}

	public String createCommentBody(Report report) {
		MediationMessagesBundle messages = getMessages(MediationMessagesBundle.class);
		StringBuilder resultComment = new StringBuilder();

		ofNullable(report.getResult())
				.ifPresent((result) -> append(resultComment, messages.resultMsg(result)));

		ofNullable(report.getTotalProcessed()).ifPresent(totalProcessed -> {
			append(resultComment, messages.totalProcessed(String.valueOf(totalProcessed)));
		});

		ofNullable(report.getSuccess()).ifPresent(success -> {
			append(resultComment, messages.successProcessed(String.valueOf(success)));
		});

		ofNullable(report.getUnsuitables()).ifPresent(unsuitables -> {
			if (!unsuitables.isEmpty()) {
				append(resultComment, messages.unsuitableData());

				unsuitables.entrySet().forEach(entry -> {
					append(resultComment, messages.unsuitableStage(getStageName(entry.getKey())));
					append(resultComment, messages.unsuitableCount(String.valueOf(entry.getValue())));
				});
			}
		});

		return resultComment.toString();
	}

	public Employee getCommentator() {
		return partyRoleRp.getQueueUser();
	}

	private StringBuilder append(StringBuilder sb, String msg) {
		return sb.append(msg).append(LINE_BREAK);
	}

	private String getStageName(String stage) {
		MediationMessagesBundle messages = LocaleUtils.getMessages(MediationMessagesBundle.class);

		switch (stage) {
			case STAGE_OF_CONVERTATION:
				return messages.stageOfConvertation();
			case STAGE_OF_ANALYSIS:
				return messages.stageOfAnalysis();
			case STAGE_OF_IDENTIFICATION:
				return messages.stageOfIdentification();
			case STAGE_OF_CHARGING:
				return messages.stageOfCharging();
			default:
				throw new SystemException(format("Unsupported stage %s", stage));
		}
	}

	@AllArgsConstructor
	public enum Result {
		//@formatter:off
		OK		(MediationMessagesBundle::resultOk),
		WARN	(MediationMessagesBundle::resultWarn),
		ERROR	(MediationMessagesBundle::resultError);
		//@formatter:on

		private Function<MediationMessagesBundle, String> nameGetter;

		public String getName() {
			return nameGetter.apply(getMessages(MediationMessagesBundle.class));
		}

		public static JobReportService.Result getResult(long callsCount, long unsuitableCallsCount) {
			if (callsCount == 0) {
				return OK;
			}

			return callsCount == unsuitableCallsCount ? ERROR : (unsuitableCallsCount == 0L ? OK : WARN);
		}
	}
}
