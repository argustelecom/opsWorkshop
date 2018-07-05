package ru.argustelecom.box.nri.map.network.accessports;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import org.jboss.logging.Logger;
import ru.argustelecom.box.env.map.page.aspects.CurrentMapAspectSettings;
import ru.argustelecom.box.nri.resources.model.ResourceState;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationAppService;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationDto;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.page.PresentationState;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Настройки просмотра аспекта Порты доступа.
 *
 */
@PresentationState
public class AccessPortsSettingsFrameState implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(AccessPortsSettingsFrameState.class);

	@Inject
	private CurrentMapAspectSettings currentMapAspectSettings;

	private Settings settings;

	private boolean allObjectStates;

	@Inject
	private ResourceSpecificationAppService resourceSpecificationAppService;

	@Getter
	@Setter
	private List<ResourceSpecificationDto> allSpecifications;

	@PostConstruct
	private void init() {

		allSpecifications = resourceSpecificationAppService.findAllSpecifications();
		// инициализируем настройки по параметрам страницы, если есть
		Object paramSettings = currentMapAspectSettings.getValue();
		if (paramSettings != null && paramSettings instanceof Settings) {
			settings = ((Settings) paramSettings).clone();
		}
		// Иначе дефолтные
		if (settings == null) {
			settings = new Settings(Sets.newHashSet(ResourceState.values()) ,
					allSpecifications.get(0).getId());
		}

		allObjectStates = isAllSettingsObjectStatesSelected();
	}

	private boolean isAllSettingsObjectStatesSelected() {
		return getObjectStatePossibleValues().size() == settings.getObjectStates().size();
	}

	public boolean isAllObjectStates() {
		return allObjectStates;
	}

	/**
	 * Применяет настройки. Если появится автоматическое применение настроек без явной кнопки, то здесь будет проверка
	 * валидности настроек перед применением.
	 */
	public void apply() {
		log.debug("Применение настроек");
		settings.applied = true;
		// применение настроек заключается собственно в установке общего current-значения, отличного от предыдущего (по
		// ссылке)
		currentMapAspectSettings.setValue(settings.clone());
	}

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public List<ResourceState> getObjectStatePossibleValues() {
		return Arrays.asList(ResourceState.values());
	}

	/**
	 * Значение настроек аспекта. Сериализуется. Должен иметь только свойства базовых, сериализуемых типов
	 */
	public static class Settings implements Serializable, Cloneable {
		private static final long serialVersionUID = 1L;
		private Long specId;
		private Set<ResourceState> objectStates;
		private boolean applied;

		/**
		 * конструктор
		 * @param objectStates статусы
		 * @param specId идентификатор спецификации
		 */
		public Settings(Set<ResourceState> objectStates, Long specId) {
			super();
			this.objectStates = objectStates != null ? objectStates : Collections.emptySet();
			this.specId = specId;
		}

		/** Значение фильтра "По технологии" */
		@NotNull
		public Long getSpecId() {
			return specId;
		}

		public void setSpecId(Long specId) {
			this.specId = checkNotNull(specId);
		}


		/**
		 * Показывать только порты и ТПА в указанных операционных статусах. {@link #isAllObjectStates()}, то этот
		 * фильтр не учиывается.
		 *
		 * @return
		 */
		@NotEmpty
		public Set<ResourceState> getObjectStates() {
			return objectStates;
		}

		public void setObjectStates(Set<ResourceState> objectStates) {
			this.objectStates = objectStates;
		}


		/**
		 * true, если настройки были явно применены пользователем (кнопкой Показать), а не просто установлены
		 * автоматически по-умолчанию. И значит при рендеринге можно по ним получать и отображать данные. Иначе, по
		 * автоматически установленным дефолтам, не следует сразу получать данные чтобы не заставлять пользователя ждать
		 *
		 * @return
		 */
		public boolean isApplied() {
			return applied;
		}

		// клонируются ради буферизации состояния (в адресной строке сохраняются примененные, а не редактируемые)
		// отсюда же отсутствие equals - для работоспособности достаточно идентичности по ссылке
		// (ObservablePresentationState поймет, что значение другое, т.к. у клона другая ссылка)
		@Override
		protected Settings clone() {
			try {
				return (Settings) super.clone();
			} catch (CloneNotSupportedException e) {
				throw new SystemException(e);
			}
		}

		@Override
		public String toString() {
			return new ToStringBuilder(this)
					.append("spec", specId)
					.append("objectStates", objectStates)
					.append("isApplied", applied).toString();
		}
	}

}
