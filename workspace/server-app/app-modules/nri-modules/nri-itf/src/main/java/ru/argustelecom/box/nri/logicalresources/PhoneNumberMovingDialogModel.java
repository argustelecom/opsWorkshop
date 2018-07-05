package ru.argustelecom.box.nri.logicalresources;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberAppService;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberPool;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер диалога перемещения номеров
 *
 * @author d.khekk
 * @since 27.11.2017
 */
@Named("phoneNumberMovingDM")
@PresentationModel
public class PhoneNumberMovingDialogModel implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Список пулов для выбора
	 */
	@Getter
	private List<PhoneNumberPool> pools = new ArrayList<>();

	/**
	 * Выбранный пул
	 */
	@Getter
	@Setter
	private PhoneNumberPool selectedPool;

	/**
	 * Номера телефонов для переноса
	 */
	@Getter
	private List<PhoneNumberDtoTmp> phonesToMove = new ArrayList<>();

	/**
	 * Сервис для операций над телефонными номерами
	 */
	@Inject
	private PhoneNumberAppService service;

	/**
	 * Обработчик события перемещения номеров
	 */
	@Getter
	@Setter
	private Callback<Object> onMoveButtonPressed;

	/**
	 * Действие посте открытия диалога перемещения
	 *
	 * @param pools        список пулов
	 * @param phonesToMove номера телефонов для перемещения
	 */
	public void onMovingDialogOpen(List<PhoneNumberPool> pools, List<PhoneNumberDtoTmp> phonesToMove) {
		this.pools = pools;
		this.phonesToMove = phonesToMove;
	}

	/**
	 * Переместить номера в выбранный пул
	 */
	public void move() {
		for (PhoneNumberDtoTmp numberDto : phonesToMove) {
			service.changePool(numberDto.convertToBaseDto(), selectedPool);
		}
		selectedPool = null;
		onMoveButtonPressed.execute(null);
	}
}
