package ru.argustelecom.box.nri.logicalresources.ip.address;

import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddress;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddressPurpose;
import ru.argustelecom.box.nri.logicalresources.ip.address.nls.IPAddressAppServiceMessagesBundle;

import javax.inject.Inject;

import static ru.argustelecom.system.inf.utils.CheckUtils.checkNotNull;

/**
 * Сервис доступа к IP-адресам
 *
 * @author d.khekk
 * @since 11.12.2017
 */
@ApplicationService
public class IPAddressAppService {

	/**
	 * Репозиторий доступа к хранилищу телефонных номеров
	 */
	@Inject
	private IPAddressRepository repository;

	/**
	 * Транслятор сущностей в ДТО
	 */
	@Inject
	private IPAddressDtoTranslator translator;

	/**
	 * Сервис генерации айдишников
	 */
	@Inject
	private IdSequenceService idSequenceService;

	/**
	 * Создать новый IP-адрес
	 *
	 * @param ipAddressDto ДТО нового IP-адреса
	 * @return ДТО созданного IP-адреса
	 */
	public IPAddressDto create(IPAddressDto ipAddressDto) {
		IPAddressAppServiceMessagesBundle messages = LocaleUtils.getMessages(IPAddressAppServiceMessagesBundle.class);
		checkNotNull(ipAddressDto.getName(), messages.ipAddressMustHaveName());
		IPAddress ipAddress = IPAddress.builder()
				.id(idSequenceService.nextValue(IPAddress.class))
				.name(ipAddressDto.getName())
				.comment(ipAddressDto.getComment())
				.isStatic(ipAddressDto.getIsStatic())
				.build();
		return translator.translate(repository.create(ipAddress));
	}

	/**
	 * Найти IP-адрес по его ID
	 *
	 * @param id идентификатор IP-адреса
	 * @return найденный адрес
	 */
	public IPAddressDto findOne(Long id) {
		if (id == null)
			return null;
		return translator.translate(repository.findOne(id));
	}

	/**
	 * Изменить комментарий IP-адреса
	 *
	 * @param ipAddressDto ДТО с измененным комментарием
	 */
	public void changeComment(IPAddressDto ipAddressDto) {
		IPAddressAppServiceMessagesBundle messages = LocaleUtils.getMessages(IPAddressAppServiceMessagesBundle.class);
		checkNotNull(ipAddressDto.getId(), messages.ipAddressDoesNotHaveId());
		IPAddress ipAddress = repository.findOneWithRefresh(ipAddressDto.getId());
		ipAddress.setComment(ipAddressDto.getComment());
		repository.save(ipAddress);
	}

	/**
	 * Изменить назначение адреса
	 * @param id идентификатор
	 * @param purpose новое назначение
	 */
	public void changePurpose(Long id, IPAddressPurpose purpose) {
		IPAddressAppServiceMessagesBundle messages = LocaleUtils.getMessages(IPAddressAppServiceMessagesBundle.class);
		checkNotNull(id, messages.idIsMissed());
		checkNotNull(purpose, messages.purposeIsMissed());

		IPAddress ipAddress = repository.findOneWithRefresh(id);
		ipAddress.setPurpose(purpose);
		repository.save(ipAddress);
	}
}
