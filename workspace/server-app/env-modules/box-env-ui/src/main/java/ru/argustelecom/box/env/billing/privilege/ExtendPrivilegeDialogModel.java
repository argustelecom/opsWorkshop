package ru.argustelecom.box.env.billing.privilege;

import static com.google.common.base.Preconditions.checkState;
import static java.time.temporal.ChronoUnit.MILLIS;
import static ru.argustelecom.box.env.stl.period.PeriodUtils.createPeriod;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.fromLocalDateTime;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.toLocalDateTime;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import com.google.common.collect.Range;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.privilege.PrivilegeAppService;
import ru.argustelecom.box.env.stl.period.PeriodDuration;
import ru.argustelecom.box.env.stl.period.PeriodType;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named("extendPrivilegeDm")
@PresentationModel
public class ExtendPrivilegeDialogModel implements Serializable {

	@Inject
	private PrivilegeAppService privilegeAs;

	@Getter
	@Setter
	private PrivilegeDto privilege;

	@Getter
	@Setter
	private int amount;

	@Getter
	@Setter
	private PeriodUnit periodUnit;

	public void openDialog() {
		RequestContext.getCurrentInstance().update("extend_privilege_form-extend_privilege_dlg");
		RequestContext.getCurrentInstance().execute("PF('extendPrivilegeDlgVar').show()");
	}

	public void extend() {
		LocalDateTime nextPeriodStart = toLocalDateTime(privilege.getValidTo()).plus(1, MILLIS);
		PeriodDuration nextPeriodDuration = PeriodDuration.of(amount, periodUnit);
		Range<LocalDateTime> nextPeriodBoundaries = createPeriod(nextPeriodStart, nextPeriodDuration);
		checkState(nextPeriodBoundaries != null);

		Date newPrivilegeEnd = fromLocalDateTime(nextPeriodBoundaries.upperEndpoint());
		privilegeAs.extendPrivilege(privilege.getId(), newPrivilegeEnd);
		privilege.setValidTo(newPrivilegeEnd);
		clean();
	}

	public void clean() {
		privilege = null;
		amount = 0;
		periodUnit = null;
	}

	public PeriodUnit[] getPossiblePeriodUnits() {
		boolean subjectIsSubscription = privilege.getSubject().getType().equals(PrivilegeSubjectType.SUBSCRIPTION);
		
		return subjectIsSubscription
				? privilege.getSubject().getProvisionTerms().getPeriodType().getChargingPeriodUnits()
				: PeriodType.CALENDARIAN.getChargingPeriodUnits();
	}

	public boolean canBeExtended(PrivilegeDto privilege) {
		return !privilege.getType().equals(PrivilegeTypeRef.DISCOUNT);
	}

	private static final long serialVersionUID = -7351807761718989311L;

}