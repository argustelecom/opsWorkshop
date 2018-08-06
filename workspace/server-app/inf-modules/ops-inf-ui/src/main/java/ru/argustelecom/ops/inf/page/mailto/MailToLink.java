package ru.argustelecom.ops.inf.page.mailto;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import ru.argustelecom.ops.inf.validator.EmailValidator;

import com.google.common.base.Strings;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.argustelecom.system.inf.exception.SystemException;

public class MailToLink {

	private Set<Recipient> recipientList = new LinkedHashSet<>();
	private Set<Recipient> ccList = new LinkedHashSet<>();
	private Set<Recipient> bccList = new LinkedHashSet<>();

	private String subject;
	private String body;

	private String href;

	public MailToLink withRecipient(Recipient recipient) {
		this.href = null;
		this.recipientList.add(recipient);
		return this;
	}

	public MailToLink withCc(Recipient cc) {
		this.href = null;
		this.ccList.add(cc);
		return this;
	}

	public MailToLink withBcc(Recipient bcc) {
		this.href = null;
		this.bccList.add(bcc);
		return this;
	}

	public MailToLink withSubject(String subject) {
		this.href = null;
		this.subject = subject;
		return this;
	}

	public MailToLink withBody(String body) {
		this.href = null;
		this.body = body;
		return this;
	}

	public Collection<Recipient> recipientList() {
		return Collections.unmodifiableSet(recipientList);
	}

	public Collection<Recipient> ccList() {
		return Collections.unmodifiableSet(ccList);
	}

	public Collection<Recipient> bccList() {
		return Collections.unmodifiableSet(bccList);
	}

	public String subject() {
		return subject;
	}

	public String body() {
		return body;
	}

	public String href() {
		if (href == null) {
			href = constructLink();
		}
		return href;
	}

	private String constructLink() {
		if (recipientList.isEmpty()) {
			return null;
		}

		StringBuilder outcome = new StringBuilder();

		outcome.append("mailto:");
		append(outcome, recipientList);

		boolean firstParam = true;
		firstParam = append(outcome, firstParam, "cc", ccList);
		firstParam = append(outcome, firstParam, "bcc", bccList);
		firstParam = append(outcome, firstParam, "subject", subject);
		append(outcome, firstParam, "body", body);

		return outcome.toString();
	}

	private void append(StringBuilder outcome, Set<Recipient> recipients) {
		Iterator<Recipient> it = recipients.iterator();
		while (it.hasNext()) {
			it.next().appendTo(outcome);
			if (it.hasNext()) {
				outcome.append(';');
			}
		}
	}

	private boolean append(StringBuilder outcome, boolean firstParam, String paramName, Set<Recipient> paramValues) {
		if (paramValues != null && !paramValues.isEmpty()) {
			outcome.append(firstParam ? '?' : '&');
			outcome.append(paramName).append('=');
			append(outcome, paramValues);
			return false;
		}
		return firstParam;
	}

	private boolean append(StringBuilder outcome, boolean firstParam, String paramName, String paramValue) {
		if (!Strings.isNullOrEmpty(paramValue)) {
			outcome.append(firstParam ? '?' : '&');
			outcome.append(paramName).append('=').append(encode(paramValue));
			return false;
		}
		return firstParam;
	}

	private static String encode(String value) {
		try {
			String encodedValue = URLEncoder.encode(value, "UTF-8");
			return encodedValue.replaceAll("\\+", "%20");
		} catch (UnsupportedEncodingException e) {
			throw new SystemException(e);
		}
	}

	@Getter
	@EqualsAndHashCode(of = "email")
	public static class Recipient {

		private String name;
		private String email;

		private Recipient(String email, String name) {
			checkArgument(EmailValidator.validate(email), "Invalid 'email' argument");
			this.name = name;
			this.email = email;
		}

		public static Recipient of(String email) {
			return new Recipient(email, null);
		}

		public static Recipient of(String email, String name) {
			return new Recipient(email, name);
		}

		public void appendTo(StringBuilder builder) {
			boolean nameAllowed = !Strings.isNullOrEmpty(name);

			if (nameAllowed) {
				builder.append(encode(name));
				builder.append('<');
			}

			builder.append(email);

			if (nameAllowed) {
				builder.append('>');
			}
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			appendTo(sb);
			return sb.toString();
		}
	}
}
