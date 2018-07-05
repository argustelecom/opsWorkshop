package ru.argustelecom.box.env.billing.privilege;

import static ru.argustelecom.box.env.privilege.model.PrivilegeType.TRUST_PERIOD;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import ru.argustelecom.box.env.privilege.discount.model.Discount;
import ru.argustelecom.box.env.privilege.model.CustomerPrivilege;
import ru.argustelecom.box.env.privilege.model.PersonalAccountPrivilege;
import ru.argustelecom.box.env.privilege.model.Privilege;
import ru.argustelecom.box.env.privilege.model.SubscriptionPrivilege;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.exception.SystemException;

@DtoTranslator
public class PrivilegeDtoTranslator {

	@Inject
	private PrivilegeSubjectDtoTranslator subjectTranslator;

	public PrivilegeDto translate(Privilege privilege) {
		privilege = EntityManagerUtils.initializeAndUnproxy(privilege);

		//@formatter:off
		return PrivilegeDto.builder()
					.id(privilege.getId())
					.type(determinePrivilegeType(privilege))
					//создание нового экземпляра Date необходимо, т.к. в сущности хранится Timestamp, а у него метод
					//equals() возвращает false, если аргумент не является Timestamp
					.validFrom(new Date(privilege.getValidFrom().getTime()))
					.validTo(new Date((privilege.getValidTo().getTime())))
					.objectName(privilege.getObjectName())
					.subject(subjectTranslator.translate(getSubject(privilege)))
				.build();
		//@formatter:on
	}

	public PrivilegeDto translate(Discount discount) {
		discount = EntityManagerUtils.initializeAndUnproxy(discount);

		//@formatter:off
		return PrivilegeDto.builder()
				.id(discount.getId())
				.type(PrivilegeTypeRef.DISCOUNT)
				.validFrom(new Date(discount.getValidFrom().getTime()))
				.validTo(new Date(discount.getValidTo().getTime()))
				.objectName(discount.getObjectName())
				.rateOfDiscount(discount.getRate())
				.subject(subjectTranslator.translate(discount.getSubscription()))
				.build();
		//@formatter:on
	}

	public List<PrivilegeDto> translate(List<Privilege> privileges) {
		return privileges.stream().map(this::translate).collect(Collectors.toList());
	}

	private PrivilegeTypeRef determinePrivilegeType(Privilege privilege) {
		return privilege.getType().equals(TRUST_PERIOD) ? PrivilegeTypeRef.TRUST_PERIOD : PrivilegeTypeRef.TRIAL_PERIOD;
	}

	private BusinessObject getSubject(Privilege privilege) {
		privilege = EntityManagerUtils.initializeAndUnproxy(privilege);
		if (privilege instanceof SubscriptionPrivilege) {
			return ((SubscriptionPrivilege) privilege).getSubscription();
		} else if (privilege instanceof PersonalAccountPrivilege) {
			return ((PersonalAccountPrivilege) privilege).getPersonalAccount();
		} else if (privilege instanceof CustomerPrivilege) {
			return ((CustomerPrivilege) privilege).getCustomer();
		} else
			throw new SystemException(String.format("Unsupported privilege: '%s'", privilege));
	}

}