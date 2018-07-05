package ru.argustelecom.box.nri.logicalresources;

import lombok.Getter;
import org.jboss.logging.Logger;
import ru.argustelecom.box.env.type.CurrentType;
import ru.argustelecom.box.env.type.model.Type;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecification;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * Контроллер фрейма с инаформацией о логическом ресурса
 * Created by s.kolyada on 01.11.2017.
 */
@PresentationModel
public class LogicalResourceSpecAttributesFrameModel  implements Serializable {

	private static final long serialVersionUID = -2583193748075285035L;

	private static final Logger log = Logger.getLogger(LogicalResourceSpecAttributesFrameModel.class);

	/**
	 * Холдер с выбранной спецификация
	 */
	@Inject
	private CurrentType currentSpec;

	/**
	 * Выбранная спека
	 */
	private Type specification;

	/**
	 * Тип ноды лог.ресурса выбранной спеки
	 */
	@Getter
	private LogicalResNodeType resourceType;

	/**
	 * Спецификация телефонного номера
	 */
	@Getter
	private PhoneNumberSpecification phoneNumberSpec;

	/**
	 * Подготовка к отображению фрейма
	 */
	public void preRender() {
		refresh();
	}

	/**
	 * Получить иконку для текущей спецификации
	 * @return
	 */
	public String getCurrentSpecIconValue() {
		return resourceType == null ? "fa fa-question" : resourceType.getIcon();
	}

	/**
	 * Обновить данные
	 */
	private void refresh() {
		if (currentSpec.changed(phoneNumberSpec)) {
			specification = currentSpec.getValue();

			if (specification == null) {
				clear();
				return;
			}

			processSpecification(specification);
			log.debugv("postConstruct. customer_spec_id={0}", phoneNumberSpec != null ? phoneNumberSpec.getId() : null);
		}
	}

	/**
	 * Обработать данные спеки
	 * @param spec спецификация
	 */
	private void processSpecification(Type spec) {
		clear();
		if (spec instanceof PhoneNumberSpecification) {
			resourceType = LogicalResNodeType.PHONE_NUMBER;
			phoneNumberSpec = (PhoneNumberSpecification) currentSpec.getValue();
		}
	}

	/**
	 * Очистить параметры
	 */
	private void clear() {
		resourceType = null;
		phoneNumberSpec = null;
	}
}
