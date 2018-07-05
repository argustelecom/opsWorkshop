package ru.argustelecom.box.nri.logicalresources.ip.subnet;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.net.util.SubnetUtils;
import org.jboss.logging.Logger;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.nri.logicalresources.ip.subnet.nls.IpSubnetCreationDialogModelMessagesBundle;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.io.Serializable;
import java.util.function.BiConsumer;

/**
 * Контроллер диалога создания новой IP подсети
 *
 * @author d.khekk
 * @since 15.12.2017
 */
@Named("ipSubnetCreationDM")
@PresentationModel
public class IpSubnetCreationDialogModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(IpSubnetCreationDialogModel.class);

	/**
	 * Минимальное значение маски подсети поумолчанию
	 */
	private static final int MINIMAL_SUBNET_MASK_DEFAULT = 20;

	/**
	 * Новая IP подсеть
	 */
	@Getter
	private IPSubnetDto newSubnet;

	/**
	 * Статические ли будут IP-адреса подсети
	 */
	@Getter
	@Setter
	private Boolean isStatic;

	/**
	 * Коллбек для передачи на страницу созданной IP подсети
	 */
	@Getter
	@Setter
	private BiConsumer<IPSubnetDto, Boolean> onCreateButtonPressed;

	/**
	 * Действия после открытия диалога создания
	 */
	public void onCreationDialogOpen() {
		newSubnet = IPSubnetDto.builder().build();
	}

	/**
	 * Создать новую IP подсеть
	 */
	public void create() {
		try {
			onCreateButtonPressed.accept(newSubnet, isStatic);
		} catch (RuntimeException ex) {
			log.error("Не удалось создать подсеть", ex);
			FacesContext context = FacesContext.getCurrentInstance();
			IpSubnetCreationDialogModelMessagesBundle messages = LocaleUtils.getMessages(IpSubnetCreationDialogModelMessagesBundle.class);
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, messages.subnetWasNotCreated(),
					ex.getLocalizedMessage());
			context.addMessage(null, message);
		}
	}

	/**
	 * Валидация имени подсети
	 * @param context контекст
	 * @param comp компонент
	 * @param value значение
	 */
	public void validateName(FacesContext context, UIComponent comp,
								Object value) {
		String name = (String) value;

		String newName;
		String mask;

		IpSubnetCreationDialogModelMessagesBundle messages = LocaleUtils.getMessages(IpSubnetCreationDialogModelMessagesBundle.class);
		try {
			mask = name.substring(name.indexOf('/')+1,name.length());
			newName = new SubnetUtils(name).getInfo().getNetworkAddress() + "/" + mask;
		} catch (IllegalArgumentException ex) {
			log.error("Адрес подсети указан в неверном формате", ex);
			((UIInput) comp).setValid(false);
			FacesContext.getCurrentInstance().validationFailed();
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, messages.wrongSubnetAddress(),
					messages.subnetAddressHasWrongFormat());
			context.addMessage(comp.getClientId(context), message);
			return;
		}


		if (!StringUtils.equals(name, newName)) {
			((UIInput) comp).setValid(false);
			FacesContext.getCurrentInstance().validationFailed();
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, messages.wrongSubnetAddress(),
					messages.maybeYouMentionedThisSubnet() + newName);
			context.addMessage(comp.getClientId(context), message);
			return;
		}

		// проверяем, что маска телефонного нормера не слишком мала
		String minMaskStr = System.getProperty("box.nri.logicalresource.ip.min-mask");
		int minMask = NumberUtils.toInt(minMaskStr, MINIMAL_SUBNET_MASK_DEFAULT);
		int iMask = NumberUtils.toInt(mask);
		if (iMask < minMask) {
			((UIInput) comp).setValid(false);
			FacesContext.getCurrentInstance().validationFailed();
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, messages.wrongSubnetAddress(),
					messages.subnetMaskLessThanMinimalValue() + minMask);
			context.addMessage(comp.getClientId(context), message);
			return;
		}
		if (iMask > 30) {
			((UIInput) comp).setValid(false);
			FacesContext.getCurrentInstance().validationFailed();
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, messages.wrongSubnetAddress(),
					messages.maskCanNotBeLongerThan());
			context.addMessage(comp.getClientId(context), message);
			return;
		}
	}
}
