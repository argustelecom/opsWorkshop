package ru.argustelecom.box.env.pricing.model;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.util.Currency;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;

import lombok.Builder;
import lombok.Getter;
import ru.argustelecom.box.env.billing.provision.model.AbstractProvisionTerms;
import ru.argustelecom.box.env.billing.provision.model.NonRecurrentTerms;
import ru.argustelecom.box.env.measure.model.MeasuredValue;
import ru.argustelecom.box.env.product.model.AbstractProductType;
import ru.argustelecom.box.env.stl.Money;

/**
 * Единовременное продуктовое предложение
 * <p>
 *
 */
@Entity
@Access(AccessType.FIELD)
public class MeasuredProductOffering extends ProductOffering {

	private static final long serialVersionUID = -3110114412397344569L;

	@Getter
	@Embedded
	@AttributeOverride(name = "storedValue", column = @Column(name = "measure_value"))
	@AssociationOverride(name = "storedUnit", joinColumns = { @JoinColumn(name = "measure_unit_id") })
	private MeasuredValue measuredValue;

	protected MeasuredProductOffering() {
	}

	@Builder
	protected MeasuredProductOffering(Long id, AbstractPricelist pricelist, int orderNum,
			AbstractProductType productType, AbstractProvisionTerms provisionTerms, Money price, Currency currency,
			MeasuredValue volume) {
		super(id, pricelist, orderNum, productType, provisionTerms, price, currency);
		changeVolume(volume);
	}

	public void changeVolume(MeasuredValue volume) {
		this.measuredValue = checkRequiredArgument(volume, "volume");
	}

	@Override
	protected void checkNewTerms(AbstractProvisionTerms provisionTerms) {
		super.checkNewTerms(provisionTerms);
		checkArgument(provisionTerms instanceof NonRecurrentTerms);
	}
}