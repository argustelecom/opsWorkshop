package ru.argustelecom.box.env.telephony.tariff;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.party.CustomerAppService;
import ru.argustelecom.box.env.party.CustomerTypeAppService;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.env.telephony.tariff.TariffListViewState.TariffListMode;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.env.telephony.tariff.model.CommonTariff;
import ru.argustelecom.box.inf.page.outcome.OutcomeConstructor;
import ru.argustelecom.box.inf.page.outcome.param.IdentifiableOutcomeParam;
import ru.argustelecom.system.inf.page.PresentationModel;

import static ru.argustelecom.box.env.telephony.tariff.TariffCardViewModel.*;
import static ru.argustelecom.box.env.telephony.tariff.TariffListViewState.TariffListMode.COMMON;
import static ru.argustelecom.box.env.telephony.tariff.TariffListViewState.TariffListMode.CUSTOM;

@PresentationModel
@Named(value = "tariffCreationDm")
public class TariffCreationDialogModel implements Serializable {

	private static final long serialVersionUID = -341341999574740972L;

	@Inject
	private OutcomeConstructor outcomeConstructor;

	@Inject
	private TariffAppService tariffAs;

	@Inject
	private CustomerAppService customerAs;

	@Inject
	private CustomerTypeAppService customerTypeAs;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Getter
	@Setter
	private TariffCreationDto newTariff;

	@Getter
	@Setter
	private TariffListMode mode;

	private List<BusinessObjectDto<CustomerType>> customerTypes;
	private List<BusinessObjectDto<CommonTariff>> commonTariffs;

	public void onCreationDialogOpen() {
		newTariff = new TariffCreationDto();

		RequestContext.getCurrentInstance().execute("PF('tariffCreationPanelVar').hide()");
		RequestContext.getCurrentInstance().update("tariff_creation_form");
		RequestContext.getCurrentInstance().execute("PF('tariffCreationDlgVar').show()");
	}

	public String onCreated() {
		return outcomeConstructor.construct(VIEW_ID, IdentifiableOutcomeParam.of("tariff", create()));
	}

	private AbstractTariff create() {
		AbstractTariff tariff = null;
		if (mode.equals(COMMON)) {
			tariff = tariffAs.createCommonTariff(
					newTariff.getName(),
					newTariff.getRatedUnit(),
					newTariff.getRoundingPolicy(),
					newTariff.getValidFrom(),
					newTariff.getValidTo()
			);
		}

		if (mode.equals(CUSTOM)) {
			tariff = tariffAs.createCustomTariff(
					newTariff.getName(),
					newTariff.getRatedUnit(),
					newTariff.getRoundingPolicy(),
					newTariff.getValidFrom(),
					newTariff.getValidTo(),
					newTariff.getParent().getId(),
					newTariff.getCustomer().getId()
			);
		}

		cleanCreationParams();
		return tariff;
	}

	public List<BusinessObjectDto<CustomerType>> getCustomerTypes() {
		if (customerTypes == null) {
			customerTypes = businessObjectDtoTr.translate(customerTypeAs.findAllCustomerTypes());
		}

		return customerTypes;
	}

	public List<BusinessObjectDto<CommonTariff>> getCommonTariffs() {
		if (commonTariffs == null) {
			commonTariffs = businessObjectDtoTr.translate(tariffAs.findAvailableCommonTariffs());
		}
		return commonTariffs;
	}

	public void cleanCreationParams() {
		mode = null;
		newTariff = null;
	}

	public List<TariffListMode> getModesForCreationButton() {
		return TariffListMode.getModesForCreation();
	}

	public List<? extends BusinessObjectDto<? extends Customer>> completeCustomer(String customerName) {
		if (newTariff.getCustomerType() != null) {
			return businessObjectDtoTr.translate(customerAs.findCustomerBy(newTariff.getCustomerType().getId(), customerName));
		}

		return Collections.emptyList();
	}

	public List<PeriodUnit> getPossibleRatedUnits() {
		return Arrays.asList(PeriodUnit.SECOND, PeriodUnit.MINUTE);
	}
}
