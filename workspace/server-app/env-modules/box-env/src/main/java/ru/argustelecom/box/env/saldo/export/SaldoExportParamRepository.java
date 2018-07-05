package ru.argustelecom.box.env.saldo.export;

import java.io.Serializable;
import java.util.Date;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import ru.argustelecom.box.env.saldo.export.model.CalculationType;
import ru.argustelecom.box.env.saldo.export.model.SaldoExportParam;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.inf.service.Repository;

@Repository
public class SaldoExportParamRepository implements Serializable {

	private static final long serialVersionUID = -6910915102147083620L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private SaldoExportIssueRepository seir;

	public SaldoExportParam getParam() {
		return em.find(SaldoExportParam.class, SaldoExportParam.PARAM_ID);
	}

	public SaldoExportParam editParam(@NotNull PeriodUnit periodUnit, @NotNull CalculationType calculationType,
			@NotNull Date executeTime, boolean resetNumberGenerator) {
		if (resetNumberGenerator)
			seir.resetNumberGenerator();

		SaldoExportParam param = getParam();

		param.setPeriodUnit(periodUnit);
		param.setCalculationType(calculationType);
		param.setExecuteTime(executeTime);

		em.merge(param);
		em.flush();
		return param;
	}

}