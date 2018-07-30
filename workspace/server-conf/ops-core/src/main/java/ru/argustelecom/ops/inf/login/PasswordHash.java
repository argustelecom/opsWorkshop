package ru.argustelecom.ops.inf.login;

import static java.lang.String.format;
import static org.jboss.security.auth.spi.Util.encodeBase16;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;

import org.jboss.security.auth.spi.Util;

import ru.argustelecom.system.inf.exception.SystemException;

/**
 * Инкапсулирует функции хеширования паролей. Поддерживаются все алгоритмы, описанные в {@link MessageDigest}.
 * Используется подход salted hash: перед хешированием пароля накладывается системная соль, после хешируется сам пароль,
 * затем сгенерированная соль (хранится рядом с паролем в БД, для генерации используется алгоритм SHA1PRNG) и в
 * завершении накладывается еще одна системная соль.
 * <p>
 * Знание хеша пароля, его соли, системных констант и даже метода генерации хеша не позволит подобрать правильный
 * исходный пароль, все равно потребуется длительный брутфорс.
 * <p>
 * Системные константы нужны на случай утечки данных о паролях и их хранимых солях из БД.
 * 
 * @author a.shelishkevich
 */
public class PasswordHash {

	private String hash;
	private byte[] salt;

	/**
	 * Создает экземпляр PasswordHash по предрасчитанному ранее хешу. Соль не указывается и не принимается в рассчет.
	 * 
	 * @param hash
	 */
	public PasswordHash(String hash) {
		this.hash = hash;
	}

	/**
	 * Создает экземпляр PasswordHash по предрассчитанным ранее хешу и соли.
	 * 
	 * @param hash
	 * @param salt
	 */
	public PasswordHash(String hash, byte[] salt) {
		this.salt = salt;
		this.hash = hash;
	}

	/**
	 * Создает экземпляр PasswordHash используя в качестве основы "сырой" пароль и указанный алгоритм. При создании
	 * будет сгенерирована соль, а также сразу рассчитается хеш сырого пароля с использованием указанного алгоритма.
	 * 
	 * @param rawPassword
	 * @param algorithm
	 */
	public PasswordHash(String rawPassword, Algorithm algorithm) {
		this.salt = generateSalt();
		this.hash = generateHash(rawPassword, this.salt, algorithm);
	}

	/**
	 * Создает экземпляр PasswordHash используя в качестве основы "сырой" пароль и указанный алгоритм. При создании
	 * будет рассчитан пароль с использованием переданной соли. Если вместо соли передали null или пустой массив, то
	 * соль будет сгенерирована все равно.
	 * 
	 * @param rawPassword
	 * @param salt
	 * @param algorithm
	 */
	public PasswordHash(String rawPassword, byte[] salt, Algorithm algorithm) {
		this.salt = salt != null && salt.length != 0 ? salt : generateSalt();
		this.hash = generateHash(rawPassword, this.salt, algorithm);
	}

	/**
	 * Возвращает рассчитанный хеш
	 * 
	 * @return
	 */
	public String getHash() {
		return hash;
	}

	/**
	 * Возвращает соль, сгенерированную или указанную пользователем (зависит от конструктора, который был вызван для
	 * создания экземпляра)
	 * 
	 * @return
	 */
	public byte[] getSalt() {
		return salt;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Вычисляется только на основе рассчитанного хэша
	 */
	@Override
	public int hashCode() {
		return hash.hashCode();
	}

	/**
	 * {@inheritDoc} Сравнение вернет true в случае, если рассчитанные хеши текущего экземпляра и переданного совпадают.
	 * <p>
	 * Остальные параметры в расчет не принимаются
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		PasswordHash that = (PasswordHash) obj;
		return Objects.equals(this.hash, that.hash);
	}

	private byte[] generateSalt() {
		SecureRandom sr = createSecureRandom(GEN_SALT_ALGORITHM);
		byte[] salt = new byte[64];
		sr.nextBytes(salt);
		return salt;
	}

	/**
	 * ВНИМАНИЕ! Нельзя менять реализацию этого метода, т.к. это может повлиять на успешность авторизации, при
	 * необходимости изменения нужно будет ввести версионность
	 */
	private String generateHash(String rawPassword, byte[] salt, Algorithm algorithm) {
		MessageDigest md = createMessageDigest(algorithm);
		md.update(SYSTEM_SALT_PREFIX);
		md.update(rawPassword.getBytes());
		md.update(salt);
		md.update(SYSTEM_SALT_SUFFIX);
		return encodeBase16(md.digest());
	}

	private MessageDigest createMessageDigest(Algorithm algorithm) {
		String algorithmName = algorithm != null ? algorithm.getAlgorithName() : "UNKNOWN";
		try {
			return MessageDigest.getInstance(algorithmName);
		} catch (NoSuchAlgorithmException e) {
			throw new SystemException(format("Hashing algorithm '%s' not found", algorithmName), e);
		}
	}

	private SecureRandom createSecureRandom(String algorithm) {
		try {
			return SecureRandom.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new SystemException(format("Algorithm '%s' not found", algorithm), e);
		}
	}

	/**
	 * Преобразует соль из байтового массива в строковое представление
	 * 
	 * @return
	 */
	public static String saltToString(byte[] salt) {
		return salt != null ? Util.tob64(salt) : null;
	}

	/**
	 * Преобразует строковое представление соли обратно в байтовый массив
	 * 
	 * @return
	 */
	public static byte[] saltFromString(String salt) {
		return salt != null ? Util.fromb64(salt) : null;
	}

	public enum Algorithm {
		//@formatter:off
		MD2("MD2", 32), 
		MD5("MD5", 32), 
		SHA1("SHA-1", 40), 
		SHA224("SHA-224", 56), 
		SHA256("SHA-256", 64), 
		SHA384("SHA-384", 96), 
		SHA512("SHA-512", 128);
		//@formatter:on

		private String algorithName;
		private int hashLength;

		private Algorithm(String algorithName, int hashLength) {
			this.algorithName = algorithName;
			this.hashLength = hashLength;
		}

		public String getAlgorithName() {
			return algorithName;
		}

		public int getHashLength() {
			return hashLength;
		}

		public static Algorithm get(String algorithName) {
			for (Algorithm algorithm : Algorithm.values()) {
				if (algorithm.getAlgorithName().equalsIgnoreCase(algorithName)) {
					return algorithm;
				}
			}
			return null;
		}
	}

	private static final byte[] SYSTEM_SALT_PREFIX = "prAp?Afre$ec*ephuka2eSTaFReb5As6".getBytes();
	private static final byte[] SYSTEM_SALT_SUFFIX = "w4aGe6egaTrahEgUtaG-cRUchUDA3uXe".getBytes();
	private static final String GEN_SALT_ALGORITHM = "SHA1PRNG";
}
