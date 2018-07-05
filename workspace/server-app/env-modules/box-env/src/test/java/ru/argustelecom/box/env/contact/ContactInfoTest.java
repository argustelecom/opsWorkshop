package ru.argustelecom.box.env.contact;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.argustelecom.box.env.stl.EmailAddress;
import ru.argustelecom.box.env.stl.PhoneNumber;
import ru.argustelecom.box.env.stl.SkypeLogin;

public class ContactInfoTest {

	private AtomicLong idSequence;

	private ContactInfo contactInfo;

	private ContactType phoneContactType = createContactType(ContactCategory.PHONE, "Мобильный номер");
	private ContactType emailContactType = createContactType(ContactCategory.EMAIL, "Домашняя почта");
	private ContactType skypeContactType = createContactType(ContactCategory.SKYPE, "Скайп");
	private ContactType customContactType = createContactType(ContactCategory.CUSTOM, "Отзыв на пароль");

	private PhoneNumber phoneNumber = PhoneNumber.create("+78123333660");
	private EmailAddress emailAddress = EmailAddress.create("box@argustelecom.ru");
	private SkypeLogin skypeLogin = SkypeLogin.create("box.team");
	private String customValue = "Тюльпаны зацветают в полночь";

	@Before
	public void setup() {
		contactInfo = new ContactInfo();
	}

	@After
	public void cleanup() {
		contactInfo = null;
	}

	@Test
	public void shouldCreatePhoneContactByContactType() {
		Contact<?> phone = contactInfo.createContact(phoneContactType, phoneNumber, nextId());
		checkContact(phone, phoneContactType, PhoneContact.class, phoneNumber);
		assertEquals(1, contactInfo.getContacts().size());
	}

	@Test
	public void shouldCreateEmailContactByContactType() {
		Contact<?> email = contactInfo.createContact(emailContactType, emailAddress, nextId());
		checkContact(email, emailContactType, EmailContact.class, emailAddress);
		assertEquals(1, contactInfo.getContacts().size());
	}

	@Test
	public void shouldCreateSkypeContactByContactType() {
		Contact<?> skype = contactInfo.createContact(skypeContactType, skypeLogin, nextId());
		checkContact(skype, skypeContactType, SkypeContact.class, skypeLogin);
		assertEquals(1, contactInfo.getContacts().size());
	}

	@Test
	public void shouldCreateCustomContactByContactType() {
		Contact<?> custom = contactInfo.createContact(customContactType, customValue, nextId());
		checkContact(custom, customContactType, CustomContact.class, customValue);
		assertEquals(1, contactInfo.getContacts().size());
	}

	@Test
	public void shouldCreateContactByContactTypeWithTypeCasting() {
		PhoneContact phone = contactInfo.createContact(PhoneContact.class, phoneContactType, phoneNumber, nextId());
		checkContact(phone, phoneContactType, PhoneContact.class, phoneNumber);
		assertEquals(1, contactInfo.getContacts().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailCreationWhenValueTypeMismatch() {
		contactInfo.createContact(phoneContactType, emailAddress, nextId());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailCreationWhenContactClassConflictWithContactType() {
		contactInfo.createContact(EmailContact.class, phoneContactType, emailAddress, nextId());
	}

	@Test
	public void shouldGetContactById() {
		contactInfo.createContact(phoneContactType, phoneNumber, nextId());
		Contact<?> phone = contactInfo.getContact(lastId());
		assertNotNull(phone);
	}

	@Test
	public void shouldGetContactByIdWithTypeCasting() {
		contactInfo.createContact(phoneContactType, phoneNumber, nextId());
		PhoneContact phone = contactInfo.getContact(PhoneContact.class, lastId());
		assertNotNull(phone);
	}

	@Test
	public void shouldReturnNullWhenUnknownIdSpecified() {
		contactInfo.createContact(phoneContactType, phoneNumber, nextId());
		contactInfo.createContact(emailContactType, emailAddress, nextId());

		Contact<?> nullContact = contactInfo.getContact(100500L);
		assertNull(nullContact);
	}

	@Test
	public void shouldReturnNullWhenInvalidClassSpecified() {
		contactInfo.createContact(phoneContactType, phoneNumber, nextId());
		EmailContact email = contactInfo.getContact(EmailContact.class, lastId());
		assertNull(email);

		Contact<?> phone = contactInfo.getContact(lastId());
		assertNotNull(phone);

		email = contactInfo.castContact(EmailContact.class, phone);
		assertNull(email);
	}

	@Test
	public void shouldRemoveContact() {
		contactInfo.createContact(phoneContactType, phoneNumber, nextId());
		contactInfo.createContact(emailContactType, emailAddress, nextId());

		boolean removingResult = contactInfo.remove(contactInfo.getContact(lastId()));
		assertTrue(removingResult);
	}

	private ContactType createContactType(ContactCategory category, String typeName) {
		ContactType result = new ContactType(nextId());
		result.setCategory(category);
		result.setObjectName(typeName);
		result.setName(typeName);
		return result;
	}

	private <C extends Contact<?>> void checkContact(Contact<?> contact, ContactType type, Class<C> clazz, Object value) {
		assertNotNull(contact);
		assertEquals(clazz, contact.getClass());
		assertEquals(type, contact.getType());
		assertEquals(value, contact.getValue());
	}

	private long nextId() {
		if (idSequence == null) {
			idSequence = new AtomicLong(0);
		}
		return idSequence.incrementAndGet();
	}

	private long lastId() {
		return idSequence != null ? idSequence.get() : 0;
	}
}