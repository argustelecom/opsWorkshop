package ru.argustelecom.box.env.pricing.model;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapperDef;
import ru.argustelecom.box.publang.productdirectory.model.ICustomPricelist;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;

/**
 * <a href="http://boxwiki.argustelecom.ru:10753/pages/viewpage.action?pageId=4162397">Индивидуальный прайс-лист</a>
 */
@Entity
@Access(AccessType.FIELD)
@EntityWrapperDef(name = ICustomPricelist.WRAPPER_NAME)
public class CustomPricelist extends AbstractPricelist {

	private static final long serialVersionUID = -5700349515832037487L;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	protected CustomPricelist() {
	}

	public CustomPricelist(Long id, Customer customer, Owner owner) {
		super(id, owner);
		this.customer = checkNotNull(customer);
	}

	public Customer getCustomer() {
		return customer;
	}

	@Override
	public boolean isSuitableForCustomer(Customer customer) {
		return this.customer.equals(customer);
	}

	// *****************************************************************************************************************
	// Inner classes
	// *****************************************************************************************************************

	public static class CustomPricelistQuery extends PricelistQuery<CustomPricelist> {

		private EntityQueryEntityFilter<CustomPricelist, Customer> customer;

		public CustomPricelistQuery() {
			super(CustomPricelist.class);
			customer = createEntityFilter(CustomPricelist_.customer);
		}

		public EntityQueryEntityFilter<CustomPricelist, Customer> customer() {
			return customer;
		}

	}

}