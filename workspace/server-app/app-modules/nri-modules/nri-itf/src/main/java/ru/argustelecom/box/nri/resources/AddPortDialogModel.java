package ru.argustelecom.box.nri.resources;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.nri.ports.PortAppService;
import ru.argustelecom.box.nri.ports.PortComboDto;
import ru.argustelecom.box.nri.ports.PortDto;
import ru.argustelecom.box.nri.ports.PortEthernetDto;
import ru.argustelecom.box.nri.ports.PortOpticSplitterDto;
import ru.argustelecom.box.nri.ports.PortOpticTransceiverDto;
import ru.argustelecom.box.nri.ports.PortPassiveDto;
import ru.argustelecom.box.nri.ports.PortPonDto;
import ru.argustelecom.box.nri.ports.PortXDslDto;
import ru.argustelecom.box.nri.ports.model.ComboPortUsageType;
import ru.argustelecom.box.nri.ports.model.EthernetPortType;
import ru.argustelecom.box.nri.ports.model.OpticSplitterRole;
import ru.argustelecom.box.nri.ports.model.OpticTransceiverFormFactor;
import ru.argustelecom.box.nri.ports.model.PassivePortType;
import ru.argustelecom.box.nri.ports.model.PortPurpose;
import ru.argustelecom.box.nri.ports.model.PortTechnicalCondition;
import ru.argustelecom.box.nri.ports.model.PortType;
import ru.argustelecom.box.nri.ports.model.TransmissionMedium;
import ru.argustelecom.box.nri.ports.model.XDslPortType;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDto;
import ru.argustelecom.box.nri.resources.nls.AddPortDialogModelMessagesBundle;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Диалог создания порта
 */
@Named(value = "addPortDM")
@PresentationModel
public class AddPortDialogModel implements Serializable {
	private static final long serialVersionUID = -8529935708649881581L;

	/**
	 * Создаваемый/редактируемый ресурс
	 */
	@Getter
	private ResourceInstanceDto resource;

	/**
	 * сервис для работы с портами
	 */
	@Inject
	private PortAppService portAppService;
	/**
	 * Тип порта
	 */
	@Getter
	@Setter
	private PortType selectedType;

	/**
	 * Создаваемый порт
	 */
	@Getter
	private PortDto port;

	/**
	 * Количество создаваемых портов
	 */
	@Getter
	@Setter
	private Integer newPortsNumber = 1;

	/**
	 * Начальное значение инкремента
	 */
	@Getter
	@Setter
	private Integer initialPortNumber = 1;

	/**
	 * Надо ли добавлять инкремент к имени ресурса
	 */
	@Getter
	@Setter
	private Boolean shouldIncrementName = false;

	/**
	 * Инициализирует диалог
	 *
	 * @param resource русрс
	 */
	public void preRender(ResourceInstanceDto resource) {
		checkArgument(resource != null, LocaleUtils.getMessages(AddPortDialogModelMessagesBundle.class).resourceIsNull());
		checkArgument(resource.getSpecification() != null, LocaleUtils.getMessages(AddPortDialogModelMessagesBundle.class).resourceDoesNotHaveSpec());
		checkArgument(CollectionUtils.isNotEmpty(resource.getSpecification().getSupportedPortTypes()), LocaleUtils.getMessages(AddPortDialogModelMessagesBundle.class).resourceDoesNotSupportPorts());


		if (resource.getSpecification().getSupportedPortTypes().size() == 1)
			selectedType = resource.getSpecification().getSupportedPortTypes().iterator().next();
		if (port != null)
			port.setResourceId(resource.getId());
		this.resource = resource;

	}

