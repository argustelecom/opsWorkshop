package ru.argustelecom.ops.inf.login;

import static org.bouncycastle.util.encoders.Hex.decode;
import static org.bouncycastle.util.encoders.Hex.encode;

import java.util.Objects;

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.engines.GOST28147Engine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;

import ru.argustelecom.system.inf.exception.SystemException;

/**
 * Инкапсулирует функции хеширвония паролей. Для шифрования используется алгоритм из ГОСТ 28147-89.
 *
 * @author o.naumov
 */
public class PasswordEncrypt {

	private static final byte[] KEY = "prAp?Afre$ec*ephuka2eSTaFReb5As6".getBytes();

	private String encryptedValue;

	/**
	 * Создаёт экземпляр {@link PasswordEncrypt} по зашифрованному значению или шифрует переданное значение пароля.
	 * 
	 * @param encrypted
	 *            true если передано зашифрованное значение.
	 * @param value
	 *            зашифрованое значение или пароль, который надо зашифровать.
	 */
	public PasswordEncrypt(boolean encrypted, String value) {
		if (encrypted)
			this.encryptedValue = value;
		else
			this.encryptedValue = encrypt(value);
	}

	@Override
	public int hashCode() {
		return encryptedValue.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		PasswordEncrypt that = (PasswordEncrypt) obj;
		return Objects.equals(this.encryptedValue, that.encryptedValue);
	}

	/**
	 * @return Зашифрованное значение пароля.
	 */
	public String getEncryptedValue() {
		return encryptedValue;
	}

	/**
	 * @return Расшифрованное значение пароля.
	 */
	public String getDecryptedValue() {
		return decrypt();
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	/**
	 * !!! Использование HEX.encode для получения массива байт обязательно, иначе не будет получено значение пароля.
	 */
	private String encrypt(String rawPassword) {
		return new String(encode(performEncryption(true, rawPassword.getBytes())));
	}

	/**
	 * !!! Использование HEX.decode для получения массива байт обязательно, иначе не будет получено значение пароля.
	 */
	private String decrypt() {
		return new String(performEncryption(false, decode(encryptedValue))).trim();
	}

	/**
	 * Выполняет действие над переданным value - может быть как зашифрованным так и сырым паролем.
	 * 
	 * @param encrypt
	 *            определяет нужно ли зашифровать переданное value.
	 * @param value
	 *            зашифрованные/сырой пароль над которым будет выполняться обратное действие.
	 * @return
	 */
	private byte[] performEncryption(boolean encrypt, byte[] value) {
		BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new GOST28147Engine()));

		cipher.init(encrypt, new KeyParameter(KEY));
		byte[] cipherText = new byte[cipher.getOutputSize(value.length)];

		int outputLength = cipher.processBytes(value, 0, value.length, cipherText, 0);
		try {
			cipher.doFinal(cipherText, outputLength);
			return cipherText;
		} catch (CryptoException ce) {
			throw new SystemException("Can not encrypt password", ce);
		}
	}

}