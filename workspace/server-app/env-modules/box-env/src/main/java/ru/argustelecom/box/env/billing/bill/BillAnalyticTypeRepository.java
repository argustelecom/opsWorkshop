package ru.argustelecom.box.env.billing.bill;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.google.common.collect.Lists;

import ru.argustelecom.box.env.billing.bill.model.AbstractBillAnalyticType;
import ru.argustelecom.box.env.billing.bill.model.BillAnalyticType;
import ru.argustelecom.box.env.billing.bill.model.BillAnalyticType.AnalyticCategory;
import ru.argustelecom.box.env.billing.bill.model.BillPeriod;
import ru.argustelecom.box.env.billing.bill.model.BillType;
import ru.argustelecom.box.env.billing.bill.model.SummaryBillAnalyticType;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.cache.DirectoryCacheService;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.exception.BusinessException;

@Repository
public class BillAnalyticTypeRepository implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private DirectoryCacheService cacheSvc;

	public List<BillAnalyticType> findAllBillAnalyticTypes() {
		return cacheSvc.getDirectoryObjects(BillAnalyticType.class);
	}

	public List<BillAnalyticType> findRowBillAnalyticTypes() {
		return cacheSvc.getDirectoryObjects(BillAnalyticType.class, BillAnalyticType::getIsRow);
	}

	public List<SummaryBillAnalyticType> findAllSummaryBillAnalyticType() {
		return cacheSvc.getDirectoryObjects(SummaryBillAnalyticType.class);
	}

	public List<AbstractBillAnalyticType> findAnalyticTypesByIds(List<Long> ids) {
		return EntityManagerUtils.findList(em, AbstractBillAnalyticType.class, ids);
	}

	public BillAnalyticType findBillAnalyticTypeBy(Long id) {
		checkNotNull(id);
		//@formatter:off
		return cacheSvc.getDirectoryObjects(BillAnalyticType.class)
				.stream()
				.filter(type -> type.getId().equals(id))
				.findFirst()
				.orElseThrow(() -> new BusinessException(format("Invalid BillAnalyticType id: %s", id)));
		//@formatter:on
	}

	/**
	 * Собираем все доступные для данного периода счета типы анатитик, расчёты которых понадобятся при формировании
	 * счёта. Сюда входят:
	 * <ul>
	 * <li>выбранные для спецификации счёта типы аналитик</li>
	 * <li>типы аналитик необходимые для расчёта выбранных итоговых аналитик</li>
	 * <li>типы аналитик необходимые для расчёта итоговой суммы к оплате</li>
	 * <li>типы аналитик - строки счета</li>
	 * </ul>
	 *
	 * @return Уникальный перечень типов аналитик.
	 */
	public Set<BillAnalyticType> find(BillType billType, BillPeriod period, List<AnalyticCategory> categories) {
		Set<BillAnalyticType> analyticTypes = new HashSet<>();

		// добавляем выбранные аналитики
		analyticTypes.addAll(billType.getBillAnalyticTypes());

		// добавляем аналитики из итоговых аналитик
		for (SummaryBillAnalyticType summaryType : billType.getSummaryBillAnalyticTypes()) {
			analyticTypes.addAll(summaryType.getBillAnalyticTypes());
		}

		// добавляем аналитики из итоговой аналитики к оплате
		if (billType.getSummaryToPay() != null) {
			analyticTypes.addAll(billType.getSummaryToPay().getBillAnalyticTypes());
		}

		// Добавляем аналитики - строки счета
		analyticTypes.addAll(findRowBillAnalyticTypes());

		if (categories != null) {
			analyticTypes.removeIf(analyticType -> !categories.contains(analyticType.getAnalyticCategory()));
		}

		if (!period.isIterable()) {
			analyticTypes.removeIf(analyticType -> !analyticType.getAvailableForCustomPeriod());
		}
		return analyticTypes;
	}

	public Set<BillAnalyticType> find(BillType billType, BillPeriod period, AnalyticCategory category) {
		return find(billType, period, Lists.newArrayList(category));
	}

	private static final long serialVersionUID = -6752917523374026866L;
}
