package ru.argustelecom.box.inf.queue.api;

import java.util.Date;

import ru.argustelecom.box.inf.queue.api.context.Context;
import ru.argustelecom.box.inf.queue.api.model.QueueEvent;
import ru.argustelecom.system.inf.modelbase.Identifiable;

public interface QueueProducer {

	QueueEvent schedule(Identifiable queueObject, String groupId, Priority priority, Date scheduledTime,
			String handlerName, Context context);

	QueueEvent schedule(String queueId, String groupId, Priority priority, Date scheduledTime, String handlerName,
			Context context);

	void remove(Identifiable queueObject);

	void remove(String queueId);

	boolean restart(Identifiable queueObject);

	boolean restart(String queueId);

	enum Priority {

		// Значения приоритетов должны идти строго по порядку от минимального (высокий приоритет) до максимального
		// (низкий приоритет) с инкрементом 1. Иначе сломается valueOf

		HIGHEST(1), HIGH(2), MEDIUM(3), LOW(4), LOWEST(5);

		private int value;

		Priority(int value) {
			this.value = value;
		}

		public int value() {
			return value;
		}

		public static Priority valueOf(int priorityValue) {
			if (priorityValue < HIGHEST.value)
				return HIGHEST;
			if (priorityValue > LOWEST.value)
				return LOWEST;

			return values()[priorityValue - 1];
		}
	}

}
