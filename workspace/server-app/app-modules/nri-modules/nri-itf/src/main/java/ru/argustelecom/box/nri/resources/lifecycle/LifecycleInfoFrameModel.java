package ru.argustelecom.box.nri.resources.lifecycle;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.model.diagram.Connection;
import org.primefaces.model.diagram.DefaultDiagramModel;
import org.primefaces.model.diagram.Element;
import org.primefaces.model.diagram.connector.FlowChartConnector;
import org.primefaces.model.diagram.endpoint.BlankEndPoint;
import org.primefaces.model.diagram.endpoint.EndPoint;
import org.primefaces.model.diagram.endpoint.EndPointAnchor;
import org.primefaces.model.diagram.overlay.ArrowOverlay;
import org.primefaces.model.diagram.overlay.LabelOverlay;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Фрейм с графом ЖЦ
 * Created by s.kolyada on 02.11.2017.
 */
@Named(value = "lifecycleInfoFrameModel")
@PresentationModel
public class LifecycleInfoFrameModel implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Модель ЖЦ
	 */
	@Getter
	@Setter
	private DefaultDiagramModel model;

	/**
	 * ЖЦ
	 */
	@Getter
	private ResourceLifecycleDto lifecycle;

	/**
	 * элементы с привязкой к фазам
	 */
	private Map<ResourceLifecyclePhaseDto, Element> elements;

	/**
	 * Находимся ли мы в режиме создания фазы ЖЦ
	 */
	@Getter
	private boolean phaseCreationMode = false;

	/**
	 * Сервис доступа в ЖЦ
	 */
	@Inject
	private ResourceLifecycleAppService lifecycleAppService;

	/**
	 * Выбранная фаза ЖЦ
	 */
	@Getter
	private ResourceLifecyclePhaseDto selectedphase;

	/**
	 * Инициализирует фрейм
	 * @param lifeCycleDto
	 */
	public void preRender(ResourceLifecycleDto lifeCycleDto) {
		this.lifecycle = lifeCycleDto;
		clear();
		this.model = buildPhasesGraph(lifeCycleDto);
	}

	/**
	 * Очистить параметры
	 */
	public void clear() {
		elements = null;
	}

	/**
	 * Построить граф ЖЦ
	 * @param lifecycle ЖЦ
	 * @return модель графа ЖЦ
	 */
	private DefaultDiagramModel buildPhasesGraph(ResourceLifecycleDto lifecycle) {

		DefaultDiagramModel localModel = initModel();

		elements = loadAllLifecyclePhases(lifecycle);

		if (MapUtils.isEmpty(elements)) {
			return localModel;
		}

		initConnections(elements, localModel);
		elements.values().forEach(phase -> localModel.addElement(phase));


		return localModel;
	}

	/**
	 * Инициализация связей в графе
	 * @param phases фазы ЖЦ
	 * @param localModel модель графа
	 */
	private void initConnections(Map<ResourceLifecyclePhaseDto, Element> phases, DefaultDiagramModel localModel) {
		for (Map.Entry<ResourceLifecyclePhaseDto, Element> entry : phases.entrySet()) {
			ResourceLifecyclePhaseDto phase = entry.getKey();
			for (ResourceLifecyclePhaseTransitionDto outcomingPhase : phase.getOutcomingPhases()) {
				Connection connection = createConnection(entry.getValue(), phases.get(outcomingPhase.getOutcomingPhase()), localModel);
				addConnectionComment(connection, outcomingPhase.getComment());
			}
		}
	}

	/**
	 * Инициализация модели графа
	 * @return модель графа
	 */
	private DefaultDiagramModel initModel() {
		DefaultDiagramModel localModel = new DefaultDiagramModel();
		localModel.setMaxConnections(-1);

		FlowChartConnector connector = new FlowChartConnector();
		connector.setAlwaysRespectStubs(true);
		connector.setCornerRadius(10);
		connector.setPaintStyle("{strokeStyle:'#C7B097',lineWidth:3}");
		connector.setHoverPaintStyle("{strokeStyle:'#5C738B'}");
		localModel.setDefaultConnector(connector);

		return localModel;
	}

	/**
	 * Загрузить список фаз и созщать к ним элементы графа
	 * @param lifecycle ЖЦ
	 * @return карта с фазами ЖЦ и эдементами графа
	 */
	private Map<ResourceLifecyclePhaseDto, Element> loadAllLifecyclePhases(ResourceLifecycleDto lifecycle) {

		List<ResourceLifecyclePhaseDto> phases = lifecycleAppService.loadAllLifecyclePhases(lifecycle);

		Map<ResourceLifecyclePhaseDto, Element> result = new HashMap<>();
		for (ResourceLifecyclePhaseDto phase : phases) {
			result.put(phase, createElement(phase,phase.getX(), phase.getY()));
		}
		return result;
	}

	/**
	 * Создать элемент графа
	 * @param phase фаза
	 * @param x координата х
	 * @param y координата у
	 * @return элемент
	 */
	private Element createElement(ResourceLifecyclePhaseDto phase, String x, String y) {
		Element element = new Element(phase, x, y);
		element.setId("###" + phase.getId());
		return element;
	}

	/**
	 * создать связть
	 * @param from из
	 * @param to в
	 * @param localModel модель
	 * @return связь
	 */
	private Connection createConnection(Element from, Element to, DefaultDiagramModel localModel) {
		EndPoint fromEP = createEndpoint(from, EndPointAnchor.BOTTOM);
		EndPoint toEP = createEndpoint(to, EndPointAnchor.TOP);

		Connection conn = new Connection(fromEP, toEP);
		conn.getOverlays().add(new ArrowOverlay(20, 20, 1, 1));

		localModel.connect(conn);
		return conn;
	}

	/**
	 * Добавить подпись к связи
	 * @param connection связь
 	 * @param comment подпись
	 */
	private void addConnectionComment(Connection connection, String comment) {
		if(StringUtils.isNotBlank(comment)) {
			connection.getOverlays().add(new LabelOverlay(comment, "flow-label", 0.5));
		}
	}

	/**
	 * Создать конечную точку для элемента графа
	 * @param element элемент
	 * @param anchor положение конечной точки
	 * @return конечная точка
	 */
	private EndPoint createEndpoint(Element element, EndPointAnchor anchor) {
		for (EndPoint endPoint : element.getEndPoints()) {
			if (endPoint.getAnchor().equals(anchor)) {
				return endPoint;
			}
		}
		EndPoint ep = new BlankEndPoint(anchor);
		element.addEndPoint(ep);
		return ep;
	}

	/**
	 * Колюек для создания нового перехода
	 * @return колбек
	 */
	public Callback<ResourceLifecyclePhaseTransitionDto> createNewPhaseTransition() {
		return transition -> createConnection(elements.get(transition.getIncomingPhase()), elements.get(transition.getOutcomingPhase()),
						 this.model);
	}

	/**
	 * Колбек создания новой фазы
	 * @return колбек
	 */
	public Callback<ResourceLifecyclePhaseDto> createNewPhase() {
		return phase -> elements.put(phase, createElement(phase, "5px", "5px"));
	}

	/**
	 * Получить все фазы
	 * @return все фазы
	 */
	public Set<ResourceLifecyclePhaseDto> getPhases() {
		if (MapUtils.isEmpty(elements)) {
			return Collections.emptySet();
		}
		return elements.keySet();
	}

	/**
	 * Действие по нажатии кнокпи создания перехода
	 */
	public void createTransition() {
		phaseCreationMode = false;
		RequestContext.getCurrentInstance().update("lifecycle_phase_add_dlg");
		RequestContext.getCurrentInstance().execute("PF('lifecyclePhaseAddDlg').show()");
	}

	/**
	 * Действие на нажатие кнопки создания фазы
	 */
	public void createPhase() {
		phaseCreationMode = true;
		RequestContext.getCurrentInstance().update("lifecycle_phase_add_dlg");
		RequestContext.getCurrentInstance().execute("PF('lifecyclePhaseAddDlg').show()");
	}

	/**
	 * Событие вызываемое при перемещении узла в графе
	 * Требуется для сохранения позиции узла, иначе при апдейте элемент вернётся на прежнее место
	 */
	public void onNodeMove() {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String id = params.get("node_id");
		String x = params.get("node_x");
		String y = params.get("node_y");

		// получаем из составного id собственный идентификатор элемента
		int pos = id.lastIndexOf("###");
		if (pos != -1) {
			id = id.substring(pos);
		}

		// выбираем элемент и выставляем его координаты
		Element element = model.findElement(id);
		if (element != null && coordinateUpdated(element, x, y)) {
			element.setX(x);
			element.setY(y);

			lifecycleAppService.saveCoordinates((ResourceLifecyclePhaseDto) element.getData(), x, y);
		}
	}

	/**
	 * Обработчик события клика на ноду с фазой
	 */
	public void onNodeClick() {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String id = params.get("node_id");
		// получаем из составного id собственный идентификатор элемента
		int pos = id.lastIndexOf("###");
		if (pos != -1) {
			id = id.substring(pos);
		}
		// выбираем элемент и выставляем его координаты
		Element element = model.findElement(id);
		selectedphase = (ResourceLifecyclePhaseDto)element.getData();

		RequestContext.getCurrentInstance().update("lifecycle_phase_view_dlg");
		RequestContext.getCurrentInstance().execute("PF('lifecyclePhaseViewDlg').show()");
	}

	/**
	 * Проверка изменилясь ли координая
	 * @param element элемент
	 * @param x координата х
	 * @param y координата у
	 * @return имстина если менялись, иначе ложь
	 */
	private boolean coordinateUpdated(Element element, String x, String y) {
		if (!Optional.ofNullable(element.getX()).orElse("").equals(x)) {
			return true;
		}
		if (!Optional.ofNullable(element.getY()).orElse("").equals(y)) {
			return true;
		}
		return false;
	}

	/**
	 * Изменить начальную фазу ЖЦ
	 */
	public void changeInitialPhase() {
		lifecycle = lifecycleAppService.changeLifecycleInitailPhase(lifecycle, lifecycle.getInitialPhase());
	}
}
