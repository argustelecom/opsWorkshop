package ru.argustelecom.box.env.message.mail;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;

import org.jboss.logging.Logger;

import com.google.common.collect.Lists;

import ru.argustelecom.box.env.message.nls.MessageMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;

@Stateless
public class MailService implements Serializable {

	private static final long serialVersionUID = 4943277687894592026L;

	private static final Logger log = Logger.getLogger(MailService.class);

	@Resource(mappedName = "java:jboss/mail/Default")
	private Session mailSession;

	public void sendMail(String to, String subject, String senderName, String message,
			Collection<Attachment> attachments) throws SendingMailException {
		sendMail(Lists.newArrayList(to), subject, senderName, message, attachments);
	}

	public void sendMail(Collection<String> recipients, String subject, String senderName, String message,
			Collection<Attachment> attachments) throws SendingMailException {
		Transport transport = null;
		try {
			transport = mailSession.getTransport();
			transport.connect();

			MimeMessage mailMessage = new MimeMessage(mailSession);
			mailMessage.setSubject(subject);
			for (String recipient : recipients) {
				mailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
			}

			mailMessage.setFrom(new InternetAddress(mailSession.getProperty("mail.from"), senderName));

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(createMessageBodyPart(message));

			if (attachments != null && !attachments.isEmpty()) {
				for (Attachment attachment : attachments) {
					multipart.addBodyPart(createAttachmentBodyPart(attachment));
				}
			}

			mailMessage.setContent(multipart);

			if (log.isDebugEnabled()) {
				log.debugf("Отправка письма %s ", (Object[]) mailMessage.getAllRecipients());
			}

			transport.sendMessage(mailMessage, mailMessage.getAllRecipients());
		} catch (Exception e) {
			throw new SendingMailException(LocaleUtils.getMessages(MessageMessagesBundle.class).sendMessageError(), e);
		} finally {
			if (transport != null) {
				try {
					transport.close();
				} catch (MessagingException e) {
					log.error("Transport close error ", e);
				}
			}
		}
	}

	private MimeBodyPart createMessageBodyPart(String message) throws MessagingException {
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(message, "text/html;charset=UTF-8");
		return messageBodyPart;
	}

	private MimeBodyPart createAttachmentBodyPart(Attachment attachment) throws MessagingException, IOException {
		MimeBodyPart attachmentPart = new MimeBodyPart();
		attachmentPart.setFileName(MimeUtility.encodeText(attachment.getFileName()));
		attachmentPart.setDisposition(MimeBodyPart.ATTACHMENT);
		DataSource datasource = new ByteArrayDataSource(attachment.getData(), attachment.getMimeType());
		attachmentPart.setDataHandler(new DataHandler(datasource));
		return attachmentPart;
	}
}
