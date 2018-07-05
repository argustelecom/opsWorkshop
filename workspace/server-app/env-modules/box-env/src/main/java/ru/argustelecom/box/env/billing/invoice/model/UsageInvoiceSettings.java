package ru.argustelecom.box.env.billing.invoice.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Sets.newHashSet;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Collections.unmodifiableSet;
import static javax.persistence.AccessType.FIELD;
import static javax.persistence.EnumType.STRING;
import static org.hibernate.annotations.CacheConcurrencyStrategy.READ_WRITE;
import static ru.argustelecom.box.env.billing.invoice.model.UsageInvoiceSettings.CACHE_REGION_NAME;
import static ru.argustelecom.box.env.stl.period.PeriodDuration.of;
import static ru.argustelecom.box.env.stl.period.PeriodUnit.DAY;
import static ru.argustelecom.box.env.stl.period.PeriodUnit.MONTH;
import static ru.argustelecom.box.env.stl.period.PeriodUnit.WEEK;
import static ru.argustelecom.box.env.stl.period.PeriodUtils.createPeriod;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.fromLocalDateTime;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.toLocalDateTime;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.google.common.collect.Range;
import org.hibernate.annotations.Cache;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionType;
import ru.argustelecom.box.env.stl.period.PeriodDuration;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.env.stl.period.PeriodUtils;
import ru.argustelecom.box.inf.chrono.ChronoUtils;
import ru.argustelecom.box.inf.modelbase.BusinessDirectory;

/**
 * Определяет <a href="http://boxwiki.argustelecom.ru:10753/pages/viewpage.action?pageId=6719033">правила тарификации
 * телефонных вызовов</a>. Хранится только один экземпляр данной сущности.
 */
@Getter
@Setter
@Entity
@Table(schema = "system")
@Access(FIELD)
@Cache(usage = READ_WRITE, region = CACHE_REGION_NAME)
public class UsageInvoiceSettings extends BusinessDirectory {

	public static final String CACHE_REGION_NAME = "ru.argustelecom.box.env-rw-cache-region";
	public static final Set<PeriodUnit> VALID_PERIODS = unmodifiableSet(newHashSet(DAY, WEEK, MONTH));

	/**
	 * Количество единиц измерения для задания расписания обработки
	 */
	private int scheduleUnitAmount;

	/**
	 * Единица измерения для расписания обработки
	 */
	@Enumerated(STRING)
	private PeriodUnit scheduleUnit;

	/**
	 * Время начала обработки
	 */
	@Temporal(TemporalType.TIME)
	private Date scheduleStartTime;

	/**
	 * Количество единиц измерения для расписания закрытия инвойсов
	 */
	private int closeInvoiceUnitAmount;

	/**
	 * Единица измерения для расписания закрытия инвойсов
	 */
	@Enumerated(STRING)
	private PeriodUnit closeInvoiceUnit;

	/**
	 * Тип закрытия инвойса
	 */
	@Enumerated(STRING)
	private InvoicePeriodEnd invoicePeriodEnd;

	/**
	 * Резервирование средств по инвойсам
	 */
	private boolean reserveFunds;

	/**
	 * Опция по умолчанию, к которой будут относиться договоры без поставщиков
	 */
	@OneToOne(optional = false)
	@JoinColumn(name = "telephony_option_type_id")
	private TelephonyOptionType telephonyOptionType;

	protected UsageInvoiceSettings() {
	}

	public UsageInvoiceSettings(Long id) {
		super(id);
	}

	public void setScheduleUnit(PeriodUnit scheduleUnit) {
		checkState(VALID_PERIODS.contains(checkNotNull(scheduleUnit)));
		this.scheduleUnit = scheduleUnit;
	}

	public void setCloseInvoiceUnit(PeriodUnit closeInvoiceUnit) {
		checkState(VALID_PERIODS.contains(checkNotNull(closeInvoiceUnit)));
		this.closeInvoiceUnit = closeInvoiceUnit;
	}

	public Date nextScheduledTime(LocalDateTime poi) {
		Range<LocalDateTime> period = createPeriod(toLocalDateTime(new Date(scheduleStartTime.getTime())), poi,
				of(scheduleUnitAmount, scheduleUnit));
		LocalDateTime next = period.upperEndpoint().plus(1, MILLIS);
		return fromLocalDateTime(next);
	}

	private static final long serialVersionUID = -2947126686319769560L;
}
