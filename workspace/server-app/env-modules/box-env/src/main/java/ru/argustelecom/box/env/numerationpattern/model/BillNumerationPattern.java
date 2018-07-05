package ru.argustelecom.box.env.numerationpattern.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.billing.bill.model.BillType;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;

@Entity
@Access(AccessType.FIELD)
@Getter
@Setter
public class BillNumerationPattern extends NumerationPattern {

	protected BillNumerationPattern() {
	}

	public BillNumerationPattern(Long id) {
		super(id);
	}

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "bill_type_id")
	private BillType billType;

	public static class BillNumerationPatternEntityQuery extends AbstractNumerationPatternQuery<BillNumerationPattern> {

		EntityQueryEntityFilter<BillNumerationPattern, BillType> billType = createEntityFilter(
				BillNumerationPattern_.billType);

		public BillNumerationPatternEntityQuery() {
			super(BillNumerationPattern.class);
		}

		public EntityQueryEntityFilter<BillNumerationPattern, BillType> billType() {
			return billType;
		}
	}

	private static final long serialVersionUID = -4210790490056322978L;
}
