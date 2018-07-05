package ru.argustelecom.box.env.telephony.tariff;

import lombok.Getter;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
@Named(value = "tariffAttrFm")
public class TariffAttrFrameModel implements Serializable {

	@Inject
	private TariffAppService tariffAs;

	@Getter
	private TariffDto tariff;

	public void preRender(TariffDto tariff) {
		if (!Objects.equals(this.tariff, tariff)) {
			this.tariff = tariff;
		}
	}

	public void saveName() {
		tariffAs.updateTariffName(tariff.getId(), tariff.getName());
	}

	public void save() {
		tariffAs.updateTariffDates(tariff.getId(), tariff.getValidFrom(), tariff.getValidTo());
		tariffAs.updateTariffRatedUnit(tariff.getId(), tariff.getRatedUnit());
		tariffAs.updateTariffRoundPolicy(tariff.getId(), tariff.getRoundingPolicy());
	}

	public List<PeriodUnit> getPossiblePeriodValues() {
		return Arrays.asList(PeriodUnit.SECOND, PeriodUnit.MINUTE);
	}


	private static final long serialVersionUID = 2518834801652123273L;
}