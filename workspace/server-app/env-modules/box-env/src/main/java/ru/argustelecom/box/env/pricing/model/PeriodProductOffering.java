package ru.argustelecom.box.env.pricing.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static ru.argustelecom.box.env.pricing.model.PricelistState.CREATED;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.util.Currency;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import lombok.Builder;
import lombok.Getter;
import ru.argustelecom.box.env.billing.provision.model.AbstractProvisionTerms;
import ru.argustelecom.box.env.privilege.model.PrivilegeType;
import ru.argustelecom.box.env.product.model.AbstractProductType;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.stl.period.PeriodDuration;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuerySimpleFilter;

/**
 * Длительное ("на период") продуктовое предложение
 * <p>
 *
 */
@Entity
@Access(AccessType.FIELD)
public class PeriodProductOffering extends ProductOffering {

	private static final long serialVersionUID = -2574670210682160526L;

	@Getter
	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "amount", column = @Column(name = "period_amount")),
			@AttributeOverride(name = "unit", column = @Column(name = "period_unit")) })
	private PeriodDuration period;

	/**
	 * Тип привилегии, которая предоставляется с продуктовым предложением.
	 */
	@Getter
	@Enumerated(EnumType.STRING)
	@Column(length = 64)
	private PrivilegeType privilegeType;

	/**
	 * Настройки для длительности привилегии, которая предоставляется с продуктовым предложением.
	 */
	@Getter
	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "amount", column = @Column(name = "privilege_amount")),
			@AttributeOverride(name = "unit", column = @Column(name = "privilege_unit")) })
	private PeriodDuration privilegeDuration;

	protected PeriodProductOffering() {
	}

	@Builder
	protected PeriodProductOffering(Long id, AbstractPricelist pricelist, int orderNum, AbstractProductType productType,
			AbstractProvisionTerms provisionTerms, Money price, Currency currency, PeriodDuration volume) {
		super(id, pricelist, orderNum, productType, provisionTerms, price, currency);
		changeVolume(volume);
	}

	public void changeVolume(PeriodDuration volume) {
		this.period = checkRequiredArgument(volume, "volume");
	}

	/**
	 * Изменяет настройки для привилегии продуктового предложения. Изменение возможно только в случае если прайс-лист
	 * продуктового предложения находится в оформлении.
	 */
	public void setPrivilegeParams(PrivilegeType type, int amount, PeriodUnit unit) {
		checkNotNull(type);
		checkNotNull(unit);
		checkArgument(amount > 0);
		checkArgument(CREATED.equals(getPricelist().getState()));

		if (!hasSamePrivilegeParams(type, amount, unit)) {
			this.privilegeType = type;
			this.privilegeDuration = PeriodDuration.of(amount, unit);
		}
	}

	/**
	 * Удаляет настройки для привилегии. Изменение возможно только в случае если прайс-лист продуктового предложения
	 * находится в оформлении.
	 */
	public void removePrivilegeParams() {
		checkArgument(CREATED.equals(getPricelist().getState()));

		privilegeType = null;
		privilegeDuration = null;
	}

	/**
	 * Сравнивает идентичность настроек привилегии.
	 */
	private boolean hasSamePrivilegeParams(PrivilegeType type, int amount, PeriodUnit unit) {
		boolean emptyPrivilegeParams = privilegeType == null;

		if (emptyPrivilegeParams) {
			return type == null;
		} else {
			boolean equalType = privilegeType.equals(type);
			boolean equalAmount = privilegeDuration.getAmount() == amount;
			boolean equalUnit = privilegeDuration.getUnit().equals(unit);
			return equalType && equalAmount && equalUnit;
		}
	}

	public static class PeriodProductOfferingQuery<T extends PeriodProductOffering> extends ProductOfferingQuery<T> {

		private EntityQuerySimpleFilter<T, PrivilegeType> privilegeType;

		public PeriodProductOfferingQuery(Class<T> entityClass) {
			super(entityClass);
			privilegeType = createFilter(PeriodProductOffering_.privilegeType);
		}

		public EntityQuerySimpleFilter<T, PrivilegeType> privilegeType() {
			return privilegeType;
		}

	}

}