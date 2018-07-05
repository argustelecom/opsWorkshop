package ru.argustelecom.box.env.billing.bill;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.bill.model.Bill;
import ru.argustelecom.box.env.billing.bill.model.GroupingMethod;
import ru.argustelecom.box.env.report.api.ReportContext;
import ru.argustelecom.box.env.report.api.ReportOutputFormat;
import ru.argustelecom.box.env.report.api.ReportPattern;
import ru.argustelecom.box.env.report.api.ReportProcessor;
import ru.argustelecom.box.env.report.api.ReportTemplateFormat;
import ru.argustelecom.box.env.report.model.ReportModelTemplate;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;

@ApplicationService
public class BillReportAppService implements Serializable {

	private static final long serialVersionUID = -6303471096389166857L;

	private static final String BILL_QUERY = "BillReportAppService.billQuery";
	private static final String CONTRACT_QUERY = "BillReportAppService.contractQuery";
	private static final String SUBSCRIPTIONS_QUERY = "BillReportAppService.subscriptionsQuery";

	@PersistenceContext
	private EntityManager em;

	@Inject
	private ReportProcessor processor;

	public InputStream generateReport(Long billId) throws BillReportException {
		checkNotNull(billId, "billId is required");

		try (ByteArrayOutputStream billOs = new ByteArrayOutputStream()) {
			Bill bill = initBill(billId);
			ReportContext reportContext = new ReportContext();
			bill.fillReportContext(reportContext);
			processor.process(createBillReportPattern(bill), reportContext, billOs);
			return new ByteArrayInputStream(billOs.toByteArray());
		} catch (Exception e) {
			throw new BillReportException("Ошибка генерации счета", e);
		}
	}

	private ReportPattern createBillReportPattern(Bill bill) throws SQLException, BillReportException {
		ReportModelTemplate template = bill.getTemplate();
		ReportTemplateFormat templateFormat = ReportTemplateFormat.getReportTemplateFormatBy(template.getMimeType());

		//@formatter:off
		return ReportPattern.builder()
					.templateFormat(templateFormat)
					.templateContent(template.getBinaryStream())
					.reportOutputFormat(ReportOutputFormat.PDF)
					.name(template.getFileName())
				.build();
		//@formatter:on
	}

	@NamedQuery(name = BILL_QUERY, query = "from Bill b " + "join fetch b.type bs " + "left join fetch bs.analytics "
			+ "join fetch bs.propertyHolder " + "join fetch b.customer c " + "join fetch c.typeInstance csi "
			+ "join fetch csi.type cs " + "join fetch cs.propertyHolder " + "join fetch c.party p "
			+ "join fetch p.typeInstance psi " + "join fetch psi.type ps " + "join fetch ps.propertyHolder "
			+ "where b.id = :id")
	private Bill initBill(Long billId) {
		Bill bill = em.createNamedQuery(BILL_QUERY, Bill.class).setParameter("id", billId).getSingleResult();
		if (bill.getGroupingMethod().equals(GroupingMethod.CONTRACT)) {
			initContract(bill.getGroupId());
		}
		List<Long> subsIdList = bill.getAggDataContainer().getDataHolder().getSubscriptionIdList();
		if (subsIdList != null && !subsIdList.isEmpty())
			initSubscriptions(subsIdList);
		return bill;
	}

	@NamedQuery(name = CONTRACT_QUERY, query = "from Contract c" + " join fetch c.type cs"
			+ " join fetch cs.propertyHolder" + " join fetch c.attachmentContext" + " join fetch c.commentContext "
			+ " join fetch c.entries " + " where c.id = :id")
	private void initContract(Long contractId) {
		em.createNamedQuery(CONTRACT_QUERY).setParameter("id", contractId).getResultList();
	}

	@NamedQuery(name = SUBSCRIPTIONS_QUERY, query = "from Subscription s " + "join fetch s.costCause "
			+ "join fetch s.subjectCause " + "where s.id in (:ids)")
	private void initSubscriptions(List<Long> subscriptionIdList) {
		em.createNamedQuery(SUBSCRIPTIONS_QUERY).setParameter("ids", subscriptionIdList).getResultList();
	}

}