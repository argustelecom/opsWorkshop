package ru.argustelecom.box.nri.resources.lifecycle;

import lombok.Getter;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationDto;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер фрейма информации о спецификациях с заданным ЖЦ
 * Created by s.kolyada on 02.11.2017.
 */
@Named(value = "lifecycleSpecificationsInfoFrameModel")
@PresentationModel
public class LifecycleSpecificationsInfoFrame implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * ЖЦ
	 */
	@Getter
	private ResourceLifecycleDto lifecycle;

	/**
	 * Поддерживаемые спецификации
	 */
	@Getter
	private List<ResourceSpecificationDto> supportingSpecifications = new ArrayList<>();

	/**
	 * Сервис работы с ЖЦ
	 */
	@Inject
	private ResourceLifecycleAppService lifecycleAppService;

	/**
	 * Инициализирует фрейм
	 * @param lifeCycleDto
	 */
	public void preRender(ResourceLifecycleDto lifeCycleDto) {
		this.lifecycle = lifeCycleDto;
		clear();
		init();
	}

	/**
	 * Инициализация
	 */
	private void init() {
		this.supportingSpecifications = loadSupportingSpecifications(lifecycle);
	}

	/**
	 * Загрузить спецификации с жизненным циклом
	 * @param lifecycleDto ЖЦ
	 * @return список спецификаций с заданным ЖЦ
	 */
	private  List<ResourceSpecificationDto> loadSupportingSpecifications(ResourceLifecycleDto lifecycleDto) {
		return lifecycleAppService.findResourceSpecificationsWithLifecycle(lifecycleDto);
	}

	/**
	 * Очистить параметры
	 */
	public void clear() {
		supportingSpecifications.clear();
	}
}
