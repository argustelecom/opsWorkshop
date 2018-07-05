package ru.argustelecom.box.env.billing.invoice.model;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.account.model.Reserve;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryDateFilter;

@Entity
@Access(AccessType.FIELD)
public abstract class RegularInvoice extends AbstractInvoice {

	private static final long serialVersionUID = -4241458508630473567L;

	/**
	 * Если в соответствии с текущими условиями предоставления поддерживается функциональность резервирования средств на
	 * лицевом счете, то открытый инвойс будет содержать ссылку на объект типа Reserve, который, в свою очередь, влияет
	 * на доступные средства на лицевом счете.
	 *
	 * @see #detachReserve()
	 */
	@Getter
	@Setter
	@OneToOne(fetch = FetchType.LAZY)
	private Reserve reserve;

	/**
	 * Планируемая дата начала действия инвойса. Может отличаться от даты начала тарификации вследствие применения
	 * политики округления.
	 *
	 */
	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;

	/**
	 * Планируемая дата завершения инвойса. Может отличаться от даты окончания тарификации вследствие применения
	 * политики округления.
	 *
	 */
	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;

	/**
	 * Стоимость без учёта скидок и прочих модификаторов.
	 *
	 */
	@Getter
	@Setter
	@Embedded
	@AttributeOverride(name = "amount", column = @Column(name = "price"))
	private Money price;

	/**
	 * Конструктор для JPA
	 */
	protected RegularInvoice() {
	}

	/**
	 * Основной конструктор, предназначен для инстанцирования инвойса
	 *
	 * @param id
	 *            - идентификатор инвойса, должен быть определен извне
	 * @param personalAccount
	 *            - лицевой счет, на котором необходимо создать инвойс
	 */
	protected RegularInvoice(Long id, PersonalAccount personalAccount) {
		super(id, personalAccount);
	}

	/**
	 * Отцепляет резерв от текущего инвойса
	 *
	 * @return резерв, если он поддерживался или null, если резервирование было запрещено
	 */
	public Reserve detachReserve() {
		Reserve current = getReserve();
		setReserve(null);
		return current;
	}

	/**
	 * Ассоциирует переданный объект Reserve с текущим инвойсом
	 */
	protected void doAttachReserve(Reserve reserve) {
		setReserve(reserve);
		updateReserve();
	}

	/**
	 * Если поддерживается резервирование, то обновляет сумму резерва по расчитанной {@link #getTotalPrice()}
	 */
	protected void updateReserve() {
		Money total = getTotalPrice();
		if (reserve != null && !Objects.equals(reserve.getAmount(), total)) {
			reserve.updateAmount(total);
		}
	}

	public static class RegularInvoiceQuery<T extends RegularInvoice> extends InvoiceQuery<T> {

		private EntityQueryDateFilter<T> startDate;
		private EntityQueryDateFilter<T> endDate;

		public RegularInvoiceQuery(Class<T> entityClass) {
			super(entityClass);
			startDate = createDateFilter(RegularInvoice_.startDate);
			endDate = createDateFilter(RegularInvoice_.endDate);
		}

		public EntityQueryDateFilter<T> startDate() {
			return startDate;
		}

		public EntityQueryDateFilter<T> endDate() {
			return endDate;
		}
	}
}
