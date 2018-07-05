package ru.argustelecom.box.env.billing.bill.model;

import java.util.Collections;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;

@Entity
@Access(AccessType.FIELD)
public class SummaryBillAnalyticType extends AbstractBillAnalyticType {

	@Getter
	@Setter
	private Boolean roundNegativeValue;

	/**
	 * Флаг говорящий, нужно ли инвертировать значение аналитики для конечного пользователя.
	 */
	@Getter
	@Setter
	private Boolean invertible;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "summary_type_analytic_type", schema = "system", joinColumns = @JoinColumn(name = "summary_type_id"), inverseJoinColumns = @JoinColumn(name = "analytic_type_id"))
	private List<BillAnalyticType> billAnalyticTypes;

	protected SummaryBillAnalyticType() {
		super();
	}

	public SummaryBillAnalyticType(long id) {
		super(id);
	}

	public List<BillAnalyticType> getBillAnalyticTypes() {
		return Collections.unmodifiableList(billAnalyticTypes);
	}

	private static final long serialVersionUID = -6334698422690752805L;
}
