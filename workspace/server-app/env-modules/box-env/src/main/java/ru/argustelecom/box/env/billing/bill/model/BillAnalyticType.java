package ru.argustelecom.box.env.billing.bill.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.billing.bill.nls.BillAnalyticMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

@Getter
@Setter
@Entity
@Access(AccessType.FIELD)
public class BillAnalyticType extends AbstractBillAnalyticType {

	@Enumerated(EnumType.STRING)
	private AnalyticCategory analyticCategory;

	@Enumerated(EnumType.STRING)
	private ChargesType chargesType;

	@Enumerated(EnumType.STRING)
	private BillDateGetter billDateGetter;

	private Boolean isRow;

	protected BillAnalyticType() {
		super();
	}

	public BillAnalyticType(long id) {
		super(id);
	}

	public enum AnalyticCategory {
		CHARGE, INCOME, BALANCE;

		public String getName() {
			BillAnalyticMessagesBundle messages = LocaleUtils.getMessages(BillAnalyticMessagesBundle.class);
			switch (this) {
			case CHARGE:
				return messages.charge();
			case INCOME:
				return messages.income();
			case BALANCE:
				return messages.balance();
			default:
				throw new SystemException("Unsupported AnalyticCategory");
			}
		}
	}

	private static final long serialVersionUID = 8687085265551944012L;
}
