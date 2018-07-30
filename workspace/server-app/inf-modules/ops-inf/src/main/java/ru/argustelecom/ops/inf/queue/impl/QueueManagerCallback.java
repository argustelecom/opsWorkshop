package ru.argustelecom.ops.inf.queue.impl;

public interface QueueManagerCallback {

	boolean isActive();

	boolean fork();

}
