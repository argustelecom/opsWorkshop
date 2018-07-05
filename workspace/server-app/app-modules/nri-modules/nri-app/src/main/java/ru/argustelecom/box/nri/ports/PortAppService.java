package ru.argustelecom.box.nri.ports;

import org.apache.commons.lang3.Validate;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.box.nri.ports.model.Port;
import ru.argustelecom.box.nri.ports.model.PortCombo;
import ru.argustelecom.box.nri.ports.model.PortEthernet;
import ru.argustelecom.box.nri.ports.model.PortOpticSplitter;
import ru.argustelecom.box.nri.ports.model.PortOpticTransceiver;
import ru.argustelecom.box.nri.ports.model.PortPassive;
import ru.argustelecom.box.nri.ports.model.PortPon;
import ru.argustelecom.box.nri.ports.model.PortXDsl;
import ru.argustelecom.box.nri.ports.nls.PortAppServiceBundle;
import ru.argustelecom.box.nri.resources.ResourceInstanceRepository;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDto;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис работы с портами
 */
@ApplicationService
public class PortAppService implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Сервис генерации айдишников
	 */
	@Inject
	private IdSequenceService idSequenceService;
	/**
	 * Транслятор в ДТО порта
	 */
	@Inject
	private PortDtoTranslator portDtoTranslator;

	/**
	 * Транслятор в ДТО порта
	 */
	@Inject
	private PortEthernetDtoTranslator portEthernetDtoTranslator;

	/**
	 * Транслятор в ДТО порта
	 */
	@Inject
	private PortXDslDtoTranslator portXDslDtoTranslator;

	/**
	 * Транслятор в ДТО порта
	 */
	@Inject
	private PortOpticSplitterDtoTranslator portOpticSplitterDtoTranslator;

	/**
	 * Транслятор в ДТО порта
	 */
	@Inject
	private PortPonDtoTranslator portPonDtoTranslator;
	/**
	 * Транслятор в ДТО порта
	 */
	@Inject
	private PortComboDtoTranslator portComboDtoTranslator;

	/**
	 * Транслятор в ДТО порта
	 */
	@Inject
	private PortPassiveDtoTranslator portPassiveDtoTranslator;

	/**
	 * Транслятор оптического трансивера
	 */
	@Inject
	private PortOpticTransceiverDtoTranslator portPortOpticTransceiverDtoTranslator;
	/**
	 * Репозиторий портов
	 */
	@Inject
	private PortRepository portRepository;

	/**
	 * Репозиторий ресурсов
	 */
	@Inject
	private ResourceInstanceRepository resourceInstanceRepository;

	/**
	 * Получиь все порты по ресурсы
	 *
	 * @param resourceInstanceDto ресурс
	 * @return список портов
	 */
	public List<PortDto> loadAllPortsByResource(ResourceInstanceDto resourceInstanceDto) {
		if (resourceInstanceDto == null) {
			return Collections.emptyList();
		}
		return portRepository.loadAllPortsByResource(resourceInstanceDto.getId())
				.stream()
				.map(portDtoTranslator::translate)
				.collect(Collectors.toList());
	}

	/**
	 * Загрузить информацию о порте
	 *
	 * @param id идентификатор
	 * @return дто порта
	 */
	public PortDto loadPort(Long id) {
		Port port = portRepository.findById(id);
		PortDto dto;
		switch (port.getType()) {
			case ETHERNET_PORT:
				dto = portEthernetDtoTranslator.translate((PortEthernet) portRepository.findById(id));
				break;
			case XDSL_PORT:
				dto = portXDslDtoTranslator.translate((PortXDsl) portRepository.findById(id));
				break;
			case COMBO_PORT:
				dto = portComboDtoTranslator.translate((PortCombo) portRepository.findById(id));
				break;
			case OPTIC_SPLITTER:
				dto = portOpticSplitterDtoTranslator.translate((PortOpticSplitter) portRepository.findById(id));
				break;
			case PON_PORT:
				dto = portPonDtoTranslator.translate((PortPon) portRepository.findById(id));
				break;
			case PASSIVE_PORT:
				dto = portPassiveDtoTranslator.translate((PortPassive) portRepository.findById(id));
				break;
			case OPTIC_TRANSCEIVER:
				dto = portPortOpticTransceiverDtoTranslator.translate((PortOpticTransceiver) portRepository.findById(id));
				break;
			default:
				throw new UnsupportedOperationException();
		}
		return dto;
	}

	/**
	 * Сохраняем в БД порт
	 *
	 * @param port
	 */
	public void savePortEthernet(PortEthernetDto port) {
		PortEthernet eth = (PortEthernet) portFactory(port);
		eth.setMacAddress(port.getMacAddress());
		eth.setPortType(port.getPortType());
		fillCommonFields(port, eth);
		portRepository.save(eth);
	}

	/**
	 * Сохраняем в БД порт
	 *
	 * @param port
	 */
	public void savePortXDsl(PortXDslDto port) {
		PortXDsl xDsl = (PortXDsl) portFactory(port);

		xDsl.setMacAddress(port.getMacAddress());
		xDsl.setPortType(port.getPortType());
		fillCommonFields(port, xDsl);
		portRepository.save(xDsl);
	}

	/**
	 * Сохраняем в БД порт
	 *
	 * @param port
	 */
	public void savePortOpticSplitter(PortOpticSplitterDto port) {
		PortOpticSplitter opticSplitter = (PortOpticSplitter) portFactory(port);

		opticSplitter.setConnectorType(port.getConnectorType());
		opticSplitter.setRole(port.getRole());

		fillCommonFields(port, opticSplitter);
		portRepository.save(opticSplitter);
	}

	/**
	 * Сохранить комбо порт
	 *
	 * @param port
	 */
	public void savePortCombo(PortComboDto port) {
		PortCombo portCombo = (PortCombo) portFactory(port);
		portCombo.setMacAddress(port.getMacAddress());
		portCombo.setUsageType(port.getUsageType());
		portCombo.setPortType(port.getPortType());
		fillCommonFields(port, portCombo);
		portRepository.save(portCombo);
	}

	/**
	 * Сохранить PortPon
	 *
	 * @param port
	 */
	public void savePortPon(PortPonDto port) {
		PortPon portPon = (PortPon) portFactory(port);
		portPon.setConnectorType(port.getConnectorType());
		portPon.setMaxSubscriberNum(port.getMaxSubscriberNum());
		fillCommonFields(port, portPon);
		portRepository.save(portPon);
	}

	/**
	 * Сохранить PortPassive
	 *
	 * @param port
	 */
	public void savePortPassive(PortPassiveDto port) {
		PortPassive portPassive = (PortPassive) portFactory(port);

		portPassive.setPortType(port.getPortType());

		fillCommonFields(port, portPassive);
		portRepository.save(portPassive);
	}

	/**
	 * Сохранить PortOpticTransceiver
	 *
	 * @param port
	 */
	public void savePortOpticTransceiver(PortOpticTransceiverDto port) {
		PortOpticTransceiver portOpticTransceiver = (PortOpticTransceiver) portFactory(port);
		portOpticTransceiver.setMacAddress(port.getMacAddress());
		portOpticTransceiver.setFormFactor(port.getFormFactor());
		portOpticTransceiver.setWaveLength(port.getWaveLength());
		portOpticTransceiver.setSerialNum(port.getSerialNum());

		fillCommonFields(port, portOpticTransceiver);
		portRepository.save(portOpticTransceiver);
	}

	/**
	 * Создаёт новый порт или вытаскивает уже существующий
	 *
	 * @param port дто
	 * @return порт
	 */
	private Port portFactory(PortDto port) {
		Validate.isTrue(port != null, LocaleUtils.getMessages(PortAppServiceBundle.class).portIsNull());
		Validate.isTrue(port.getResourceId() != null, LocaleUtils.getMessages(PortAppServiceBundle.class).resourceIdIsNull());
		Port newPort;
		if (port.getId() == null) {
			switch (port.getType()) {
				case ETHERNET_PORT:
					newPort = new PortEthernet(idSequenceService.nextValue(Port.class));
					break;
				case XDSL_PORT:
					newPort = new PortXDsl(idSequenceService.nextValue(Port.class));
					break;
				case COMBO_PORT:
					newPort = new PortCombo(idSequenceService.nextValue(Port.class));
					break;
				case OPTIC_SPLITTER:
					newPort = new PortOpticSplitter(idSequenceService.nextValue(Port.class));
					break;
				case PON_PORT:
					newPort = new PortPon(idSequenceService.nextValue(Port.class));
					break;
				case PASSIVE_PORT:
					newPort = new PortPassive(idSequenceService.nextValue(Port.class));
					break;
				case OPTIC_TRANSCEIVER:
					newPort = new PortOpticTransceiver(idSequenceService.nextValue(Port.class));
					break;
				default:
					throw new UnsupportedOperationException();
			}
		} else
			newPort = portRepository.findById(port.getId());
		if (newPort == null)
			throw new IllegalStateException(LocaleUtils.getMessages(PortAppServiceBundle.class).didNotFindPort());
		return newPort;
	}

	/**
	 * Заполняет общие поля
	 *
	 * @param source
	 * @param target
	 */
	private void fillCommonFields(PortDto source, Port target) {

		target.setPortNumber(source.getPortNumber());
		target.setPortName(source.getPortName());
		target.setAccessTechnology(source.getAccessTechnology());
		target.setPortPurpose(source.getPortPurpose());
		target.setTechnicalCondition(source.getTechnicalCondition());
		target.setTransmissionMedium(source.getTransmissionMedium());

		ResourceInstance resourceInstance = resourceInstanceRepository.findOne(source.getResourceId());
		if (resourceInstance == null)
			throw new IllegalStateException(LocaleUtils.getMessages(PortAppServiceBundle.class).didNotFindResource());
		target.setResource(resourceInstance);
	}

	/**
	 * Удалить порт
	 *
	 * @param id ид порта
	 */
	public void deletePort(Long id) {
		portRepository.deletePort(id);
	}
}
