package ru.argustelecom.box.env.pricing.model;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.math.BigDecimal;
import java.util.Currency;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import lombok.Getter;
import ru.argustelecom.box.env.billing.provision.model.AbstractProvisionTerms;
import ru.argustelecom.box.env.product.model.AbstractProductType;
import ru.argustelecom.box.env.product.model.ProductTypeComposite;
import ru.argustelecom.box.env.report.api.Printable;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;

/**
 * Продуктовое предложение
 * <p>
 *
 */
@Entity
@Access(AccessType.FIELD)
public abstract class ProductOffering extends BusinessObject implements Printable {

	private static final long serialVersionUID = -1377983503601413462L;

	@Getter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pricelist_id")
	private AbstractPricelist pricelist;

	@Getter
	@Column(nullable = false)
	private int orderNum;

	@Getter
	@Embedded
	@AttributeOverride(name = "amount", column = @Column(name = "price"))
	private Money price;

	@Column(length = 16, nullable = false)
	private String currency;

	@Version
	private Long version;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "product_type_id")
	private AbstractProductType productType;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "provision_terms_id")
	private AbstractProvisionTerms provisionTerms;

	protected ProductOffering() {
	}

	protected ProductOffering(Long id, AbstractPricelist pricelist, int orderNum, AbstractProductType productType,
			AbstractProvisionTerms provisionTerms, Money price, Currency currency) {
		super(id);
		this.pricelist = checkRequiredArgument(pricelist, "pricelist");
		this.orderNum = orderNum;
		changeOffering(productType, price, currency);
		changeTerms(provisionTerms);
	}

	@Override
	public String getObjectName() {
		return productType.getObjectName();
	}

	public Currency getCurrency() {
		return Currency.getInstance(currency);
	}

	public AbstractProductType getProductType() {
		return EntityManagerUtils.initializeAndUnproxy(productType);
	}

	public AbstractProvisionTerms getProvisionTerms() {
		return EntityManagerUtils.initializeAndUnproxy(provisionTerms);
	}

	public Money getPriceWithoutTax() {
		return price.divide(pricelist.getTaxMultiplier().add(BigDecimal.ONE));
	}

	public boolean isRecurrentProduct() {
		return getProvisionTerms().isRecurrent();
	}

	public void changeOffering(AbstractProductType productType, Money price, Currency currency) {
		this.productType = checkRequiredArgument(productType, "productType");
		changeOfferingPrice(price, currency);
	}

	public void changeOfferingPrice(Money price, Currency currency) {
		checkRequiredArgument(price, "price");
		checkArgument(price.compareTo(Money.ZERO) > 0);
		this.price = price;
		this.currency = currency != null ? currency.getCurrencyCode()
				: ru.argustelecom.box.env.stl.Currency.getDefault().name();
	}

	public void changeTerms(AbstractProvisionTerms provisionTerms) {
		checkNewTerms(provisionTerms);
		this.provisionTerms = provisionTerms;
	}

	protected void checkNewTerms(AbstractProvisionTerms provisionTerms) {
		checkRequiredArgument(provisionTerms, "provisionTerms");
	}

	@Override
	public ProductOfferingRdo createReportData() {
		//@formatter:off
		return ProductOfferingRdo.builder()
					.id(getId())
					.product(getProductType().createReportData())
					.value(getPrice().getRoundAmount())
					.valueWithoutTax(getPriceWithoutTax().getRoundAmount())
					.taxValue(getPricelist().getOwner().getTaxRate())
					.currency(getCurrency().getSymbol())
				.build();
		//@formatter:on
	}

	public boolean isComposite() {
		return EntityManagerUtils.initializeAndUnproxy(productType) instanceof ProductTypeComposite;
	}

	public static class ProductOfferingQuery<T extends ProductOffering> extends EntityQuery<T> {

		private EntityQueryEntityFilter<T, AbstractPricelist> pricelist;
		private EntityQueryEntityFilter<T, AbstractProvisionTerms> provisionTerms;
		private EntityQueryEntityFilter<T, AbstractProductType> productType;

		public ProductOfferingQuery(Class<T> entityClass) {
			super(entityClass);
			pricelist = createEntityFilter(ProductOffering_.pricelist);
			provisionTerms = createEntityFilter(ProductOffering_.provisionTerms);
			productType = createEntityFilter(ProductOffering_.productType);
		}

		public EntityQueryEntityFilter<T, AbstractPricelist> pricelist() {
			return pricelist;
		}

		public EntityQueryEntityFilter<T, AbstractProvisionTerms> provisionTerms() {
			return provisionTerms;
		}

		public EntityQueryEntityFilter<T, AbstractProductType> productType() {
			return productType;
		}
	}

}
