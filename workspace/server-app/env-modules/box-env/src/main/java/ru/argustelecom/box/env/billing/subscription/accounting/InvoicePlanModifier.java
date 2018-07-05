package ru.argustelecom.box.env.billing.subscription.accounting;

import static java.util.Comparator.comparing;
import static ru.argustelecom.system.inf.chrono.DateUtils.after;
import static ru.argustelecom.system.inf.chrono.DateUtils.before;
import static ru.argustelecom.system.inf.chrono.DateUtils.equal;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.function.Predicate;

import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.billing.subscription.accounting.impl.InvoicePlanBuilder;
import ru.argustelecom.box.env.billing.subscription.accounting.impl.InvoicePlanner;
import ru.argustelecom.system.inf.modelbase.Identifiable;
import ru.argustelecom.system.inf.modelbase.NamedObject;

/**
 * Модификатор плана инвойса. Это какой-то абстрактный объект, который действует строго ограниченное время и может
 * выполнять одну из ролей: модифицировать период действия инвойса (доверительный период, пробный период, возможно
 * что-то еще появится в будущем), модифицировать стоимость инвойса (скидка, пробный период, возможно что-то еще
 * появится в будущем)
 */
public interface InvoicePlanModifier extends Identifiable, NamedObject {

	/**
	 * Дата и время начала действия модификатора
	 */
	Date getValidFrom();

	/**
	 * Дата и время окончания действия модификатора
	 */
	Date getValidTo();

	/**
	 * Костыль для возможности игнорировать результаты проверки баланса лицевого счета. Перекрывается в доверительном
	 * периоде.
	 */
	default boolean trustOnBalanceChecking() {
		return false;
	}

	/**
	 * Применяет текущий модификатор к указанному ивнойсу. Это unsafe метод, не используй его напрямую!
	 */
	default void attachTo(LongTermInvoice invoice) {
		// do nothing
	}

	/**
	 * Отменяет действие текущего модификатора для указанного инвойса. Это unsafe метод, не используй его напрямую!
	 */
	default void detachFrom(LongTermInvoice invoice) {
		// do nothing
	}

	/**
	 * Создает предикат для проверки, что указанная точка интереса входит в период модификатора
	 */
	static Predicate<InvoicePlanModifier> containing(final Date poi) {
		return p -> (after(poi, p.getValidFrom()) || equal(poi, p.getValidFrom())) && before(poi, p.getValidTo());
	}

	/**
	 * Создает компаратор для упорядочивания модификаторов по времени, начиная с самого раннего и заканчивая самым
	 * поздним (прямой или натуральный порядок)
	 */
	static Comparator<InvoicePlanModifier> ascendingOrder() {
		return comparing(InvoicePlanModifier::getValidFrom);
	}

	/**
	 * Создает компаратор для упорядочивания модификаторов по времени, начиная с самого позднего и заканчивая самым
	 * ранним (обратный порядок)
	 */
	static Comparator<InvoicePlanModifier> descendingOrder() {
		return ascendingOrder().reversed();
	}

	/**
	 * Модификатор периода. Предназначен для ограничения периода инвойса таким образом, что ПЛАНИРУЕМАЯ дата окончания
	 * инвойса, действующего под влиянием текущего модификатора периода, никогда не будет превышать дату и время
	 * окончания действия этого периода. ВАЖНО, модификатор периода не влияет на дату и время окончания тарификации,
	 * т.е. может сложиться ситуация, при которой инвойс вследствие округления захватил для тарификации базовую единицу
	 * за правой границей этого модификатора.
	 * <p>
	 * Модификатор периода обрабатывается в {@linkplain InvoicePlanner}
	 */
	interface InvoicePlanPeriodModifier extends InvoicePlanModifier {

		/**
		 * Приоритет применения модификатора периода. Если в один момент времени для подписки определено несколько
		 * модификаторов периода, то сначала будет рассматриваться самый приоритетный (чем меньше значение приоритета,
		 * тем он "главнее") и после уже менее приоритетные. ВАЖНО! Модификаторы период одного приоритета не могут
		 * пересекаться
		 */
		default int getPriority() {
			return -1;
		}

		/**
		 * Создает компаратор для упорядочивания модификаторов по приоритету в натуральном порядке (т.е. начиная с
		 * самого главного и т.д.). Может быть скомбинирован с другими компараторами модификаторов
		 */
		static Comparator<InvoicePlanPeriodModifier> ascendingByPriority() {
			return comparing(InvoicePlanPeriodModifier::getPriority);
		}

		/**
		 * Создает компаратор для упорядочивания модификаторов по приоритету в обратном порядке (т.е. начиная с самого
		 * общего и заканчивая самым частным). Может быть скомбинирован с другими компараторами модификаторов
		 */
		static Comparator<InvoicePlanPeriodModifier> descendingByPriority() {
			return ascendingByPriority().reversed();
		}
	}

	/**
	 * Модификатор стоимости инвойса. Предназначен для изменения стоимости базовой единицы в период действия этого
	 * модификатора. Не влияет на даты инвойса. Является единственным фактором возникновения нескольких детализирующих
	 * записей в плане инвойса
	 * <p>
	 * Модификатор стоимости обрабатывается в {@linkplain InvoicePlanBuilder}
	 */
	interface InvoicePlanPriceModifier extends InvoicePlanModifier {

		/**
		 * Множитель стоимости одной базовой единицы. Например, если скидка 30%, то стоимость базовой единицы должна
		 * составлять 0.7 от базовой, таким образом, модификатор стоимости должен равняться 0.7. Если штраф (фантазия)
		 * 30%, то стоимость базовой единицы должна составлять 1.3 от базовой.
		 */
		BigDecimal getPriceFactor();
	}

}
