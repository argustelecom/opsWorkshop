package ru.argustelecom.box.env.pricing;


import static ru.argustelecom.box.env.billing.provision.ProvisionTermsDto.ProvisionTermsType.RECURRENT;
import static ru.argustelecom.box.env.billing.provision.model.RecurrentTermsState.ACTIVE;
import static ru.argustelecom.box.env.stl.period.PeriodType.CALENDARIAN;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.primefaces.context.RequestContext;

import ru.argustelecom.box.env.billing.provision.ProvisionTermsAppService;
import ru.argustelecom.box.env.billing.provision.ProvisionTermsDto;
import ru.argustelecom.box.env.billing.provision.ProvisionTermsDtoTranslator;
import ru.argustelecom.box.env.billing.provision.model.AbstractProvisionTerms;
import ru.argustelecom.box.env.billing.provision.model.NonRecurrentTerms;
import ru.argustelecom.box.env.billing.provision.model.RecurrentTerms;
import ru.argustelecom.box.env.measure.MeasureUnitAppService;
import ru.argustelecom.box.env.measure.MeasureUnitDto;
import ru.argustelecom.box.env.measure.MeasureUnitDtoTranslator;
import ru.argustelecom.box.env.pricing.model.PeriodProductOffering;
import ru.argustelecom.box.env.pricing.model.ProductOffering;
import ru.argustelecom.box.env.product.ProductDto;
import ru.argustelecom.box.env.product.ProductDtoTranslator;
import ru.argustelecom.box.env.product.ProductTypeAppService;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "productOfferingEditDm")
@PresentationModel
public class ProductOfferingEditDialogModel implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private ProductOfferingAppService productOfferingAs;

	@Inject
	private MeasureUnitAppService measureUnitAs;

	@Inject
	private ProductTypeAppService productTypeAs;

	@Inject
	private ProvisionTermsAppService provisionTermsAs;

	@Inject
	private MeasureUnitDtoTranslator measureUnitDtoTr;

	@Inject
	private ProductOfferingDtoTranslator productOfferingDtoTr;

	@Inject
	private ProductOfferingEditDtoTranslator productOfferingEditDtoTr;

	@Inject
	private ProvisionTermsDtoTranslator provisionTermsDtoTr;

	@Inject
	private ProductDtoTranslator productDtoTr;

	private Callback<ProductOfferingDto> callback;
	private Long pricelistId;
	private Long editableProductOfferingId;
	private ProductOfferingEditDto productOfferingEditDto;

	private List<ProductDto> productTypes;
	private List<MeasureUnitDto> measureUnits;
	private List<ProvisionTermsDto> provisionTermsList;

	public void onDialogOpen() {
		RequestContext.getCurrentInstance().update("product_offering_edit_form-product_offering_edit_dlg");
		RequestContext.getCurrentInstance().execute("PF('productOfferingEditDlgVar').show()");
		if (!isEditableMode())
			productOfferingEditDto = new ProductOfferingEditDto();
	}

	public void submit() {
		if (!isEditableMode())
			callback.execute(productOfferingDtoTr.translate(create()));
		else {
			change();
			callback.execute(productOfferingDtoTr.translate(em.find(ProductOffering.class, editableProductOfferingId)));
		}

		cancel();
	}

	public void cancel() {
		callback = null;
		pricelistId = null;
		editableProductOfferingId = null;
		productOfferingEditDto = null;
	}

	public void onProvisionTermsChanged() {
		if (isSelectedCalendarPeriod())
			productOfferingEditDto.setAmount(1L);
		else
			productOfferingEditDto.setAmount(null);
	}

	public boolean isSelectedCalendarPeriod() {
		return productOfferingEditDto.getProvisionTerms() != null
				&& productOfferingEditDto.getProvisionTerms().getType().equals(RECURRENT)
				&& productOfferingEditDto.getProvisionTerms().getPeriodType().equals(CALENDARIAN);
	}

	public List<ProductDto> getPossibleProducts() {
		if (productTypes == null)
			productTypes = productTypeAs.findProductTypes().stream().map(productDtoTr::translate)
					.collect(Collectors.toList());
		return productTypes;
	}

	public List<ProvisionTermsDto> getProvisionTermsList() {
		if (provisionTermsList == null)
			provisionTermsList = provisionTermsAs.findAll().stream().filter(this::checkAvailableTerms)
					.map(provisionTermsDtoTr::translate).collect(Collectors.toList());
		return provisionTermsList;
	}

	public List<MeasureUnitDto> getMeasureUnits() {
		if (measureUnits == null)
			measureUnits = measureUnitAs.findAllMeasureUnits().stream().map(measureUnitDtoTr::translate)
					.collect(Collectors.toList());
		return measureUnits;
	}

	public List<PeriodUnit> getPeriodUnits() {
		return productOfferingEditDto.getProvisionTerms() != null
				&& productOfferingEditDto.getProvisionTerms().getType().equals(RECURRENT)
						? Arrays.asList(
								productOfferingEditDto.getProvisionTerms().getPeriodType().getAccountingPeriodUnits())
						: Collections.emptyList();
	}

	public void setEditableProductOfferingId(Long id) {
		if (!Objects.equals(editableProductOfferingId, id)) {
			this.editableProductOfferingId = id;
			productOfferingEditDto = productOfferingEditDtoTr
					.translate(em.find(ProductOffering.class, editableProductOfferingId));
		}
	}

	public ProductOfferingEditDto getProductOfferingEditDto() {
		return productOfferingEditDto;
	}

	public void setCallback(Callback<ProductOfferingDto> callback) {
		this.callback = callback;
	}

	public void setPricelistId(Long pricelistId) {
		this.pricelistId = pricelistId;
	}

	private boolean checkAvailableTerms(AbstractProvisionTerms terms) {
		AbstractProvisionTerms initTerms = EntityManagerUtils.initializeAndUnproxy(terms);
		return (initTerms instanceof RecurrentTerms && ((RecurrentTerms) initTerms).inState(ACTIVE))
				|| initTerms instanceof NonRecurrentTerms;
	}

	private ProductOffering create() {
		switch (productOfferingEditDto.getProvisionTerms().getType()) {
		case RECURRENT:
			//@formatter:off
			PeriodProductOffering periodProductOffering = productOfferingAs.createPeriodProductOffering(
					pricelistId,
					productOfferingEditDto.getProduct().getId(),
					productOfferingEditDto.getAmount(),
					productOfferingEditDto.getPeriodUnit(),
					productOfferingEditDto.getProvisionTerms().getId(),
					productOfferingEditDto.getPrice(),
					Currency.getInstance(ru.argustelecom.box.env.stl.Currency.getDefault().name())
			);
			productOfferingAs.changePrivilegeParams(
					periodProductOffering.getId(),
					productOfferingEditDto.getPrivilegeType(),
					productOfferingEditDto.getPrivilegeAmount(),
					productOfferingEditDto.getPrivilegeUnit()
			);
			return periodProductOffering;
			//@formatter:on
		case NON_RECURRENT:
			//@formatter:off
			return productOfferingAs.createMeasureProductOffering(
					pricelistId,
					productOfferingEditDto.getProduct().getId(),
					productOfferingEditDto.getAmount(),
					productOfferingEditDto.getMeasureUnit().getId(),
					productOfferingEditDto.getProvisionTerms().getId(),
					productOfferingEditDto.getPrice(),
					Currency.getInstance(ru.argustelecom.box.env.stl.Currency.getDefault().name())
			);
			//@formatter:on
		default:
			throw new SystemException(String.format("Unsupported provision terms: '%s'",
					productOfferingEditDto.getProvisionTerms().getType()));
		}
	}

	private void change() {
		//@formatter:off
		productOfferingAs.changeProductOffering(
				productOfferingEditDto.getId(),
				productOfferingEditDto.getProduct().getId(),
				productOfferingEditDto.getPrice(),
				Currency.getInstance(ru.argustelecom.box.env.stl.Currency.getDefault().name())
		);
		//@formatter:on

		switch (productOfferingEditDto.getProvisionTerms().getType()) {
		case RECURRENT:
			//@formatter:off
			productOfferingAs.changePeriodVolume(
					productOfferingEditDto.getProvisionTerms().getId(),
					productOfferingEditDto.getId(),
					productOfferingEditDto.getAmount(),
					productOfferingEditDto.getPeriodUnit()
			);

			productOfferingAs.changePrivilegeParams(
					productOfferingEditDto.getId(),
					productOfferingEditDto.getPrivilegeType(),
					productOfferingEditDto.getPrivilegeAmount(),
					productOfferingEditDto.getPrivilegeUnit()
			);
			//@formatter:on
			break;
		case NON_RECURRENT:
			//@formatter:off
				productOfferingAs.changeMeasureVolume(
						productOfferingEditDto.getId(),
						productOfferingEditDto.getAmount(),
						productOfferingEditDto.getMeasureUnit().getId()
				);
				//@formatter:on
			break;
		default:
			throw new SystemException(String.format("Unsupported provision terms: '%s'",
					productOfferingEditDto.getProvisionTerms().getType()));
		}
	}

	// аналогичный метод в других диалогах публичный и он необходим для вью
	public boolean isEditableMode() {
		return productOfferingEditDto != null && productOfferingEditDto.getId() != null;
	}

	private static final long serialVersionUID = -7302323422599933103L;

}