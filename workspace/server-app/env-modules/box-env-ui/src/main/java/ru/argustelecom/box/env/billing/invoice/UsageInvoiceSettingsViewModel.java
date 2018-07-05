package ru.argustelecom.box.env.billing.invoice;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import ru.argustelecom.box.env.commodity.telephony.TelephonyOptionTypeAppService;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionType;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

import static java.util.Optional.ofNullable;

@Named("usageInvoiceSettingsVm")
@PresentationModel
public class UsageInvoiceSettingsViewModel extends ViewModel {

	@Inject
	private UsageInvoiceSettingsAppService usageInvoiceSettingsAs;

	@Inject
	private UsageInvoiceSettingsTranslator usageInvoiceSettingsTr;

	@Inject
	private TelephonyOptionTypeAppService telephonyOptionTypeAs;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Getter
	private UsageInvoiceSettingsDto settings;

	@Getter
	private List<BusinessObjectDto<TelephonyOptionType>> optionTypes;

	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
		init();
	}

	public void onSave() {
		Long optionTypeId = ofNullable(settings.getOptionType()).map(BusinessObjectDto::getId).orElse(null);
		usageInvoiceSettingsAs.update(settings.getId(), settings.getScheduleUnitAmount(),
				settings.getScheduleUnit(), settings.getScheduleStartTime(), settings.getCloseInvoiceUnitAmount(),
				settings.getCloseInvoiceUnit(), settings.getInvoicePeriodEnd(), settings.getReserveFunds(),
				optionTypeId);
	}

	public void onCancel() {
		init();
	}

	private void init() {
		settings = usageInvoiceSettingsTr.translate(usageInvoiceSettingsAs.find());
	}

	public List<BusinessObjectDto<TelephonyOptionType>> getOptionTypes() {
		if (optionTypes == null) {
			optionTypes = businessObjectDtoTr.translate(telephonyOptionTypeAs.findAll());
		}
		return optionTypes;
	}

	private static final long serialVersionUID = 115641001245691251L;
}
