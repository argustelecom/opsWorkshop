package ru.argustelecom.box.env.pricing.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.criteria.Predicate;

import ru.argustelecom.box.env.party.model.CustomerSegment;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapperDef;
import ru.argustelecom.box.publang.productdirectory.model.ICommonPricelist;

/**
 * <a href="http://boxwiki.argustelecom.ru:10753/pages/viewpage.action?pageId=3538979">Публичный прайс-лист</a>
 */
@Entity
@Access(AccessType.FIELD)
@EntityWrapperDef(name = ICommonPricelist.WRAPPER_NAME)
public class CommonPricelist extends AbstractPricelist {

	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(schema = "system", name = "pricelist_customer_segments", joinColumns = {
			@JoinColumn(name = "pricelist_id") }, inverseJoinColumns = { @JoinColumn(name = "segment_id") })
	private List<CustomerSegment> customerSegments = new ArrayList<>();

	protected CommonPricelist() {
	}

	public CommonPricelist(Long id, Owner owner) {
		super(id, owner);
	}

	public List<CustomerSegment> getCustomerSegments() {
		return Collections.unmodifiableList(customerSegments);
	}

	public boolean addCustomerSegment(CustomerSegment segment) {
		if (!customerSegments.contains(segment)) {
			customerSegments.add(segment);
			return true;
		}
		return false;
	}

	public boolean removeCustomerSegment(CustomerSegment segment) {
		return customerSegments.remove(segment);
	}

	@Override
	public boolean isSuitableForCustomer(Customer customer) {
		if (customerSegments.isEmpty()) {
			return true;
		}
		for (CustomerSegment customerSegment : customerSegments) {
			if (customerSegment.getCustomerType().equals(customer.getTypeInstance().getType())) {
				return true;
			}
		}

		return false;
	}

	private static final long serialVersionUID = 1011052486614299266L;

	// *****************************************************************************************************************
	// Inner classes
	// *****************************************************************************************************************

	public static class CommonPricelistQuery extends PricelistQuery<CommonPricelist> {

		public CommonPricelistQuery() {
			super(CommonPricelist.class);
		}

		public Predicate byCustomerSegment(CustomerSegment segment) {
			return criteriaBuilder().isMember(segment, root().get(CommonPricelist_.customerSegments));
		}

	}

}