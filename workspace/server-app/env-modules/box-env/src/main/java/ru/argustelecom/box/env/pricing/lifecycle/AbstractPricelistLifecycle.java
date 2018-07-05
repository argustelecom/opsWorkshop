package ru.argustelecom.box.env.pricing.lifecycle;

import static ru.argustelecom.box.env.lifecycle.api.definition.LifecycleVariable.of;
import static ru.argustelecom.box.env.pricing.lifecycle.AbstractPricelistLifecycle.Behaviors.editAttributes;
import static ru.argustelecom.box.env.pricing.lifecycle.AbstractPricelistLifecycle.Behaviors.editEntry;
import static ru.argustelecom.box.env.pricing.lifecycle.AbstractPricelistLifecycle.Behaviors.modifyEntries;
import static ru.argustelecom.box.env.pricing.lifecycle.AbstractPricelistLifecycle.Behaviors.modifySegments;
import static ru.argustelecom.box.env.pricing.lifecycle.AbstractPricelistLifecycle.Behaviors.remove;
import static ru.argustelecom.box.env.pricing.model.PricelistState.CANCELLED;
import static ru.argustelecom.box.env.pricing.model.PricelistState.CLOSED;
import static ru.argustelecom.box.env.pricing.model.PricelistState.CREATED;
import static ru.argustelecom.box.env.pricing.model.PricelistState.INFORCE;

import lombok.AllArgsConstructor;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleVariable;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleBuilder;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleFactory;
import ru.argustelecom.box.env.order.nls.OrderMessagesBundle;
import ru.argustelecom.box.env.pricing.lifecycle.action.DoWriteComment;
import ru.argustelecom.box.env.pricing.lifecycle.validator.MustBeEmpty;
import ru.argustelecom.box.env.pricing.lifecycle.validator.MustHaveAnyOffering;
import ru.argustelecom.box.env.pricing.lifecycle.validator.MustStillValid;
import ru.argustelecom.box.env.pricing.lifecycle.validator.MustWarnOnClose;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.box.env.pricing.model.PricelistState;
import ru.argustelecom.box.env.pricing.nls.PricelistMessagesBundle;
import ru.argustelecom.box.env.type.model.properties.TextProperty;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

public abstract class AbstractPricelistLifecycle<P extends AbstractPricelist>
		implements LifecycleFactory<PricelistState, P> {

	@Override
	public void buildLifecycle(LifecycleBuilder<PricelistState, P> lifecycle) {

		lifecycle.keyword(getClass().getSimpleName());
		lifecycle.name("Жизненный цикл прайс-листа");

		//@formatter:off
        lifecycle.route(Routes.ACTIVATE, Routes.ACTIVATE.getName())
            .from(CREATED)
            .to(INFORCE)
            	.silent(false)
            	.contextVar(Variables.COMMENT)
                .validate(MustHaveAnyOffering.class)
                .validate(MustStillValid.class)
                .execute(DoWriteComment.class)
            .end()
        .end();
        
        lifecycle.route(Routes.CANCEL, Routes.CANCEL.getName())
		    .from(CREATED)
		    .to(CANCELLED)
		        .validate(MustBeEmpty.class)
		    .end()
		.end();

        lifecycle.route(Routes.CLOSE, Routes.CLOSE.getName())
            .from(INFORCE)
            .to(CLOSED)
                .silent(false)
            	.contextVar(Variables.COMMENT)
                .validate(MustWarnOnClose.class)
                .execute(DoWriteComment.class)
            .end()
        .end();
        //@formatter:on

		lifecycle.forbid(CREATED, remove);
		lifecycle.forbid(INFORCE, remove, modifyEntries, editEntry, editAttributes);
		lifecycle.forbid(CLOSED, remove, editAttributes, modifyEntries, editEntry, modifySegments);
		lifecycle.forbid(CANCELLED, editAttributes, modifyEntries, editEntry, modifySegments);
	}

	public enum Routes {
		ACTIVATE,
		CANCEL,
		CLOSE;

		public String getName() {
			PricelistMessagesBundle messages = LocaleUtils.getMessages(PricelistMessagesBundle.class);

			switch (this) {
				case ACTIVATE:
					return messages.routeActivate();
				case CANCEL:
					return messages.routeCancel();
				case CLOSE:
					return messages.routeClose();
				default:
					throw new SystemException("Unsupported AbstractPricelistLifecycle.Routes");
			}
		}
	}

	public static final class Variables {

		public static final LifecycleVariable<TextProperty> COMMENT = of(TextProperty.class, var -> {
			PricelistMessagesBundle messages = LocaleUtils.getMessages(PricelistMessagesBundle.class);

			var.setName(messages.lifecycleVariableComment());
			var.setHint(messages.lifecycleVariableCommentHint());
			var.setLinesCount(5);
		});

		private Variables() {
		}
	}

	public enum Behaviors {
		remove, editAttributes, modifyEntries, editEntry, modifySegments
	}
}
