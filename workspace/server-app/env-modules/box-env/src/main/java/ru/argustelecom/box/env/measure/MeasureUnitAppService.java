package ru.argustelecom.box.env.measure;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import ru.argustelecom.box.env.measure.model.MeasureUnit;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class MeasureUnitAppService implements Serializable {

	@Inject
	private MeasureUnitRepository measureUnitRp;

	public List<MeasureUnit> findAllMeasureUnits() {
		return measureUnitRp.findAll();
	}

	private static final long serialVersionUID = 781065424750017213L;

}