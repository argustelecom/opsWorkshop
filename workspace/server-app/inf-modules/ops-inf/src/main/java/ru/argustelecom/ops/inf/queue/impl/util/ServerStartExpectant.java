package ru.argustelecom.ops.inf.queue.impl.util;

import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;

import org.jboss.logging.Logger;

import ru.argustelecom.system.inf.exception.SystemException;

/**
 * FIXME абстрагировать ServerStartedTriggerRunnable, сделать его публично доступным, устранить дублирование кода
 * <p>
 * Заимствовано из {@link ru.argustelecom.system.inf.application.ServerStartedEventTrigger ServerStartedEventTrigger}
 */
public class ServerStartExpectant {

	private MBeanServer jmxServer;
	private ObjectName appserverBean;
	private ObjectName managementBean;

	public ServerStartExpectant() {
		jmxServer = ManagementFactory.getPlatformMBeanServer();
		appserverBean = getManagedBean(APPSERVER_BEAN_NAME);
		managementBean = getManagedBean(MANAGEMENT_BEAN_NAME);
	}

	public boolean awaitServerSuccessfullyRunningInCurrentThread() {
		log.info("Начато ожидание успешного запуска сервера");
		while (getServerState().equals("starting")) {
			try {
				TimeUnit.MILLISECONDS.sleep(500);
			} catch (InterruptedException e) {
				throw new SystemException(e);
			}
		}

		boolean result = getServerState().equals("running") && checkBootErrors();
		if (result) {
			log.info("Сервер успешно запустился или уже был запущен");
		} else {
			log.fatal("Сервер запущен с ошибками или имеет неизвестное состояние");
		}
		return result;
	}

	private String getServerState() {
		return getBeanProperty(appserverBean, "serverState");
	}

	private boolean checkBootErrors() {
		CompositeData cdArray[] = (CompositeData[]) this.invokeBeanOperation(managementBean, "readBootErrors");
		if (cdArray == null) {
			return false;
		}
		return cdArray.length == 0;
	}

	private ObjectName getManagedBean(String beanName) {
		try {
			ObjectName mBean = new ObjectName(beanName);
			return mBean;
		} catch (MalformedObjectNameException e) {
			throw new SystemException("Ошибка получения бина: " + beanName, e);
		}
	}

	private String getBeanProperty(ObjectName bean, String property) {
		String attrVal = null;
		try {
			attrVal = jmxServer.getAttribute(bean, property).toString();
		} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException e) {
			throw new SystemException("Ошибка получения атрибута для бина " + bean.getCanonicalName(), e);
		}
		return attrVal;
	}

	private Object invokeBeanOperation(ObjectName bean, String operation) {
		Object data = null;
		try {
			data = jmxServer.invoke(bean, operation, null, null);
		} catch (InstanceNotFoundException | MBeanException | ReflectionException e) {
			throw new SystemException("Ошибка выполнения операции для бина " + bean.getCanonicalName(), e);
		}
		return data;
	}

	private static final String APPSERVER_BEAN_NAME = "jboss.as:management-root=server";
	private static final String MANAGEMENT_BEAN_NAME = "jboss.as:core-service=management";
	private static final Logger log = Logger.getLogger(ServerStartExpectant.class);
}
