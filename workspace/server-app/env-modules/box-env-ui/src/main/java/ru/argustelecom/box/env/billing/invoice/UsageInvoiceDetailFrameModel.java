package ru.argustelecom.box.env.billing.invoice;

import lombok.Getter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.invoice.model.UsageInvoiceEntryData;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.env.telephony.tariff.model.TelephonyZone;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
@Named("usageInvoiceDetailFm")
public class UsageInvoiceDetailFrameModel implements Serializable {

	private static final long serialVersionUID = -4349761473505203291L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Getter
	private List<UsageInvoiceEntryData> entries;

	public void preRender(UsageInvoiceEntryDto entriesDto) {
		this.entries = entriesDto.getEntries();
	}

	public BusinessObjectDto<AbstractTariff> getTariffById(Long id) {
		return businessObjectDtoTr.translate(em.find(AbstractTariff.class, id));
	}

	public BusinessObjectDto<TelephonyZone> getZoneById(Long id) {
		return businessObjectDtoTr.translate(em.find(TelephonyZone.class, id));
	}

	public Money getMoneyByAmount(BigDecimal amount) {
		return new Money(amount);
	}
}
