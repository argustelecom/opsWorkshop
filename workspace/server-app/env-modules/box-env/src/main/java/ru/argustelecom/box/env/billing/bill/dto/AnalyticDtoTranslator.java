package ru.argustelecom.box.env.billing.bill.dto;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.bill.AggData;
import ru.argustelecom.box.env.billing.bill.model.AbstractBillAnalyticType;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class AnalyticDtoTranslator {

	@PersistenceContext
	private EntityManager em;

	public AnalyticDto translate(AggData aggData) {
		AbstractBillAnalyticType abstractBillAnalyticType = em.find(AbstractBillAnalyticType.class,
				aggData.getAnalyticTypeId());
		return new AnalyticDto(aggData.getAnalyticTypeId(), abstractBillAnalyticType.getName(),
				new Money(aggData.getActualSum()));
	}

}