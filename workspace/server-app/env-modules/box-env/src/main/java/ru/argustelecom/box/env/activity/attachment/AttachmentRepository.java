package ru.argustelecom.box.env.activity.attachment;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;

import ru.argustelecom.box.env.activity.attachment.model.Attachment;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.inf.login.EmployeePrincipal;
import ru.argustelecom.box.inf.service.Repository;

@Repository
public class AttachmentRepository {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService sequence;

	public Attachment createAttachment(String fileName, String sourceFileName, String mimeType, byte[] body) {
		return createAttachment(fileName, sourceFileName, mimeType, getCurrentEmployee(), body);
	}

	public Attachment createAttachment(String fileName, String sourceFileName, String mimeType, Employee author,
			byte[] body) {
		Attachment attachment = new Attachment(sequence.nextValue(Attachment.class), author);
		attachment.setFileName(fileName);
		attachment.setSourceFileName(sourceFileName);
		attachment.setMimeType(mimeType);
		attachment.setAttachment(((Session) em.getDelegate()).getLobHelper().createBlob(body));

		return attachment;
	}

	protected Employee getCurrentEmployee() {
		EmployeePrincipal principal = checkNotNull(EmployeePrincipal.instance());
		return em.find(Employee.class, principal.getEmployeeId());
	}
}
