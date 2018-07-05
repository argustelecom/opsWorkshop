package ru.argustelecom.box.env.lifecycle.impl.factory;

import static com.google.common.base.Preconditions.checkState;

import java.io.Serializable;

import com.google.common.base.Strings;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.definition.Lifecycle;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleBuilder;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleContextConfigurator;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleRouteBuilder;
import ru.argustelecom.box.env.lifecycle.impl.definition.LifecycleImpl;
import ru.argustelecom.system.inf.modelbase.NamedObject;

public class LifecycleBuilderImpl<S extends LifecycleState<S>, O extends LifecycleObject<S>>
		implements LifecycleBuilder<S, O> {

	protected LifecycleImpl<S, O> lifecycle;

	public LifecycleBuilderImpl<S, O> begin() {
		lifecycle = new LifecycleImpl<>();
		return this;
	}

	@Override
	public LifecycleBuilder<S, O> keyword(Serializable keyword) {
		checkBegined();
		lifecycle.setKeyword(keyword);
		return this;
	}

	@Override
	public LifecycleBuilder<S, O> name(String name) {
		checkBegined();
		lifecycle.setName(name);
		return this;
	}

	@Override
	public LifecycleBuilder<S, O> forbid(S state, Serializable... behaviorKey) {
		checkBegined();
		lifecycle.forbid(state, behaviorKey);
		return this;
	}

	@Override
	public LifecycleBuilder<S, O> configure(LifecycleContextConfigurator<S, O> configurator) {
		checkBegined();
		lifecycle.addConfigurator(configurator);
		return this;
	}

	@Override
	public LifecycleRouteBuilder<S, O> route(Serializable routeKeyword, String routeName) {
		checkBegined();
		LifecycleRouteBuilderImpl<S, O> routeBuilder = new LifecycleRouteBuilderImpl<>(this);
		return routeBuilder.begin(routeKeyword, routeName);
	}

	@Override
	public <R extends Serializable & NamedObject> LifecycleRouteBuilder<S, O> route(R routeNamedKeyword) {
		return route(routeNamedKeyword, routeNamedKeyword.getObjectName());
	}

	public Lifecycle<S, O> build() {
		checkBegined();
		checkPreconditions();
		return lifecycle;
	}

	private void checkBegined() {
		checkState(lifecycle != null);
	}

	private void checkPreconditions() {
		checkState(lifecycle.getKeyword() != null, "Lifecycle keyword is required");
		checkState(!Strings.isNullOrEmpty(lifecycle.getName()), "Lifecycle name is required");
		checkState(!lifecycle.routes().isEmpty(), "Lifecycle must have at least one route");
	}

}
