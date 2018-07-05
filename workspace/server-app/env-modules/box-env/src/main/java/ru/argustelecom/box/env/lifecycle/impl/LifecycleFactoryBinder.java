package ru.argustelecom.box.env.lifecycle.impl;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.reflect.Modifier.isAbstract;
import static java.lang.reflect.Modifier.isPrivate;
import static ru.argustelecom.box.inf.utils.ReflectionUtils.getGenericParameterClass;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.jboss.logging.Logger;

import com.google.common.base.Strings;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleRegistry;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleFactory;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleRegistrant;
import ru.argustelecom.system.inf.logging.timing.LogTimer;

public class LifecycleFactoryBinder implements Extension {

	private static final Logger log = Logger.getLogger(LifecycleFactoryBinder.class);

	private LogTimer timer;
	private long analyzed;
	private long skipped;
	private long duplicates;
	private Set<String> processedTypes = new HashSet<>();

	protected void beforeBeanDiscovery(@Observes final BeforeBeanDiscovery discoveryEvent) {
		analyzed = 0;
		skipped = 0;
		duplicates = 0;
		timer = new LogTimer(log, "lifecycle factory discovery");
	}

	protected <F extends LifecycleFactory<?, ?>> void onTypeProcessing(@Observes ProcessAnnotatedType<F> type) {
		Class<F> factoryClass = type.getAnnotatedType().getJavaClass();
		String factoryClassName = factoryClass.getName();

		if (processedTypes.contains(factoryClassName)) {
			duplicates++;
			log.infov("Skipping duplicate type: {0}", factoryClass);
		} else {
			processedTypes.add(factoryClassName);

			boolean skipProcessing = factoryClass.isAnnotation() || factoryClass.isInterface()
					|| isAbstract(factoryClass.getModifiers()) || isPrivate(factoryClass.getModifiers());

			if (skipProcessing) {
				log.infov("Skipping (interface, annotation, abstract, private) type: {0}", factoryClass);
				skipped++;
			} else {
				log.infov("Processing type {0}", factoryClass);
				registerLifecycleFactory(factoryClass);
				analyzed++;
			}
		}
	}

	protected void afterBeanDiscovery(@Observes final AfterBeanDiscovery abd) {
		timer.logFinish("Analyzed:{0}, duplicated types skipped:{1}, types skipped:{2}", analyzed, duplicates, skipped);
		processedTypes.clear();
	}

	private <F extends LifecycleFactory<?, ?>> void registerLifecycleFactory(Class<F> factoryClass) {
		if (factoryClass.isAnnotationPresent(LifecycleRegistrant.class)) {
			Class<?> objectClass = getGenericParameterClass(factoryClass, LifecycleFactory.class, 1);
			checkState(LifecycleObject.class.isAssignableFrom(objectClass));

			LifecycleRegistrant registrant = factoryClass.getAnnotation(LifecycleRegistrant.class);
			String qualifier = registrant.qualifier();

			log.infov("Registering factory {0} with qualifier {1} for class {2}", factoryClass, qualifier, objectClass);
			registerLifecycleFactoryViaCaptureHelper(objectClass, factoryClass, qualifier);
		} else {
			log.warnv("Factory {0} does not annotated with @LifecycleRegistrant. Skip it", factoryClass);
		}
	}

	//@formatter:off
	
	@SuppressWarnings("unchecked")
	private <S extends LifecycleState<S>, O extends LifecycleObject<S>, F extends LifecycleFactory<S, O>> 
	void registerLifecycleFactoryViaCaptureHelper(Class<?> objectClass, Class<?> factoryClass, String qualifier) {
		Class<F> uncapturedFactoryClass = (Class<F>) factoryClass;
		Class<O> uncapturedObjectClass = (Class<O>) objectClass;
		doRegisterLifecycleFactory(uncapturedObjectClass, uncapturedFactoryClass, qualifier);
	}

	private <S extends LifecycleState<S>, O extends LifecycleObject<S>, F extends LifecycleFactory<S, O>> 
	void doRegisterLifecycleFactory(Class<O> objectClass, Class<F> factoryClass, String qualifier) {
		if (!Strings.isNullOrEmpty(qualifier)) {
			LifecycleRegistry.register(objectClass, factoryClass, qualifier);
		} else {
			LifecycleRegistry.register(objectClass, factoryClass);
		}
	}
	
	//@formatter:on
}
