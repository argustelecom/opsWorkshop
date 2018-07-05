package ru.argustelecom.box.inf.queue.impl;

public interface QueueManagerCallback {

	boolean isActive();

	boolean fork();

}
