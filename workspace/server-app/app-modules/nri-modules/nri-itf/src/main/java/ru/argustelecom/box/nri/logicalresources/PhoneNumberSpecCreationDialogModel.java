package ru.argustelecom.box.nri.logicalresources;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecification;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecificationRepository;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * Контроллер диалога создания спецификации телефонного номера
 * Created by s.kolyada on 31.10.2017.
 */
@PresentationModel
public class PhoneNumberSpecCreationDialogModel implements Serializable {

	private static final long serialVersionUID = -6080540983649523620L;

	/**
	 * Репозиторий достпа к спекам телефонных номеров
	 */
	@Inject
	private PhoneNumberSpecificationRepository phoneNumberSpecificationRepository;

	/**
	 * Имя новой спеки
	 */
	@Getter
	@Setter
	private String newName;

	/**
	 * Маска
	 */
	@Getter
	@Setter
	private String newMask = "(***)***-**-**";

	/**
	 * Новое время в сутках после которого можно переводить из временно заблокированно в доступно
	 */
	@Getter
	@Setter
	private int newBlockedInterval = 0;


	/**
	 * Описание новой спеки
	 */
	@Getter
	@Setter
	private String newDescription;

	/**
	 * Очистить параметры
	 */
	public void cleanParams() {
		newName = null;
		newDescription = null;
	}

	/**
	 * Создать спецификацию телефонного номера
	 * @return спецификацию тел.номера
	 */
	public PhoneNumberSpecification create() {
		PhoneNumberSpecification newCustomerSpec = phoneNumberSpecificationRepository.createPhoneNumberSpec(newName, newDescription,newMask,newBlockedInterval);
		cleanParams();
		return newCustomerSpec;
	}
}
