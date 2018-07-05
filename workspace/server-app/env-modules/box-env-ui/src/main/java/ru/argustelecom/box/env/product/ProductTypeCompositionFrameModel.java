package ru.argustelecom.box.env.product;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.primefaces.context.RequestContext;

import ru.argustelecom.box.env.commodity.CommoditySpecRepository;
import ru.argustelecom.box.env.commodity.model.CommoditySpec;
import ru.argustelecom.box.env.commodity.model.CommodityType;
import ru.argustelecom.box.env.commodity.model.ServiceSpec;
import ru.argustelecom.box.env.product.model.AbstractProductType;
import ru.argustelecom.box.env.product.model.ProductType;
import ru.argustelecom.box.env.product.model.ProductTypeComposite;
import ru.argustelecom.box.inf.page.outcome.OutcomeConstructor;
import ru.argustelecom.box.inf.page.outcome.param.IdentifiableOutcomeParam;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "productTypeCompositionFm")
@PresentationModel
public class ProductTypeCompositionFrameModel implements Serializable {

	private static final long serialVersionUID = 2379751603918632341L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private ProductTypeDirectoryViewState productTypeDirectoryViewState;

	@Inject
	private CommoditySpecRepository commoditySpecRp;

	@Inject
	private ProductTypeRepository productTypeRepository;

	@Inject
	private OutcomeConstructor outcomeConstructor;

	private AbstractProductType abstractProductType;

	private List<ProductType> possibleProductTypes;
	private List<CommodityType> possibleCommodityTypes;
	private List<ProductType> selectedProductTypes;
	private List<CommodityType> selectedCommodityTypes;

	public void preRender() {
		refresh();
	}

	public void onAddProductTypeDialogOpen() {
		RequestContext.getCurrentInstance().update("add_product_type_form-add_product_type_dlg");
		RequestContext.getCurrentInstance().execute("PF('addProductTypeDlgVar').show()");
	}

	public List<ProductType> getPossibleProductTypes() {
		if (possibleProductTypes == null) {
			possibleProductTypes = productTypeRepository.getAllSimpleProductTypes();
			possibleProductTypes.removeAll(((ProductTypeComposite) getAbstractProductType()).getCompositeParts());
		}
		return possibleProductTypes;
	}

	public void addProductTypes() {
		ProductTypeComposite productTypeComposite = EntityManagerUtils
				.initializeAndUnproxy((ProductTypeComposite) getAbstractProductType());
		for (AbstractProductType productType : selectedProductTypes) {
			productTypeComposite.addCompositePart((ProductType) productType);
			em.merge(productType);
		}
		em.merge(productTypeComposite);
		cleanParams();
	}

	public void removeProductType(ProductType type) {
		ProductTypeComposite productTypeComposite = EntityManagerUtils
				.initializeAndUnproxy((ProductTypeComposite) getAbstractProductType());
		productTypeComposite.removeProductType(type);
		em.merge(type);
		em.merge(productTypeComposite);
	}

	public void onAddCommodityTypeDialogOpen() {
		RequestContext.getCurrentInstance().update("add_commodity_type_form-add_commodity_type_dlg");
		RequestContext.getCurrentInstance().execute("PF('addCommodityTypeDlgVar').show()");
	}

	public List<CommodityType> getPossibleCommodityTypes() {
		if (possibleCommodityTypes == null && abstractProductType != null) {
			possibleCommodityTypes = productTypeRepository.getPossibleCommodityTypes();
			List<CommodityType> usedCommodityTypes = ((ProductType) abstractProductType).getEntries().stream()
					.map(CommoditySpec::getType).collect(Collectors.toList());
			possibleCommodityTypes.removeAll(usedCommodityTypes);
		}
		return possibleCommodityTypes;
	}

	public void addCommodityTypes() {
		ProductType productType = EntityManagerUtils.initializeAndUnproxy((ProductType) abstractProductType);
		for (CommodityType commodityType : selectedCommodityTypes) {
			CommoditySpec<?> commoditySpec = commoditySpecRp.createCommoditySpec(commodityType);
			productType.addEntry(commoditySpec);
			possibleCommodityTypes.remove(commodityType);
		}
		em.merge(productType);
		cleanParams();
	}

	public void removeCommodityType(CommoditySpec<?> spec) {
		ProductType productType = EntityManagerUtils.initializeAndUnproxy((ProductType) abstractProductType);
		CommodityType commodityType = EntityManagerUtils.initializeAndUnproxy(spec.getType());
		productType.removeCommodityType(commodityType);
		em.merge(productType);
		possibleCommodityTypes.add(commodityType);
	}

	public void cleanParams() {
		selectedProductTypes = null;
		selectedCommodityTypes = null;
	}

	public boolean isCompositeProduct() {
		return getAbstractProductType() != null && getAbstractProductType() instanceof ProductTypeComposite;
	}

	public String linkToServiceSpecificationView(ServiceSpec serviceSpec) {
		return outcomeConstructor.construct("/views/nri/service/ServiceSpecificationView.xhtml",
				IdentifiableOutcomeParam.of("serviceSpecification", serviceSpec));
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private void refresh() {
		possibleCommodityTypes = null;
		possibleProductTypes = null;
		abstractProductType = null;
		AbstractProductUnit currentProductTypeUnit = productTypeDirectoryViewState.getProductTypeUnit();
		if (currentProductTypeUnit != null && !currentProductTypeUnit.isGroup()) {
			abstractProductType = ((AbstractProductTypeUnit) currentProductTypeUnit).getAbstractProductType();
		}
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public AbstractProductType getAbstractProductType() {
		return abstractProductType;
	}

	public List<CommodityType> getSelectedCommodityTypes() {
		return selectedCommodityTypes;
	}

	public void setSelectedCommodityTypes(List<CommodityType> selectedCommodityTypes) {
		this.selectedCommodityTypes = selectedCommodityTypes;
	}

	public List<ProductType> getSelectedProductTypes() {
		return selectedProductTypes;
	}

	public void setSelectedProductTypes(List<ProductType> selectedProductTypes) {
		this.selectedProductTypes = selectedProductTypes;
	}

}