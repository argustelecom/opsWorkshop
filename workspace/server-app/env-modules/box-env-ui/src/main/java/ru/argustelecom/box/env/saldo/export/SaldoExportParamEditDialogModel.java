package ru.argustelecom.box.env.saldo.export;

import static ru.argustelecom.box.env.stl.period.PeriodUnit.DAY;
import static ru.argustelecom.box.env.stl.period.PeriodUnit.MONTH;
import static ru.argustelecom.box.env.stl.period.PeriodUnit.WEEK;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.env.saldo.export.model.CalculationType;
import ru.argustelecom.box.env.saldo.export.model.SaldoExportParam;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

import com.google.common.collect.Lists;

@Named(value = "saldoExportParamEditDM")
@PresentationModel
public class SaldoExportParamEditDialogModel implements Serializable {

	private static final long serialVersionUID = 5846741150060772541L;

	@Inject
	private SaldoExportParamRepository sepr;

	private SaldoExportParam param;
	private Callback<SaldoExportParam> callback;

	private PeriodUnit newPeriodUnit;
	private CalculationType newCalculationType;
	private Date newExecuteTime;
	private boolean resetNumberGenerator;

	public void save() {
		callback.execute(sepr.editParam(newPeriodUnit, newCalculationType, newExecuteTime, resetNumberGenerator));
	}

	public void cancel() {
		param = null;
		callback = null;
		newPeriodUnit = null;
		newCalculationType = null;
		newExecuteTime = null;
		resetNumberGenerator = false;
	}

	public List<PeriodUnit> getPeriodUnits() {
		return Lists.newArrayList(DAY, MONTH, WEEK);
	}

	public CalculationType[] getCalculationTypes() {
		return CalculationType.values();
	}
	
	public SaldoExportParam getParam() {
		return param;
	}
	
	public void setParam(SaldoExportParam param) {
		this.param = param;
		newPeriodUnit = param.getPeriodUnit();
		newCalculationType = param.getCalculationType();
		newExecuteTime = param.getExecuteTime();
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public PeriodUnit getNewPeriodUnit() {
		return newPeriodUnit;
	}

	public void setNewPeriodUnit(PeriodUnit newPeriodUnit) {
		this.newPeriodUnit = newPeriodUnit;
	}

	public CalculationType getNewCalculationType() {
		return newCalculationType;
	}

	public void setNewCalculationType(CalculationType newCalculationType) {
		this.newCalculationType = newCalculationType;
	}

	public Date getNewExecuteTime() {
		return newExecuteTime;
	}

	public void setNewExecuteTime(Date newExecuteTime) {
		this.newExecuteTime = newExecuteTime;
	}

	public boolean isResetNumberGenerator() {
		return resetNumberGenerator;
	}

	public void setResetNumberGenerator(boolean resetNumberGenerator) {
		this.resetNumberGenerator = resetNumberGenerator;
	}

	public void setCallback(Callback<SaldoExportParam> callback) {
		this.callback = callback;
	}

}