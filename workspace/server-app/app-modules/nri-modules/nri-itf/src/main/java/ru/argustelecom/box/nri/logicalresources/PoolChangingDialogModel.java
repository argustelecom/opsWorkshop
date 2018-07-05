package ru.argustelecom.box.nri.logicalresources;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberDto;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberPoolAppService;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberPoolDto;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер диалога смены пула
 *
 * @author b.bazarov
 * @since 24.11.2017
 */
@Named("poolChangingDM")
@PresentationModel
public class PoolChangingDialogModel  implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Действие с пулом после смены
	 */
	@Getter
	@Setter
	private Callback<PhoneNumberDto> afterChange;

	/**
	 * телефонный номер
	 */
	@Getter
	@Setter
	private PhoneNumberDto phoneNumber;

	/**
	 * список всех пулов
	 */
	private List<PhoneNumberPoolDto> allPools;

	/**
	 * Текущий пул
	 */
	@Getter
	@Setter
	private PhoneNumberPoolDto newPool;

	/**
	 * сервис пулов телефонных номеров
	 */
	@Inject
	private PhoneNumberPoolAppService phoneNumberPoolAppService;

	/**
	 * Инициализация диалога
	 */
	@PostConstruct
	public void init(){
		allPools = phoneNumberPoolAppService.findAllLazy();
	}

	/**
	 * Получить возможные пулы для смены
	 * @return возможные пулы для смены
	 */
	public List<PhoneNumberPoolDto> getPossiblePools() {
		List<PhoneNumberPoolDto> possiblePools = new ArrayList<>(allPools);
		if(phoneNumber != null)
			possiblePools.remove(phoneNumber.getPool());
		return possiblePools;
	}

	/**
	 * Сменить пул
	 */
	public void changePool() {
		phoneNumber.setPool(newPool);
		afterChange.execute(phoneNumber);
	}
}
