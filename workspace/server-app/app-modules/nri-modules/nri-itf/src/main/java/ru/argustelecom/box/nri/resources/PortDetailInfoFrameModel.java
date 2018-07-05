package ru.argustelecom.box.nri.resources;

import lombok.Getter;
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
import ru.argustelecom.box.nri.ports.model.OpticTransceiverWaveLength;
import ru.argustelecom.box.nri.ports.model.PassivePortType;
import ru.argustelecom.box.nri.ports.model.PonConnectorType;
import ru.argustelecom.box.nri.ports.model.PortPon;
import ru.argustelecom.box.nri.ports.model.PortPurpose;
import ru.argustelecom.box.nri.ports.model.PortTechnicalCondition;
import ru.argustelecom.box.nri.ports.model.XDslPortType;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Для работы с фреймом порта
 */
@Named(value = "portDetailInfoFrameModel")
@PresentationModel
public class PortDetailInfoFrameModel implements Serializable {
	private static final long serialVersionUID = 1;


	/**
	 * Серавис для работы с портами
	 */
	@Inject
	private PortAppService portAppService;

	/**
	 * Порт
	 */
	@Getter
	private PortDto port;

	/**
	 * Инициализирует фрейм
	 *
	 * @param port порт
	 */
	public void preRender(PortDto port) {

		if (port != null && port.getId() != null) {
			this.port = portAppService.loadPort(port.getId());
		} else {
			this.port = port;
		}

	}

	/**
	 * Сохранить текущий порт
	 */
	public void save() {
		switch (port.getType()) {
			case ETHERNET_PORT:
				portAppService.savePortEthernet((PortEthernetDto) port);
				break;
			case COMBO_PORT:
				portAppService.savePortCombo((PortComboDto) port);
				break;
			case XDSL_PORT:
				portAppService.savePortXDsl((PortXDslDto) port);
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
	 * Возможные технические сосстояния порта
	 *
	 * @return
	 */
	public List<PortTechnicalCondition> getPossiblePortTechnicalConditions() {
		return Arrays.asList(PortTechnicalCondition.values());
	}

	/**
	 * Возможные назаначения порта
	 *
	 * @return
	 */
	public List<PortPurpose> getPossiblePortPurposes() {
		return Arrays.asList(PortPurpose.values());
	}

	/**
	 * Возможные типя порта Ethernet порта
	 *
	 * @return
	 */
	public List<EthernetPortType> getPossibleEthernetPortTypes() {
		return Arrays.asList(EthernetPortType.values());
	}

	/**
	 * Возможные типы разъёма PON
	 *
	 * @return
	 */
	public List<PonConnectorType> getPossiblePonConnectorTypes() {
		return Arrays.asList(PonConnectorType.values());
	}

	/**
	 * Возможные типя порта xDsl порта
	 *
	 * @return
	 */
	public List<XDslPortType> getPossibleXDslPortTypes() {
		return Arrays.asList(XDslPortType.values());
	}

	/**
	 * возможные роли
	 *
	 * @return
	 */
	public List<OpticSplitterRole> getPossibleRoles() {
		return Arrays.asList(OpticSplitterRole.values());
	}

	/**
	 * Возможные знаяения форм-фактора оптического трансивера
	 *
	 * @return
	 */
	public List<OpticTransceiverFormFactor> getPossibleOpticTransceiverFormFactors() {
		return Arrays.asList(OpticTransceiverFormFactor.values());
	}

	/**
	 * Возвомодные значения длинны волны
	 *
	 * @return
	 */
	public List<OpticTransceiverWaveLength> getPossibleOpticTransceiverWaveLengths() {
		return Arrays.asList(OpticTransceiverWaveLength.values());
	}

	/**
	 * Возвомодные значения типов пассивного оборудоования
	 *
	 * @return
	 */
	public List<PassivePortType> getPossiblePassivePortTypes() {
		return Arrays.asList(PassivePortType.values());
	}

	/**
	 * Возможные значениия использования
	 *
	 * @return
	 */
	public List<ComboPortUsageType> getPossibleComboPortUsageTypes() {
		return Arrays.asList(ComboPortUsageType.values());
	}

	/**
	 * Список возможных значений максимально возможных подписчиков
	 *
	 * @return
	 */
	public List<Integer> getPossibleMaxSubscriberNums() {
		return new ArrayList<>(PortPon.AVAILABLE_MAX_SUBSCRIBER_NUM_VALUES);
	}


}
