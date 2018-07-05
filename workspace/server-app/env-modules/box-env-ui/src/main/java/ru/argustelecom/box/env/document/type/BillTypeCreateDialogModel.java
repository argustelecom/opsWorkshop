package ru.argustelecom.box.env.document.type;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static ru.argustelecom.box.env.billing.bill.model.BillPeriodType.CALENDARIAN;
import static ru.argustelecom.box.env.billing.bill.model.BillPeriodType.CUSTOM;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.billing.bill.BillAnalyticTypeAppService;
import ru.argustelecom.box.env.billing.bill.BillTypeAppService;
import ru.argustelecom.box.env.billing.bill.model.BillType;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.party.CustomerTypeAppService;
import ru.argustelecom.box.env.party.OwnerAppService;
import ru.argustelecom.box.env.party.SupplierAppService;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named("billTypeCreateDm")
@PresentationModel
public class BillTypeCreateDialogModel implements Serializable {

	@Inject
	private BillAnalyticTypeAppService billAnalyticTypeAs;

	@Inject
	private BillTypeDtoTranslator billTypeDtoTr;

	@Inject
	private BillTypeAppService billTypeAs;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Inject
	private CustomerTypeAppService customerTypeAs;

	@Inject
	private OwnerAppService ownerAs;

	@Inject
	private SummaryBillAnalyticTypeDtoTranslator summaryBillAnalyticTypeDtoTr;

	@Inject
	private SupplierAppService supplierAs;

	@Setter
	private Callback<DocumentTypeDto> documentTypeCallback;

	@Getter
	private BillTypeCreationDto billType = new BillTypeCreationDto();

	private List<SelectItem> providers;

	private List<BusinessObjectDto<CustomerType>> customerTypes;

	private List<SummaryBillAnalyticTypeDto> summaryBillAnalyticTypes;

	public void create() {
		Long customerTypeId = billType.getCustomerType() != null ? billType.getCustomerType().getId() : null;
		Long summaryAnalyticTypeId = billType.getSummaryBillAnalyticType().getId();
		List<Long> providerIds = billType.getProviders().stream().map(BusinessObjectDto::getId).collect(toList());

		//@formatter:off
		BillType newType = billTypeAs.create(
				billType.getName(),
				customerTypeId,
				billType.getPeriodType(),
				billType.getPeriodUnit(),
				billType.getGroupingMethod(),
				billType.getPaymentCondition(),
				summaryAnalyticTypeId,
				billType.getDescription(),
				providerIds
		);
		//@formatter:on

		documentTypeCallback.execute(billTypeDtoTr.translate(newType));
		reset();
	}

	public void onPeriodTypeChanged() {
		summaryBillAnalyticTypes = null;
		billType.setSummaryBillAnalyticType(null);
	}

	public void reset() {
		billType = new BillTypeCreationDto();
	}

	public List<BusinessObjectDto<CustomerType>> getCustomerTypes() {
		if (customerTypes == null) {
			customerTypes = businessObjectDtoTr.translate(customerTypeAs.findAllCustomerTypes());
		}
		return customerTypes;
	}

	public List<SummaryBillAnalyticTypeDto> getSummaryBillAnalyticTypes() {
		if (billType.getPeriodType() == null) {
			summaryBillAnalyticTypes = new ArrayList<>();
		}

		if (summaryBillAnalyticTypes == null) {
			//@formatter:off
			summaryBillAnalyticTypes
					= billAnalyticTypeAs.findAllSummaryBillAnalyticType().stream()
						.map(summaryBillAnalyticTypeDtoTr::translate)
						.filter(type -> (CUSTOM.equals(billType.getPeriodType()) && type.isAvailableForCustomPeriod())
								|| CALENDARIAN.equals(billType.getPeriodType()))
						.collect(toList());
			//@formatter:on
		}

		return summaryBillAnalyticTypes;
	}

	public List<SelectItem> getProviders() {
		if (providers == null) {
			// @formatter:off
			providers = newArrayList(concat(ownerAs.findAll(), supplierAs.findAll())).stream()
					.map(businessObjectDtoTr::translate)
					.map(role -> new SelectItem(role, role.getObjectName()))
					.collect(toList());
			// @formatter:on
		}
		return providers;
	}

	private static final long serialVersionUID = 6470675705417310932L;
}
