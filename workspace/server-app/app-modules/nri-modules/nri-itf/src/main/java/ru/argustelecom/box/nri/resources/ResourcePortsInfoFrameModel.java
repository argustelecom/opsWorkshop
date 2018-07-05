package ru.argustelecom.box.nri.resources;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.nri.ports.PortAppService;
import ru.argustelecom.box.nri.ports.PortDto;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDto;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;


/**
 * Фрейм с информацией о портах ресурса
 *
 * @author s.kolyada
 * @since 19.04.2018
 */
@Named(value = "resourcePortsInfoFM")
@PresentationModel
public class ResourcePortsInfoFrameModel implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Создаваемый/редактируемый ресурс
	 */
	@Getter
	private ResourceInstanceDto resource;

	/**
	 * Список портов
	 */
	@Getter
	private List<PortDto> portList;

	/**
	 * Серивс работы с портами
	 */
	@Inject
	private PortAppService portAppService;

	@Getter
	@Setter
	private PortDto selectedPort;

	/**
	 * Инициализирует фрейм
	 *
	 * @param resource русрс
	 */
	public void preRender(ResourceInstanceDto resource) {
		this.resource = resource;

		portList = portAppService.loadAllPortsByResource(resource);
		portList.sort(PortDto.COMPARATOR_BY_TYPE_AND_NAME);
	}

	/**
	 * Удалить выделенный порт
	 */
	public void deletePort() {
		if (selectedPort != null && selectedPort.getId() != null) {
			portAppService.deletePort(selectedPort.getId());
			selectedPort = null;
		}
	}

	/**
	 * Можно ли удалять порт
	 * @return
	 */
	public boolean canDelete(){
		///TODO надо добавить, проверку на нагрузку и бронь
		return selectedPort != null;
	}


}
