package ru.argustelecom.box.pa;

import static ru.argustelecom.box.inf.chrono.ChronoUtils.fromLocalDateTime;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.toLocalDateTime;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;

import com.google.common.collect.Lists;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.transaction.TransactionRepository;
import ru.argustelecom.box.env.billing.transaction.model.Transaction;
import ru.argustelecom.box.inf.nls.LocaleUtils;

@Named(value = "personalAreaTransactionsFM")
@ViewScoped
public class PersonalAreaTransactionsFrameModel implements Serializable {

	private static final long serialVersionUID = 8746179134151496897L;

	@Inject
	private TransactionRepository tr;

	private PersonalAccount personalAccount;

	private Map<Date, List<Transaction>> transactionsMap;
	private List<Date> datesValue;

	private DateFilter dateFilter;
	private Date startDate;
	private Date endDate;

	private Date currentDate = new Date();
	private Date minStartDate = new DateTime().minusYears(1).toDate();

	public void preRender(PersonalAccount account) {
		if (!Objects.equals(personalAccount, account))
			this.personalAccount = account;
	}

	public void applyFilter() {
		initTransactionsMap();
		dateFilter = null;
	}

	public void initTransactionsMap() {
		List<Transaction> transactions = tr.findTransactions(personalAccount, startDate, endDate);
		Collections.sort(transactions, (t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()));

		transactionsMap = new HashMap<>();
		datesValue = new ArrayList<>();

		for (Transaction transaction : transactions) {
			Date dateValue = DateUtils.truncate(transaction.getTransactionDate(), Calendar.DATE);
			if (transactionsMap.containsKey(dateValue))
				transactionsMap.get(dateValue).add(transaction);
			else
				transactionsMap.put(dateValue, Lists.newArrayList(transaction));
		}
		datesValue.addAll(transactionsMap.keySet());
		Collections.sort(datesValue, (date1, date2) -> date2.compareTo(date1));
	}

	public DateFilter[] getDateFilters() {
		return DateFilter.values();
	}

	public void setDateFilter(DateFilter dateFilter) {
		if (dateFilter != null) {
			startDate = dateFilter.calculateStartDate();
			endDate = dateFilter.calculateEndDate();
		}
		this.dateFilter = dateFilter;
	}

	public String getTableEmptyMessage() {
		ResourceBundle personalAreaBundle = LocaleUtils.getBundle("PersonalAreaBundle", getClass());

		if (transactionsMap == null)
			return personalAreaBundle.getString("box.pa.home.history.empty");

		return personalAreaBundle.getString("box.pa.home.history.not_found");
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public Map<Date, List<Transaction>> getTransactionsMap() {
		return transactionsMap;
	}

	public List<Date> getDatesValue() {
		return datesValue;
	}

	public DateFilter getDateFilter() {
		return dateFilter;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		LocalDateTime dateWithLatestTime = toLocalDateTime(endDate).withHour(23).withMinute(59).withSecond(59)
				.withNano(999_999_999);
		this.endDate = fromLocalDateTime(dateWithLatestTime);
	}

	public Date getCurrentDate() {
		return currentDate;
	}

	public Date getMinStartDate() {
		return minStartDate;
	}

	// *****************************************************************************************************************
	// Inner classes
	// *****************************************************************************************************************

	/**
	 * Предзаданные значение фильтров по датам, для фильтрации списка операций. Предусматриваются следующие периоды
	 * фильтрации:
	 * <ul>
	 * <li>"День" - в поле "с" должна быть проставлена текущая дата минус 1 календарный день</li>
	 * <li>"Неделя" - в поле "с" должна быть проставлена текущая дата минус 1 календарная неделя</li>
	 * <li>"Месяц" - в поле "с" должна быть проставлена текущая дата минус 1 календарный месяц</li>
	 * <li>"Год" - в поле "с" должна быть проставлена текущая дата минус 1 календарный год</li>
	 * </ul>
	 *
	 */
	public enum DateFilter {
		//@formatter:off
		TODAY	("box.pa.home.history.today"),
		WEEK	("box.pa.home.history.week"),
		MONTH	("box.pa.home.history.month"),
		YEAR	("box.pa.home.history.year");
		//@formatter:on

		private String desc;

		DateFilter(String name) {
			this.desc = name;
		}

		public String getDesc() {
			return desc;
		}

		public Date calculateStartDate() {
			DateTime date = new DateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0);
			switch (this) {
			case TODAY:
				date = date.minusDays(1);
				break;
			case WEEK:
				date = date.minusDays(7);
				break;
			case MONTH:
				date = date.minusMonths(1);
				break;
			case YEAR:
				date = date.minusYears(1);
				break;
			}
			return date.toDate();
		}

		public Date calculateEndDate() {
			return new Date();
		}

	}

}