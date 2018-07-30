package ru.argustelecom.ops.inf.queue.api;

public interface QueueManager {

	boolean startup();

	boolean shutdown();

	QueueManagerStatus getStatus();

	boolean awaitTermination(long timeout);
}
