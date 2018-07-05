package ru.argustelecom.box.inf.page.mailto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import ru.argustelecom.box.inf.page.mailto.MailToLink.Recipient;

public class MailToLinkTest {

	@Test
	public void shouldCreateRecipient() {
		Recipient recipient = Recipient.of("jacob.frye@syndicate.com");
		assertEquals("jacob.frye@syndicate.com", recipient.toString());

		recipient = Recipient.of("jacob.frye@syndicate.com", "Jacob Frye");
		assertEquals("Jacob%20Frye<jacob.frye@syndicate.com>", recipient.toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailRecipientCreationWhenEmailInvalid() {
		Recipient.of("not_email");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailRecipientCreationWhenEmailNotPassed() {
		Recipient.of(null);
	}

	@Test
	public void shouldConstructSimpleLink() {
		String href = new MailToLink().withRecipient(Recipient.of("jacob.frye@syndicate.com")).href();
		assertEquals("mailto:jacob.frye@syndicate.com", href);

		href = new MailToLink().withRecipient(Recipient.of("jacob.frye@syndicate.com", "Jacob Frye")).href();
		assertEquals("mailto:Jacob%20Frye<jacob.frye@syndicate.com>", href);
	}

	@Test
	public void shouldConstructLinkWithMultipleRecipients() {
		//@formatter:off
		String href = new MailToLink()
			.withRecipient(Recipient.of("jacob.frye@syndicate.com", "Jacob Frye"))
			.withRecipient(Recipient.of("evie.frye@syndicate.com", "Evie Frye"))
			.href();
		//@formatter:off
		assertEquals("mailto:Jacob%20Frye<jacob.frye@syndicate.com>;Evie%20Frye<evie.frye@syndicate.com>", href);
	}
	
	@Test
	public void shouldConstructComplexLink() {
		//@formatter:off
		String href = new MailToLink()
			.withRecipient(Recipient.of("jacob.frye@syndicate.com", "Jacob Frye"))
			.withCc(Recipient.of("evie.frye@syndicate.com", "Evie Frye"))
			.withBcc(Recipient.of("lydia.frye@syndicate.com", "Lydia Frye"))
			.withSubject("The Creed's maxim")
			.withBody("Nothing is true, everything is permitted.")
			.href();
		//@formatter:off
		assertEquals("mailto:Jacob%20Frye<jacob.frye@syndicate.com>?cc=Evie%20Frye<evie.frye@syndicate.com>&bcc=Lydia%20Frye<lydia.frye@syndicate.com>&subject=The%20Creed%27s%20maxim&body=Nothing%20is%20true%2C%20everything%20is%20permitted.", href);
	}
	
	@Test
	public void shouldConstructComplexLinkWithMultipleCcs() {
		//@formatter:off
		String href = new MailToLink()
			.withRecipient(Recipient.of("jacob.frye@syndicate.com", "Jacob Frye"))
			.withCc(Recipient.of("evie.frye@syndicate.com", "Evie Frye"))
			.withCc(Recipient.of("lydia.frye@syndicate.com", "Lydia Frye"))
			.href();
		//@formatter:off
		assertEquals("mailto:Jacob%20Frye<jacob.frye@syndicate.com>?cc=Evie%20Frye<evie.frye@syndicate.com>;Lydia%20Frye<lydia.frye@syndicate.com>", href);
	}
	
	@Test
	public void shouldConstructComplexLinkWithMultipleBccs() {
		//@formatter:off
		String href = new MailToLink()
			.withRecipient(Recipient.of("jacob.frye@syndicate.com", "Jacob Frye"))
			.withBcc(Recipient.of("evie.frye@syndicate.com", "Evie Frye"))
			.withBcc(Recipient.of("lydia.frye@syndicate.com", "Lydia Frye"))
			.href();
		//@formatter:off
		assertEquals("mailto:Jacob%20Frye<jacob.frye@syndicate.com>?bcc=Evie%20Frye<evie.frye@syndicate.com>;Lydia%20Frye<lydia.frye@syndicate.com>", href);
	}
	
	@Test
	public void shouldConstructEmptyLinkWhenRecipientIsMissing() {
		//@formatter:off
		String href = new MailToLink()
			.withCc(Recipient.of("evie.frye@syndicate.com", "Evie Frye"))
			.withSubject("The Creed's maxim")
			.withBody("Nothing is true, everything is permitted.")
			.href();
		//@formatter:off
		assertNull(href);
	}
	
	@Test
	public void shouldEncodeNoneASCIICharacters() {
		String href = new MailToLink().withRecipient(Recipient.of("jacob.frye@syndicate.com", "Джейкоб Фрай")).href();
		assertEquals("mailto:%D0%94%D0%B6%D0%B5%D0%B9%D0%BA%D0%BE%D0%B1%20%D0%A4%D1%80%D0%B0%D0%B9<jacob.frye@syndicate.com>", href);
	}
}
