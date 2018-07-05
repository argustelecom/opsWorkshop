package ru.argustelecom.box.nri.service;

import lombok.Getter;
import lombok.Setter;
import org.jboss.logging.Logger;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.nri.booking.IpAddressBookingRequirementDto;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddressPurpose;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IpTransferType;
import ru.argustelecom.box.nri.resources.requirements.ResourceSchemaDto;
import ru.argustelecom.box.nri.service.nls.CreateRequirementDMMessagesBundle;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;

/**
 * Модель формы работы с требованиями к парметрам
 * created by b.bazarov
 */
@Named(value = "createRequirementDialogModel")
@PresentationModel
public class CreateRequirementDialogModel implements Serializable {

	private static final long serialVersionUID = -5475751726151214139L;

	private static final Logger log = Logger.getLogger(CreateRequirementDialogModel.class);
	/**
	 * Коллбек для передачи на страницу требований
	 */
	@Getter
	@Setter
	private Consumer<IpAddressBookingRequirementDto> onCreateIpAddressRequirementButtonPressed;

	/**
	 * Новое требование к IP адресу
	 */
	@Getter
	private IpAddressBookingRequirementDto newIpAddressBookingRequirement;

	/**
	 * Действия после открытия диалога создания
	 *
	 * @param currentSchema текущая схема подключения
	 */
	public void onCreationDialogOpen(ResourceSchemaDto currentSchema) {
		newIpAddressBookingRequirement = IpAddressBookingRequirementDto.builder()
				.schema(currentSchema)
				.build();
	}

	/**
	 * Создать новое требование
	 */
	public void create() {
		try {
			onCreateIpAddressRequirementButtonPressed.accept(newIpAddressBookingRequirement);
		} catch (RuntimeException ex) {
			log.error("Не удалось создать требование к IP-адресу", ex);
			FacesContext context = FacesContext.getCurrentInstance();
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, LocaleUtils.getMessages(CreateRequirementDMMessagesBundle.class).couldNotCreateRequirement(),
					ex.getLocalizedMessage());
			context.addMessage(null, message);
		}
	}

	/**
	 * Получить все возможные методы передачи данных
	 *
	 * @return список возможных методов
	 */
	public List<IpTransferType> getTransferTypes() {
		return IpTransferType.listOfValues();
	}

	/**
	 * Получить все возможные назначения адресов
	 *
	 * @return список возможных назначений
	 */
	public List<IPAddressPurpose> getPurposes() {
		return IPAddressPurpose.listOfValues();
	}
}
