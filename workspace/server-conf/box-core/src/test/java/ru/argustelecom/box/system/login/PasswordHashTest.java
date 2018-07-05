package ru.argustelecom.box.system.login;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import ru.argustelecom.box.inf.login.PasswordHash;
import ru.argustelecom.box.inf.login.PasswordHash.Algorithm;
import ru.argustelecom.system.inf.exception.SystemException;

public class PasswordHashTest {

	private static final String password1 = "password1";
	private static final String password2 = "password2";
	private static final byte[] salt1 = "salt1".getBytes();
	private static final byte[] salt2 = "salt2".getBytes();

	@Test
	public void shouldGenerateSalt() {
		PasswordHash hash = new PasswordHash(password1, Algorithm.SHA256);
		assertNotNull(hash.getSalt());
	}

	@Test
	public void shouldGenerateEquivalentHashes() {
		PasswordHash hash1 = new PasswordHash(password1, salt1, Algorithm.SHA256);
		PasswordHash hash2 = new PasswordHash(password1, salt1, Algorithm.SHA256);
		assertEquals(hash1, hash2);
	}

	@Test
	public void shouldGenerateDifferentHashes() {
		PasswordHash hash1 = new PasswordHash(password1, salt1, Algorithm.SHA256);
		PasswordHash hash2 = new PasswordHash(password1, salt2, Algorithm.SHA256);
		assertNotEquals(hash1, hash2);

		hash1 = new PasswordHash(password1, salt1, Algorithm.SHA256);
		hash2 = new PasswordHash(password2, salt1, Algorithm.SHA256);
		assertNotEquals(hash1, hash2);

		hash1 = new PasswordHash(password1, salt1, Algorithm.SHA256);
		hash2 = new PasswordHash(password2, salt2, Algorithm.SHA256);
		assertNotEquals(hash1, hash2);
	}

	@Test
	public void shouldEqualsWithPresentHash() {
		PasswordHash hash = new PasswordHash(password1, salt1, Algorithm.SHA256);

		PasswordHash present = new PasswordHash(hash.getHash(), hash.getSalt());
		assertEquals(hash, present);

		present = new PasswordHash(hash.getHash());
		assertEquals(hash, present);
	}

	@Test(expected = SystemException.class)
	public void shouldThrownSystemException() {
		new PasswordHash(password1, salt1, Algorithm.get("UNKNOWN"));
	}

	@Test
	public void shouldSupportStandardAlgorithms() {
		for (Algorithm algorithm : Algorithm.values()) {
			PasswordHash hash = new PasswordHash(password1, algorithm);
			assertNotNull(hash.getHash());
		}
	}

	@Test
	public void shouldReturnFixedLengthHash() {
		for (Algorithm algorithm : Algorithm.values()) {
			assertHashLength(algorithm, algorithm.getHashLength());
		}
	}

	private void assertHashLength(Algorithm algorithm, int hashLength) {
		int iterationCount = 512;

		String pass = "";
		char firstChar = 0x0021;
		char lastChar = 0x007e;
		char nextChar = firstChar;

		while (iterationCount > 0) {
			pass += nextChar;
			PasswordHash hash = new PasswordHash(pass, algorithm);
			assertEquals(hash.getHash().length(), hashLength);

			nextChar++;
			if (nextChar > lastChar)
				nextChar = firstChar;
			iterationCount--;
		}
	}
}
