package ru.argustelecom.box.env.lifecycle;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleRegistry;
import ru.argustelecom.box.env.lifecycle.api.LifecycleRoutingService;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.definition.Lifecycle;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleRoute;
import ru.argustelecom.box.env.lifecycle.api.executor.LifecycleExecutor;
import ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseListener;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
@Named("lifecycleFm")
public class LifecycleFrameModel implements Serializable {

	private static final long serialVersionUID = -5251958035066944840L;

	@Inject
	private LifecycleRegistry registry;

	@Inject
	private LifecycleRoutingService routings;

	@Inject
	private LifecycleRoutingDialogModel routingDlg;

	/**
	 * Определяет, можно ли рендерить элемент управления жизненным циклом для указанного объекта
	 * 
	 * @param businessObject
	 * @return
	 */
	public <S extends LifecycleState<S>, O extends LifecycleObject<S>> boolean isRendered(O businessObject) {
		if (businessObject != null) {
			Lifecycle<S, O> lifecycle = registry.getLifecycle(businessObject);
			return lifecycle.controlledByUser().hasRoutes(businessObject);
		}
		return false;
	}

	public <S extends LifecycleState<S>, O extends LifecycleObject<S>> Collection<LifecycleRoute<S, O>> getRoutes(
			O businessObject) {
		
		if (businessObject != null) {
			Lifecycle<S, O> lifecycle = registry.getLifecycle(businessObject);
			return lifecycle.controlledByUser().getRoutes(businessObject);
		}
		return Collections.emptyList();
	}

	public <S extends LifecycleState<S>, O extends LifecycleObject<S>> LifecycleRoute<S, O> getMainRoute(
			O businessObject) {

		if (businessObject != null) {
			Lifecycle<S, O> lifecycle = registry.getLifecycle(businessObject);
			return lifecycle.controlledByUser().getMainRoute(businessObject);
		}
		return null;
	}

	public <S extends LifecycleState<S>, O extends LifecycleObject<S>> Collection<LifecycleRoute<S, O>> getSecondaryRoutes(
			O businessObject) {

		if (businessObject != null) {
			Lifecycle<S, O> lifecycle = registry.getLifecycle(businessObject);
			return lifecycle.controlledByUser().getSecondaryRoutes(businessObject);
		}
		return Collections.emptyList();
	}

	public <S extends LifecycleState<S>, O extends LifecycleObject<S>> void performRoute(O businessObject,
			LifecyclePhaseListener<S, O> phaseListener) {

		performRoute(businessObject, getMainRoute(businessObject), phaseListener);
	}

