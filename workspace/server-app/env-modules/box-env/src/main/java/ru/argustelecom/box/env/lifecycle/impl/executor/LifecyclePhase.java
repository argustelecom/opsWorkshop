package ru.argustelecom.box.env.lifecycle.impl.executor;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.jboss.logging.Logger;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseId;

abstract class LifecyclePhase implements Serializable {

	protected static final Logger log = Logger.getLogger(LifecyclePhase.class);
	private static final long serialVersionUID = 5385007776955195261L;

	@PostConstruct
	protected void postConstruct() {
		log.debugv("Phase implementer is constructed: {0}, {1}", getId(), getClass().getSimpleName());
	}

	@PreDestroy
	protected void preDestroy() {
		log.debugv("Phase implementer is destroyed: {0}, {1}", getId(), getClass().getSimpleName());
	}

	public <S extends LifecycleState<S>, O extends LifecycleObject<S>> void doPhase(
			LifecycleExecutorImpl<S, O> executor) {

		LifecyclePhaseId executorPhase = executor.getCurrentPhaseId();
		if (executorPhase != null && executorPhase.greaterOrEquals(getId())) {
			log.warnv("Lifecycle phase is already executed. [current: {0}, requested: {1}]", executorPhase, getId());
			return;
		}

		log.debugv("Executing lifecycle phase {0}", getId());
		long startTime = System.nanoTime();
		try {
			this.execute(executor);
		} finally {
			long executionTime = (System.nanoTime() - startTime) / 1000000;
			log.debugv("Execution time for phase {0}: {1}ms", getId(), executionTime);
		}
	}

	public <S extends LifecycleState<S>, O extends LifecycleObject<S>> void clean(LifecycleExecutorImpl<S, O> executor) {
		// DO NOTHING
	}

	public abstract LifecyclePhaseId getId();

	protected abstract <S extends LifecycleState<S>, O extends LifecycleObject<S>> void execute(
			LifecycleExecutorImpl<S, O> executor);

	@Override
	public String toString() {
		return String.format("LifecyclePhase[%s]", getId());
	}

}
