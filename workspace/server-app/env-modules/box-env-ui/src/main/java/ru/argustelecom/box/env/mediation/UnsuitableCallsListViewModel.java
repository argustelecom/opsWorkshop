package ru.argustelecom.box.env.mediation;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.FaceletContext;
import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.commodity.ServiceAppService;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.mediation.model.MediationError;
import ru.argustelecom.box.env.mediation.model.ProcessingStage;
import ru.argustelecom.box.env.telephony.tariff.TariffAppService;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.inf.validator.type.HasAttribute;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

import static javax.faces.view.facelets.FaceletContext.FACELET_CONTEXT_KEY;
import static ru.argustelecom.box.env.mediation.model.MediationError.CAN_NOT_DETERMINE_DIRECTION;
import static ru.argustelecom.box.env.mediation.model.MediationError.DIRECTION_FOR_TARIFF_ENTRY_NOT_FOUND;
import static ru.argustelecom.box.env.mediation.model.MediationError.GROOVY;
import static ru.argustelecom.box.env.mediation.model.MediationError.IMPOSSIBLE_APPLY_ADDITIONAL_CONVERSION_RULES;
import static ru.argustelecom.box.env.mediation.model.MediationError.IMPOSSIBLE_IDENTIFY_CUSTOMER_AND_TARIFF;
import static ru.argustelecom.box.env.mediation.model.MediationError.IMPOSSIBLE_IDENTIFY_TARIFF;
import static ru.argustelecom.box.env.mediation.model.MediationError.MISS_REQUIRED_DATA_IN_CDR;
import static ru.argustelecom.box.env.mediation.model.MediationError.SEVERAL_SUITABLE_DIRECTIONS;
import static ru.argustelecom.box.env.mediation.model.MediationError.UNKNOWN;
import static ru.argustelecom.box.env.mediation.model.ProcessingStage.CONV_STAGE_1;
import static ru.argustelecom.box.env.mediation.model.ProcessingStage.CONV_STAGE_2;
import static ru.argustelecom.box.env.mediation.model.ProcessingStage.CONV_STAGE_3;
import static ru.argustelecom.box.env.mediation.model.ProcessingStage.RATING_STAGE;

@PresentationModel
@Named("unsuitableCallsListVm")
public class UnsuitableCallsListViewModel extends ViewModel implements HasAttribute {

	private static final long serialVersionUID = 101562771333002784L;

	@Inject
	@Getter
	private UnsuitableCallsLazyDataModel lazyDm;

	@Inject
	private TariffAppService tariffAs;

	@Inject
	private ServiceAppService serviceAs;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Inject
	private UnsuitableCallsListViewState unsuitableCallsVs;

	private List<BusinessObjectDto<AbstractTariff>> possibleTariffs;

	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
	}

	@Override
	public void preRender() {
		super.preRender();

		Supplier<UnsuitableCallsContext> getContext = () -> {
			FaceletContext faceletContext = ((FaceletContext) FacesContext.getCurrentInstance().getAttributes()
					.get(FACELET_CONTEXT_KEY));

			for (UnsuitableCallsContext unsuitableContext : UnsuitableCallsContext.values()) {
				Boolean attr = (Boolean) getAttribute(unsuitableContext.getAttributeName(), faceletContext).orElse(false);
				if (attr) {
					return unsuitableContext;
				}
			}

			throw new SystemException("Невозможно определить контекст для этапа отсева");
		};

		UnsuitableCallsContext unsuitableCallsContext = getContext.get();

		lazyDm.setUnsuitableCallsContext(unsuitableCallsContext);
		unsuitableCallsVs.setUnsuitableCallsContext(unsuitableCallsContext);
	}

	public List<BusinessObjectDto<AbstractTariff>> getPossibleTariffs() {
		if (possibleTariffs == null) {
			possibleTariffs = businessObjectDtoTr.translate(tariffAs.findNonFormalizationTariffs());
		}
		return possibleTariffs;
	}

	public List<BusinessObjectDto<Service>> getPossibleServices() {
		return businessObjectDtoTr.translate(serviceAs.findAllServices());
	}

	@Getter
	@AllArgsConstructor
	public enum UnsuitableCallsContext {
		//@formatter:off
		CONVERTATION	(Arrays.asList(MISS_REQUIRED_DATA_IN_CDR, GROOVY, UNKNOWN), CONV_STAGE_1, "isConvertation"),
		ANALYSIS		(Arrays.asList(CAN_NOT_DETERMINE_DIRECTION, IMPOSSIBLE_APPLY_ADDITIONAL_CONVERSION_RULES, GROOVY, UNKNOWN), CONV_STAGE_2, "isAnalysis"),
		IDENTIFICATION	(Arrays.asList(IMPOSSIBLE_IDENTIFY_CUSTOMER_AND_TARIFF, IMPOSSIBLE_IDENTIFY_TARIFF, GROOVY, UNKNOWN), CONV_STAGE_3, "isIdentification"),
		CHARGING		(Arrays.asList(DIRECTION_FOR_TARIFF_ENTRY_NOT_FOUND, SEVERAL_SUITABLE_DIRECTIONS, GROOVY, UNKNOWN), RATING_STAGE, "isCharging");
		//@formatter:on

		private List<MediationError> possibleErrors;

		private ProcessingStage stage;
		private String attributeName;
	}
}
