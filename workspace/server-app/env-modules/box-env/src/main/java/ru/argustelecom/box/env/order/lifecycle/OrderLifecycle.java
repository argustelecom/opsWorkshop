package ru.argustelecom.box.env.order.lifecycle;

import static ru.argustelecom.box.env.lifecycle.api.definition.LifecycleVariable.of;
import static ru.argustelecom.box.env.order.lifecycle.OrderLifecycle.Behaviors.addOffer;
import static ru.argustelecom.box.env.order.lifecycle.OrderLifecycle.Behaviors.addRequirement;
import static ru.argustelecom.box.env.order.lifecycle.OrderLifecycle.Behaviors.assignee;
import static ru.argustelecom.box.env.order.lifecycle.OrderLifecycle.Behaviors.connectionAddress;
import static ru.argustelecom.box.env.order.lifecycle.OrderLifecycle.Behaviors.connectionAddressComment;
import static ru.argustelecom.box.env.order.lifecycle.OrderLifecycle.Behaviors.createContract;
import static ru.argustelecom.box.env.order.lifecycle.OrderLifecycle.Behaviors.createContractExtension;
import static ru.argustelecom.box.env.order.lifecycle.OrderLifecycle.Behaviors.dueDate;
import static ru.argustelecom.box.env.order.lifecycle.OrderLifecycle.Behaviors.priority;
import static ru.argustelecom.box.env.order.model.OrderState.ARCHIVE;
import static ru.argustelecom.box.env.order.model.OrderState.FORMALIZATION;
import static ru.argustelecom.box.env.order.model.OrderState.IN_PROGRESS;
import static ru.argustelecom.box.env.order.model.OrderState.POSTPONED;

import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleVariable;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleBuilder;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleFactory;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleRegistrant;
import ru.argustelecom.box.env.order.lifecycle.action.DoWriteComment;
import ru.argustelecom.box.env.order.lifecycle.validator.MustHaveAddressInfo;
import ru.argustelecom.box.env.order.lifecycle.validator.MustHaveContactInfo;
import ru.argustelecom.box.env.order.lifecycle.validator.MustHaveNoContractsInRegistration;
import ru.argustelecom.box.env.order.model.Order;
import ru.argustelecom.box.env.order.model.OrderState;
import ru.argustelecom.box.env.order.nls.OrderMessagesBundle;
import ru.argustelecom.box.env.type.model.properties.TextProperty;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

@LifecycleRegistrant
public class OrderLifecycle implements LifecycleFactory<OrderState, Order> {

	@Override
	public void buildLifecycle(LifecycleBuilder<OrderState, Order> lifecycle) {

		lifecycle.keyword(getClass().getSimpleName());
		lifecycle.name("Жизненный цикл заявки");

		// @formatter:off
		lifecycle.route(Routes.START_PROGRESS, Routes.START_PROGRESS.getName())
			.from(FORMALIZATION)
			.from(POSTPONED)
			.to(IN_PROGRESS)
				.silent(false)
				.contextVar(Variables.COMMENT)
				.validate(MustHaveAddressInfo.class)
				.validate(MustHaveContactInfo.class)
				.execute(DoWriteComment.class)
			.end()
		.end();

		lifecycle.route(Routes.CLOSE, Routes.CLOSE.getName())
			.from(FORMALIZATION)
			.from(IN_PROGRESS)
			.from(POSTPONED)
			.to(ARCHIVE)
				.silent(false)
				.contextVar(Variables.COMMENT)
				.validate(MustHaveNoContractsInRegistration.class)
				.execute(DoWriteComment.class)
			.end()
		.end();

		lifecycle.route(Routes.POSTPONE, Routes.POSTPONE.getName())
			.from(IN_PROGRESS)
			.to(POSTPONED)
				.silent(false)
				.contextVar(Variables.COMMENT)
				.execute(DoWriteComment.class)
			.end()
		.end();
		//@formatter:on

		lifecycle.forbid(POSTPONED, addRequirement, addOffer, createContract, createContractExtension);

		lifecycle.forbid(ARCHIVE, priority, dueDate, assignee, connectionAddress, connectionAddressComment,
				createContract, createContractExtension, addRequirement, addOffer);
	}

	public enum Routes {
		START_PROGRESS,
		CLOSE,
		POSTPONE;

		public String getName() {
			OrderMessagesBundle messages = LocaleUtils.getMessages(OrderMessagesBundle.class);

			switch (this) {
				case START_PROGRESS:
					return messages.routeStartProgress();
				case CLOSE:
					return messages.routeClose();
				case POSTPONE:
					return messages.routePostpone();
				default:
					throw new SystemException("Unsupported OrderLifecycle.Routes");
			}
		}
	}

	public static final class Variables {

		public static final LifecycleVariable<TextProperty> COMMENT = of(TextProperty.class, var -> {
			OrderMessagesBundle messages = LocaleUtils.getMessages(OrderMessagesBundle.class);

			var.setName(messages.lifecycleVariableComment());
			var.setHint(messages.lifecycleVariableCommentHint());
			var.setLinesCount(5);
		});

		private Variables() {
		}
	}

	public enum Behaviors {
		//@formatter:off
		priority,
		dueDate,
		assignee,
		connectionAddress,
		connectionAddressComment,
		createContract,
		createContractExtension,
		addRequirement,
		addOffer,
		addComment,
		addAttachment
		//@formatter:on
	}

}
