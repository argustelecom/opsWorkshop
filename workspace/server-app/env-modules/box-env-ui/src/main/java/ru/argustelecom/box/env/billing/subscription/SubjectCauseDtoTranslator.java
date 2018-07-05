package ru.argustelecom.box.env.billing.subscription;

import static ru.argustelecom.box.env.billing.subscription.SubjectCauseType.CONTRACT;
import static ru.argustelecom.box.env.billing.subscription.SubjectCauseType.ORDER;

import ru.argustelecom.box.env.billing.subscription.model.ContractSubjectCause;
import ru.argustelecom.box.env.billing.subscription.model.OrderSubjectCause;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionSubjectCause;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.order.model.Order;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.system.inf.exception.SystemException;

@DtoTranslator
public class SubjectCauseDtoTranslator {

	public SubjectCauseDto translate(SubscriptionSubjectCause subjectCause) {
		//@formatter:off
		return SubjectCauseDto.builder()
					.id(subjectCause.getId())
					.causeId(determineCauseId(subjectCause))
					.name(subjectCause.getObjectName())
					.fullName(determineCauseFullName(subjectCause))
					.type(determineSubjectCauseType(subjectCause))
					.note(subjectCause.getNote())
				.build();
		//@formatter:on
	}

	public SubjectCauseDto translate(AbstractContract<?> contract) {
		//@formatter:off
		return SubjectCauseDto.builder()
					.causeId(contract.getId())
					.name(contract.getObjectName())
					.fullName(contract.getFullName())
					.type(CONTRACT)
				.build();
		//@formatter:on
	}

	public SubjectCauseDto translate(Order order) {
		//@formatter:off
		return SubjectCauseDto.builder()
					.causeId(order.getId())
					.name(order.getObjectName())
					.fullName(order.getObjectName())
					.type(ORDER)
				.build();
		//@formatter:on
	}

	private Long determineCauseId(SubscriptionSubjectCause subjectCause) {
		return subjectCause instanceof ContractSubjectCause
				? ((ContractSubjectCause) subjectCause).getContractEntry().getContract().getId() : null;
	}

	private String determineCauseFullName(SubscriptionSubjectCause subjectCause) {
		return subjectCause instanceof ContractSubjectCause
				? ((ContractSubjectCause) subjectCause).getContractEntry().getContract().getFullName()
				: subjectCause.getObjectName();
	}

	private SubjectCauseType determineSubjectCauseType(SubscriptionSubjectCause subjectCause) {
		if (subjectCause instanceof ContractSubjectCause) {
			return CONTRACT;
		} else if (subjectCause instanceof OrderSubjectCause) {
			return ORDER;
		} else {
			throw new SystemException(
					String.format("Unsupported subject cause: '%s'", subjectCause.getClass().getSimpleName()));
		}
	}

}