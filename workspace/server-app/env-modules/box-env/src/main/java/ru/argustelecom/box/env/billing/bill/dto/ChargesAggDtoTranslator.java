package ru.argustelecom.box.env.billing.bill.dto;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.bill.model.BillAnalyticType;
import ru.argustelecom.box.env.billing.bill.model.ChargesAgg;
import ru.argustelecom.box.env.commodity.telephony.model.Option;
import ru.argustelecom.box.env.product.model.AbstractProductType;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.system.inf.exception.SystemException;

@DtoTranslator
public class ChargesAggDtoTranslator {

	@PersistenceContext
	private EntityManager em;

	public ChargesAggDto translate(ChargesAgg chargesAgg) {
		String subjectName = null;
		BillAnalyticType analyticType = em.find(BillAnalyticType.class, chargesAgg.getAnalyticTypeId());
		switch (analyticType.getChargesType()) {
		case RECURRENT:
		case NONRECURRENT:
			subjectName = em.find(AbstractProductType.class, chargesAgg.getSubjectId()).getName();
			break;
		case USAGE:
			subjectName = em.find(Option.class, chargesAgg.getSubjectId()).getObjectName();
			break;
		default:
			new SystemException("Unsupported ChargesType");
		}

		//@formatter:off
		return ChargesAggDto.builder()
				.subjectName(subjectName)
				.tax(new Money(chargesAgg.getTax()))
				.sumWithoutTax(new Money(chargesAgg.getSumWithoutTax()))
				.sumWithTax(new Money(chargesAgg.getTax()).add(new Money(chargesAgg.getSumWithoutTax())))
			.build();
		//@formatter:on
	}
}
