package ru.argustelecom.box.env.contract.lifecycle;

import static ru.argustelecom.box.env.lifecycle.api.definition.LifecycleVariable.of;

import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.contract.model.AbstractContractType;
import ru.argustelecom.box.env.contract.model.ContractState;
import ru.argustelecom.box.env.contract.nls.ContractMessagesBundle;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleVariable;
import ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseListener;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleFactory;
import ru.argustelecom.box.env.type.model.properties.TextProperty;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.validation.ValidationResult;

public abstract class AbstractContractLifecycle<T extends AbstractContractType, I extends AbstractContract<T>>
		implements LifecycleFactory<ContractState, I> {
	
	//@formatter:off
	public static final LifecyclePhaseListener<ContractState, AbstractContract<?>> warningsSuppressor =
		new LifecyclePhaseListener<ContractState, AbstractContract<?>>() {
			@Override
			public void beforeRouteExecution(ExecutionCtx<ContractState, ? extends AbstractContract<?>> ctx, 
					ValidationResult<Object> result) {
				
				if (result.hasWarnings() && !result.hasErrors()) {
					ctx.suppressWarnings();
				}
			}
		};
	//@formatter:on

	public static final class Variables {

		public static final LifecycleVariable<TextProperty> COMMENT = of(TextProperty.class, var -> {
			ContractMessagesBundle messages = LocaleUtils.getMessages(ContractMessagesBundle.class);

			var.setName(messages.lifecycleVariableComment());
			var.setHint(messages.lifecycleVariableCommentHint());
			var.setLinesCount(5);
		});

		private Variables() {
		}
	}

}