	public <S extends LifecycleState<S>, O extends LifecycleObject<S>> void performRoute(O businessObject,
			LifecycleRoute<S, O> route, LifecyclePhaseListener<S, O> phaseListener) {

		if (route == null) {
			return;
		}

		LifecycleExecutor<S, O> executor = routings.createExecutor(businessObject, route);
		if (phaseListener != null) {
			executor.addPhaseListener(phaseListener);
		}

		// Здесь определяется конечная точка для всех случаев маршрутизации (с условными переходами и без)
		// После этой операции конечная точка будет доступна для использования. Важно понимать, что после этой операции
		// (точнее, на фазе инициализации) будет отправлено событие о начале изменения состояния бизнес-объекта
		executor.determineRouteEndpoint();

		// Если переход не "тихий", то мы предполагаем, что могут быть переменные или расширения диалога маршрутизации
		// Т.е. могут быть элементы, требующие взаимодействия с пользователем
		// Наличие переменных мы можем проверить, однако наличие хардкодных расширений диалога маршрутизации мы
		// проверить никаким способом не можем. Поэтому будем показывать диалог перехода всегда для всех "не тихих"
		// переходов (возможно, в некоторых случаях это будет избыточным, однако, может быть изменено правильной
		// настройкой жизненного цикла)
		if (!executor.getEndpoint().isSilent()) {
			// Жизненный цикл хочет взаимодействовать с пользователем.
			// Диалог маршрутизации ориентируется на текущее состояние исполнителя. Поэтому в текущем кейсе покажет
			// свой первый фрейм, даже если в нем нет хардкодных расширений или переменных контекста перехода
			// Соответственно, дальнейший переход по жизненному циклу будет управляться из диалога
			showRoutingDialog(executor);
		} else {
			// Если переход был тихим, то фрейм должен попытаться довести работу по переходу по маршруту жизненного
			// цикла до конца. Для этого он выполнит валидацию (если определены валидаторы). Если сработает warning или
			// error валидации, то мы забиваем на желание выполнить переход "по тихому" (т.к. ситуация не стандартная и
			// может потенциально привести к проблемам) и насильно показываем пользователю диалог маршрутизации. В этом
			// случае диалог будет показан начиная со второго экрана (просмотр результатов валидации). На этом экране
			// пользователь сможет ознакомиться с issues и, если там одни только warnings, продолжить переход. Т.к.
			// переход тихий, то возможности вернуться на экран указания параметров у пользователя не будет.

			// Если в режиме "тихого" перехода по результатам валидации появились infos, то они не будут показаны, если
			// нет issues большего калибра. Т.е. веса info недостаточно для того, чтобы проигнорировать "тихий" режим и
			// насильно показать пользователю диалог валидации
			executor.validateRoute();
			if (!executor.getValidationResult().isSuccess(false)) {
				showRoutingDialog(executor);
			} else {
				// Если валидация была успешной, то завершим переход. При вызове этой операции будут выполнены
				// оставшиеся фазы перехода (Execute и Finalize), а также будет сохранена история и разосланы события об
				// окончании перехода
				executor.finalizeRouting();
			}
		}
	}

	private <S extends LifecycleState<S>, O extends LifecycleObject<S>> void showRoutingDialog(
			LifecycleExecutor<S, O> executor) {
		routingDlg.setExecutor(executor);
		RequestContext.getCurrentInstance().update("lifecycle_routing_dlg_form");
		RequestContext.getCurrentInstance().execute("PF('lifecycleRoutingDlgVar').show()");
	}

	public static class RouteActionModel implements Serializable {

		private static final long serialVersionUID = 3472572696989405765L;

		private Set<RouteActionModelItem> items = new HashSet<>();

		@Getter
		@Setter
		private String defaultIcon;

		@Getter
		@Setter
		private String defaultStyleClass;

		protected RouteActionModelItem get(Serializable routeKeyword) {
			for (RouteActionModelItem item : items) {
				if (item.forRoute(routeKeyword)) {
					return item;
				}
			}
			return null;
		}

		public void put(String icon, String styleClass, Serializable... routeKeywords) {
			items.add(new RouteActionModelItem(icon, styleClass, routeKeywords));
		}

		public String getIcon(Serializable routeKeyword) {
			RouteActionModelItem item = get(routeKeyword);
			return item != null ? item.getIcon() : defaultIcon;
		}

		public String getStyleClass(Serializable routeKeyword) {
			RouteActionModelItem item = get(routeKeyword);
			return item != null ? item.getIcon() : defaultStyleClass;
		}
	}

	@EqualsAndHashCode(of = "routeKeywords")
	private static class RouteActionModelItem implements Serializable {

		private static final long serialVersionUID = 2410773521101941321L;

		private Set<Serializable> routeKeywords = new HashSet<>();

		@Getter
		private String icon;

		@Getter
		private String styleClass;

		RouteActionModelItem(String icon, String styleClass, Serializable... routeKeywords) {
			checkRequiredArgument(icon, "icon");
			checkRequiredArgument(styleClass, "styleClass");
			checkRequiredArgument(routeKeywords, "routeKeywords");
			checkArgument(routeKeywords.length != 0);

			this.icon = icon;
			this.styleClass = styleClass;

			for (Serializable keyword : routeKeywords) {
				this.routeKeywords.add(keyword);
			}
		}

		boolean forRoute(Serializable routeKeyword) {
			return routeKeywords.contains(routeKeyword);
		}
	}
}
