package ru.argustelecom.box.env.billing.bill;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import lombok.Getter;
import ru.argustelecom.box.env.billing.bill.dto.BillAttributesDto;
import ru.argustelecom.box.env.billing.bill.dto.ReportModelTemplateDto;
import ru.argustelecom.box.env.billing.bill.nls.BillMessagesBundle;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.BusinessException;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.modelbase.Identifiable;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "billAttributesFm")
@PresentationModel
public class BillAttributesFrameModel implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private BillCardViewStateModel billCardViewStateModel;

	@Inject
	private BillAppService billAppService;

	@Inject
	private BillReportAppService billReportAppService;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Getter
	private BillAttributesDto billAttributesDto;

	@Getter
	private String selectedTemplateName;

	@Getter
	private Identifiable group;

	@Getter
	private BusinessObjectDto<PartyRole> provider;

	@Getter
	private BusinessObjectDto<Owner> broker;

	public void preRender() {
		initAttributes();
		initGroup();
		this.selectedTemplateName = billAttributesDto.getReportModelTemplateDto() != null
				? billAttributesDto.getReportModelTemplateDto().getFileName() : null;

	}

	public void onRecalculate() {
		RequestContext.getCurrentInstance().update("bill_recalculation_form");
		RequestContext.getCurrentInstance().execute("PF('billRecalculationDlgVar').show()");
	}

	public void onNumberChange() {
		billAppService.changeBillNumber(billCardViewStateModel.getReference(billCardViewStateModel.getBill()),
				billAttributesDto.getNumber());
	}

	public void onTemplateChange() {
		billAppService.changeBillTemplate(billCardViewStateModel.getReference(billCardViewStateModel.getBill()),
				billAttributesDto.getReportModelTemplateDto());
	}

	public void onSend() {
		RequestContext.getCurrentInstance().update("bill_recalculation_form");
		RequestContext.getCurrentInstance().execute("PF('billSendDlgVar').show()");
	}

	public StreamedContent onExport() {
		if (!checkBillHasTemplate()) {
			BillMessagesBundle messages = LocaleUtils.getMessages(BillMessagesBundle.class);
			throw new BusinessException(messages.billDoesNotHaveTemplate());
		}
		try {
			InputStream billIs = billReportAppService.generateReport(billAttributesDto.getId());
			String fileName = String.format("%s_%s.pdf", billAttributesDto.getNumber(),
					billAttributesDto.getBillDate());
			return new DefaultStreamedContent(billIs, "application/pdf", fileName);
		} catch (BillReportException e) {
			throw new SystemException("Bill generation error ", e);
		}
	}

	private void initAttributes() {
		billAttributesDto = billCardViewStateModel.getBill() != null
				? billAppService.getBillAttributesDtoFromBill(
						billCardViewStateModel.getReference(billCardViewStateModel.getBill()))
				: billAppService.getBillAttributesDtoFromBillHistory(
						billCardViewStateModel.getReference(billCardViewStateModel.getBillHistoryItem()));
	}

	private void initGroup() {
		Class<? extends Identifiable> groupingClass = billAttributesDto.getGroupingMethod().getEntityClass();
		group = em.find(groupingClass, billAttributesDto.getGroupId());

		provider = businessObjectDtoTr.translate(em.find(PartyRole.class, billAttributesDto.getProviderId()));
		if (billAttributesDto.getBrokerId() != null) {
			broker = businessObjectDtoTr.translate(em.find(Owner.class, billAttributesDto.getBrokerId()));
		}
	}

	private boolean checkBillHasTemplate() {
		return billAttributesDto.getReportModelTemplateDto() != null;
	}

	// TODO т.к. editableArea вызывает метод toString() для отображения лэйбла, у ConvertibleDto этот метод final.
	// Вынести на обсуждение
	public void setSelectedTemplateName(String selectedTemplateName) {
		billAttributesDto.getBillTypeDto().getReportTemplates().stream()
				.filter(reportTemplateDto -> reportTemplateDto.getFileName().equals(selectedTemplateName))
				.findFirst().ifPresent(billAttributesDto::setReportModelTemplateDto);
		this.selectedTemplateName = selectedTemplateName;
	}

	public List<String> removeRepeatedSelectedItem() {
		List<ReportModelTemplateDto> reportModelTemplateDtos = new ArrayList<>(
				billAttributesDto.getBillTypeDto().getReportTemplates());
		reportModelTemplateDtos.remove(billAttributesDto.getReportModelTemplateDto());
		return reportModelTemplateDtos.stream().map(ReportModelTemplateDto::getFileName).collect(Collectors.toList());
	}

	private static final long serialVersionUID = 2170193150660873612L;

}
