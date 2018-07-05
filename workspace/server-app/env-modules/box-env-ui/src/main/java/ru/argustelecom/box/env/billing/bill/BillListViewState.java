package ru.argustelecom.box.env.billing.bill;

import static ru.argustelecom.box.env.billing.bill.BillListViewState.BillFilter.BILL_DATE;
import static ru.argustelecom.box.env.billing.bill.BillListViewState.BillFilter.BILL_TYPE;
import static ru.argustelecom.box.env.billing.bill.BillListViewState.BillFilter.BROKER;
import static ru.argustelecom.box.env.billing.bill.BillListViewState.BillFilter.CUSTOMER;
import static ru.argustelecom.box.env.billing.bill.BillListViewState.BillFilter.CUSTOMER_TYPE;
import static ru.argustelecom.box.env.billing.bill.BillListViewState.BillFilter.END_DATE;
import static ru.argustelecom.box.env.billing.bill.BillListViewState.BillFilter.NUMBER;
import static ru.argustelecom.box.env.billing.bill.BillListViewState.BillFilter.PERIOD_TYPE;
import static ru.argustelecom.box.env.billing.bill.BillListViewState.BillFilter.PERIOD_UNIT;
import static ru.argustelecom.box.env.billing.bill.BillListViewState.BillFilter.PROVIDER;
import static ru.argustelecom.box.env.billing.bill.BillListViewState.BillFilter.START_DATE;
import static ru.argustelecom.box.env.billing.bill.model.BillPeriodType.CALENDARIAN;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.fromLocalDateTime;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.toLocalDateTime;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.billing.bill.model.BillPeriod;
import ru.argustelecom.box.env.billing.bill.model.BillPeriodType;
import ru.argustelecom.box.env.customer.CustomerDto;
import ru.argustelecom.box.env.customer.CustomerDtoTranslator;
import ru.argustelecom.box.env.customer.CustomerTypeDto;
import ru.argustelecom.box.env.customer.CustomerTypeDtoTranslator;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.filter.FilterMapEntry;
import ru.argustelecom.box.env.filter.FilterViewState;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.system.inf.page.PresentationState;

/**
 * Класс для хранения состояния списка счётов.
 */
@Getter
@Setter
@PresentationState
public class BillListViewState extends FilterViewState {

	@FilterMapEntry(NUMBER)
	private String number;

	@FilterMapEntry(value = CUSTOMER_TYPE, translator = CustomerTypeDtoTranslator.class)
	private CustomerTypeDto customerType;

	@FilterMapEntry(value = CUSTOMER, translator = CustomerDtoTranslator.class)
	private CustomerDto customer;

	@FilterMapEntry(value = PROVIDER)
	private BusinessObjectDto<PartyRole> provider;

	@FilterMapEntry(value = BROKER)
	private BusinessObjectDto<Owner> broker;

	@FilterMapEntry(value = BILL_DATE)
	private Date billDate;

	@FilterMapEntry(value = BILL_TYPE, translator = BillTypeDtoTranslator.class)
	private BillTypeDto billType;

	@FilterMapEntry(value = PERIOD_TYPE)
	private BillPeriodType periodType;

	@FilterMapEntry(value = PERIOD_UNIT)
	private PeriodUnit periodUnit;

	private BillPeriod period;

	@FilterMapEntry(value = START_DATE)
	private Date startDate;

	@FilterMapEntry(value = END_DATE)
	private Date endDate;

	/**
	 * Сеттер перекрыт т.к. необходимо при изменении типа периода, в случае если он стал календарным, сбросить всю
	 * оставшуюся информацию по датам начала/окончания формирования счётов.
	 */
	public void setPeriodType(BillPeriodType periodType) {
		this.periodType = periodType;

		if (periodType == null || (periodType.equals(CALENDARIAN) && !CALENDARIAN.equals(this.periodType))) {
			periodUnit = null;
			period = null;
			startDate = null;
			endDate = null;
		}
	}

	/**
	 * Создаёт экземпляр периода, по выбранной единице и дате начала формирования счётов(принимая её за точку интереса).
	 * Необходит так как период явным образом не участвует в фильтрации счётов, нужны только даны начала и окончания.
	 */
	public BillPeriod getPeriod() {
		if (periodUnit != null && period == null && startDate != null) {
			period = BillPeriod.of(periodUnit, toLocalDateTime(startDate));
		}
		return period;
	}

	/**
	 * Если фильтрация происходит по календарному периоду, то в UI заполниться информация о периоде, из которого нам
	 * надо вытащить его граничные даты и заполнить ими даты начала и окончания формирования счётов. Необходит так как
	 * период явным образом не участвует в фильтрации счётов, нужны только даны начала и окончания.
	 */
	public void setPeriod(BillPeriod period) {
		this.period = period;
		if (period != null) {
			startDate = fromLocalDateTime(period.startDateTime());
			endDate = fromLocalDateTime(period.endDateTime());
		}
	}

	static class BillFilter {

		static final String NUMBER = "NUMBER";
		static final String CUSTOMER_TYPE = "CUSTOMER_TYPE";
		static final String CUSTOMER = "CUSTOMER";
		static final String PROVIDER = "PROVIDER";
		static final String BROKER = "BROKER";
		static final String BILL_TYPE = "BILL_TYPE";
		static final String BILL_DATE = "BILL_DATE";
		static final String PERIOD_TYPE = "PERIOD_TYPE";
		static final String PERIOD_UNIT = "PERIOD_UNIT";
		static final String START_DATE = "START_DATE";
		static final String END_DATE = "END_DATE";
	}

	private static final long serialVersionUID = 5806242311964360525L;

}