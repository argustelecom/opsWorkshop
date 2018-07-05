package ru.argustelecom.box.env.billing.bill;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.box.env.billing.bill.BillSendingAppService.EmailContextBands.Bill;
import static ru.argustelecom.box.env.billing.bill.BillSendingAppService.EmailContextBands.CompanyInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.logging.Logger;

import com.google.common.collect.Lists;

import ru.argustelecom.box.env.billing.bill.model.Bill;
import ru.argustelecom.box.env.message.mail.Attachment;
import ru.argustelecom.box.env.message.mail.MailService;
import ru.argustelecom.box.env.message.mail.SendingMailException;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.report.api.ReportContext;
import ru.argustelecom.box.env.report.api.ReportOutputFormat;
import ru.argustelecom.box.env.report.api.ReportPattern;
import ru.argustelecom.box.env.report.api.ReportProcessor;
import ru.argustelecom.box.env.report.api.ReportTemplateFormat;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.exception.SystemException;

@ApplicationService
public class BillSendingAppService implements Serializable {

	private static final Logger log = Logger.getLogger(BillSendingAppService.class);

	private static final long serialVersionUID = -2672987482894161889L;

	private static final String MAIL_SUBJECT = "Счет на оплату";

	private static final String CHARSET_NAME = "UTF-8";

	@PersistenceContext
	private EntityManager em;

	@Inject
	private BillReportAppService billReportAppService;

	@Inject
	private ReportProcessor processor;

	@Inject
	private MailService mailService;

	@Inject
	private BillSendingInfoRepository sendingInfoRepo;

	public void send(Long billId, String senderName, String email) throws SendingMailException {
		checkArgument(billId != null, "billId is required");
		checkArgument(email != null, "email is required");

		try (ByteArrayOutputStream emailOs = new ByteArrayOutputStream()) {
			Bill bill = em.find(Bill.class, billId);
			InputStream billIs = billReportAppService.generateReport(billId);
			String attachName = String.format("%s_%s.pdf", bill.getDocumentNumber(), bill.getDocumentDate());
			Attachment attach = new Attachment(attachName, billIs, "application/pdf");
			Date sendingDate = new Date();
			Owner owner = findOwner(bill);
			processor.process(createEmailPattern(owner), createEmailContext(bill, owner), emailOs);
			mailService.sendMail(email, MAIL_SUBJECT, senderName, emailOs.toString(CHARSET_NAME),
					Lists.newArrayList(attach));
			sendingInfoRepo.save(em.getReference(Bill.class, billId), sendingDate, email);

			log.debugf("Счёт '%s', отправлен на: '%s', в '%s'", bill.getDocumentNumber(), email, sendingDate);

		} catch (IOException | BillReportException | SQLException | SendingMailException e) {
			throw new SendingMailException(e);
		}
	}

	private ReportPattern createEmailPattern(Owner owner) throws SQLException {
		checkState(owner.getEmailTemplate() != null, "email template not found");
		InputStream in = owner.getEmailTemplate().getBinaryStream();
		return ReportPattern.builder().templateFormat(ReportTemplateFormat.HTML).templateContent(in)
				.name(owner.getEmailTemplateName()).reportOutputFormat(ReportOutputFormat.HTML).build();
	}

	private ReportContext createEmailContext(Bill bill, Owner owner) {
		ReportContext emailContext = new ReportContext();
		emailContext.put(CompanyInfo.toString(), owner.createReportData());
		emailContext.put(Bill.toString(), bill.createReportData());
		return emailContext;
	}

	private Owner findOwner(Bill bill) {
		if (bill.getBroker() != null) {
			PartyRole inizializedBroker = EntityManagerUtils.initializeAndUnproxy(bill.getBroker());
			if (inizializedBroker instanceof Owner) {
				return (Owner) inizializedBroker;
			}
		}

		PartyRole inizializedProvider = EntityManagerUtils.initializeAndUnproxy(bill.getProvider());
		if (inizializedProvider instanceof Owner) {
			return (Owner) inizializedProvider;
		}

		throw new SystemException("Owner not found");
	}

	public enum EmailContextBands {
		Bill, CompanyInfo;
	}

}
