package ru.argustelecom.box.nri.logicalresources.ip.subnet;

import lombok.Getter;
import ru.argustelecom.system.inf.exception.BusinessExceptionWithoutRollback;

/**
 * Выбрасывается если подсеть уже существует
 */
@Getter
public class SubnetAlreadyExistException extends BusinessExceptionWithoutRollback {

	private final Long existSubnetId;

	/**
	 * Конструктор
	 * @param message сообщение
	 * @param existSubnetId id существующей подсети
	 */
	public SubnetAlreadyExistException(String message,Long existSubnetId){
			super(message);
		this.existSubnetId = existSubnetId;
	}
}
