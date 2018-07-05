package ru.argustelecom.box.env.telephony.tariff.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;

import static com.google.common.base.Preconditions.checkNotNull;

@Entity
@Access(AccessType.FIELD)
public class CustomTariff extends AbstractTariff {

	private static final long serialVersionUID = -6224826865681730058L;

	@Getter
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "parent_tariff_id")
	private CommonTariff parent;

	protected CustomTariff() {
	}

	public CustomTariff(Long id, Customer customer) {
		super(id);
		this.customer = checkNotNull(customer);
	}

	public static class CustomTariffQuery extends TariffQuery<CustomTariff> {

		private EntityQueryEntityFilter<CustomTariff, Customer> customer;
		private EntityQueryEntityFilter<CustomTariff, CommonTariff> parentTariff;

		public CustomTariffQuery() {
			super(CustomTariff.class);
			customer = createEntityFilter(CustomTariff_.customer);
			parentTariff = createEntityFilter(CustomTariff_.parent);
		}

		public EntityQueryEntityFilter<CustomTariff, Customer> customer() {
			return customer;
		}

		public EntityQueryEntityFilter<CustomTariff, CommonTariff> parentTariff() {
			return parentTariff;
		}
	}
}
