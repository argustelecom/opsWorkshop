package ru.argustelecom.box.env.party.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class PersonNameTest {

	private static final String PREFIX = "господин";
	private static final String FIRST_NAME = "Чезаре";
	private static final String SECOND_NAME = "Родригович";
	private static final String LAST_NAME = "Борджиа";
	private static final String SUFFIX = "генерал";

	@Test
	public void shouldCreateMinimalPersonName() {
		PersonName name = PersonName.of(FIRST_NAME, LAST_NAME);

		assertNotNull(name);
		assertNull(name.prefix());
		assertEquals(FIRST_NAME, name.firstName());
		assertNull(name.secondName());
		assertEquals(LAST_NAME, name.lastName());
		assertNull(name.suffix());
	}

	@Test
	public void shouldCreateStandardPersonName() {
		PersonName name = PersonName.of(FIRST_NAME, SECOND_NAME, LAST_NAME);

		assertNotNull(name);
		assertNull(name.prefix());
		assertEquals(FIRST_NAME, name.firstName());
		assertEquals(SECOND_NAME, name.secondName());
		assertEquals(LAST_NAME, name.lastName());
		assertNull(name.suffix());
	}

	@Test
	public void shouldCreateFullPersonName() {
		PersonName name = PersonName.of(PREFIX, FIRST_NAME, SECOND_NAME, LAST_NAME, SUFFIX);

		assertNotNull(name);
		assertEquals(PREFIX, name.prefix());
		assertEquals(FIRST_NAME, name.firstName());
		assertEquals(SECOND_NAME, name.secondName());
		assertEquals(LAST_NAME, name.lastName());
		assertEquals(SUFFIX, name.suffix());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailCreationWhenFirstNameNotSpecified() {
		PersonName.of(null, LAST_NAME);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailCreationWhenLastNameNotSpecified() {
		PersonName.of(FIRST_NAME, null);
	}

	@Test
	public void shouldComposeNames() {
		PersonName name = PersonName.of(PREFIX, FIRST_NAME, SECOND_NAME, LAST_NAME, SUFFIX);
		assertEquals("Ч.Р. Борджиа", name.shortInitials());
		assertEquals("Борджиа Ч.Р.", name.shortInitials(true));
		assertEquals("Чезаре Родригович Борджиа", name.shortName());
		assertEquals("Борджиа Чезаре Родригович", name.shortName(true));
		assertEquals("господин Ч.Р. Борджиа, генерал", name.fullInitials());
		assertEquals("господин Борджиа Ч.Р., генерал", name.fullInitials(true));
		assertEquals("господин Чезаре Родригович Борджиа, генерал", name.fullName());
		assertEquals("господин Борджиа Чезаре Родригович, генерал", name.fullName(true));
		assertEquals("Чезаре Родригович", name.officialAppeal());

		name = PersonName.of(null, FIRST_NAME, SECOND_NAME, LAST_NAME, SUFFIX);
		assertEquals("Ч.Р. Борджиа", name.shortInitials());
		assertEquals("Чезаре Родригович Борджиа", name.shortName());
		assertEquals("Ч.Р. Борджиа, генерал", name.fullInitials());
		assertEquals("Борджиа Ч.Р., генерал", name.fullInitials(true));
		assertEquals("Чезаре Родригович Борджиа, генерал", name.fullName());
		assertEquals("Борджиа Чезаре Родригович, генерал", name.fullName(true));

		name = PersonName.of(null, FIRST_NAME, null, LAST_NAME, SUFFIX);
		assertEquals("Ч. Борджиа", name.shortInitials());
		assertEquals("Борджиа Ч.", name.shortInitials(true));
		assertEquals("Чезаре Борджиа", name.shortName());
		assertEquals("Борджиа Чезаре", name.shortName(true));
		assertEquals("Ч. Борджиа, генерал", name.fullInitials());
		assertEquals("Борджиа Ч., генерал", name.fullInitials(true));
		assertEquals("Чезаре Борджиа, генерал", name.fullName());
		assertEquals("Борджиа Чезаре, генерал", name.fullName(true));
		assertEquals("Чезаре", name.officialAppeal());

		name = PersonName.of(null, FIRST_NAME, null, LAST_NAME, null);
		assertEquals("Ч. Борджиа", name.shortInitials());
		assertEquals("Чезаре Борджиа", name.shortName());
		assertEquals("Ч. Борджиа", name.fullInitials());
		assertEquals("Борджиа Ч.", name.fullInitials(true));
		assertEquals("Чезаре Борджиа", name.fullName());
		assertEquals("Борджиа Чезаре", name.fullName(true));
	}

	@Test
	public void shouldCapitalizeComposedNames() {
		PersonName name = PersonName.of(PREFIX, FIRST_NAME.toUpperCase(), SECOND_NAME, LAST_NAME.toLowerCase(), null);
		assertEquals("Ч.Р. Борджиа", name.shortInitials());
		assertEquals("Чезаре Родригович Борджиа", name.shortName());
		assertEquals("господин Ч.Р. Борджиа", name.fullInitials());
		assertEquals("господин Чезаре Родригович Борджиа", name.fullName());
	}

}