	/**
	 * Создать порт по выбранному типу
	 */
	public void createPort() {
		checkArgument(selectedType != null, LocaleUtils.getMessages(AddPortDialogModelMessagesBundle.class).portTypeWasNotChosen());

		switch (selectedType) {
			case ETHERNET_PORT:
				port = PortEthernetDto.builder().portPurpose(PortPurpose.SUBSCRIBER).medium(TransmissionMedium.COPPER)
						.technicalCondition(PortTechnicalCondition.IN_ORDER).portType(EthernetPortType.PORT_TYPE_100FE).build();
				break;
			case XDSL_PORT:
				port = PortXDslDto.builder().portPurpose(PortPurpose.SUBSCRIBER).transmissionMedium(TransmissionMedium.COPPER)
						.technicalCondition(PortTechnicalCondition.IN_ORDER).portType(XDslPortType.ADSL).build();
				break;
			case COMBO_PORT:
				port = PortComboDto.builder().portPurpose(PortPurpose.TECHNOLOGICAL).transmissionMedium(TransmissionMedium.NOT_CHOSEN)
						.technicalCondition(PortTechnicalCondition.IN_ORDER).usageType(ComboPortUsageType.NOT_SPECIFIED).build();
				break;
			case OPTIC_SPLITTER:
				port = PortOpticSplitterDto.builder().portPurpose(PortPurpose.SUBSCRIBER).medium(TransmissionMedium.OPTIC)
						.technicalCondition(PortTechnicalCondition.IN_ORDER).role(OpticSplitterRole.OUTCOMING).build();
				break;
			case PON_PORT:
				port = PortPonDto.builder().portPurpose(PortPurpose.TECHNOLOGICAL).medium(TransmissionMedium.OPTIC)
						.technicalCondition(PortTechnicalCondition.IN_ORDER).maxSubscriberNum(64).build();
				break;
			case PASSIVE_PORT:
				port = PortPassiveDto.builder().portPurpose(PortPurpose.SUBSCRIBER).medium(TransmissionMedium.COPPER).portType(PassivePortType.RJ45)
						.technicalCondition(PortTechnicalCondition.IN_ORDER).build();
				break;
			case OPTIC_TRANSCEIVER:
				port = PortOpticTransceiverDto.builder().portPurpose(PortPurpose.TECHNOLOGICAL).medium(TransmissionMedium.OPTIC)
						.technicalCondition(PortTechnicalCondition.IN_ORDER).formFactor(OpticTransceiverFormFactor.SFP).build();
				break;
			default:
				throw new UnsupportedOperationException();
		}

		RequestContext.getCurrentInstance().execute("PF('portCreationPanelVar').hide()");
		RequestContext.getCurrentInstance().update("port_creation");
		RequestContext.getCurrentInstance().execute("PF('portCreationDlg').show()");
	}

	/**
	 * Очищаем порт
	 */
	public void cleanCreationParams() {
		selectedType = null;
		port = null;
		resource = null;
		newPortsNumber = 1;
		shouldIncrementName = false;
		initialPortNumber = 1;
	}

	/**
	 * Сохраняем порт в БД
	 */
	public void save() {
		checkArgument(port != null, LocaleUtils.getMessages(AddPortDialogModelMessagesBundle.class).portIsNull());
		checkArgument(resource.getSpecification().getSupportedPortTypes().contains(port.getType()), LocaleUtils.getMessages(AddPortDialogModelMessagesBundle.class).portTypeIsNotSupported());

		String name = StringUtils.isBlank(port.getPortName()) ? "" : port.getPortName();
		for (int i = 0; i < newPortsNumber; i++) {
			PortDto dto = port.clone();
			dto.setPortNumber(port.getPortNumber() != null ? port.getPortNumber() + i : i);
			dto.setPortName(name + (shouldIncrementName ? Integer.toString(initialPortNumber + i) : ""));
			createOnePort(dto);
		}
		cleanCreationParams();
	}

	/**
	 * Создаём порт
	 *
	 * @param port
	 */
	private void createOnePort(PortDto port) {

		switch (port.getType()) {
			case ETHERNET_PORT:
				portAppService.savePortEthernet((PortEthernetDto) port);
				break;
			case XDSL_PORT:
				portAppService.savePortXDsl((PortXDslDto) port);
				break;
			case COMBO_PORT:
				portAppService.savePortCombo((PortComboDto) port);
				break;
			case OPTIC_SPLITTER:
				portAppService.savePortOpticSplitter((PortOpticSplitterDto) port);
				break;
			case PON_PORT:
				portAppService.savePortPon((PortPonDto) port);
				break;
			case PASSIVE_PORT:
				portAppService.savePortPassive((PortPassiveDto) port);
				break;
			case OPTIC_TRANSCEIVER:
				portAppService.savePortOpticTransceiver((PortOpticTransceiverDto) port);
				break;
			default:
				throw new UnsupportedOperationException();
		}
	}

	/**
	 * Для заголовка
	 *
	 * @return
	 */
	public String getName() {
		return port == null ? "" : port.getType().getName();
	}


}
