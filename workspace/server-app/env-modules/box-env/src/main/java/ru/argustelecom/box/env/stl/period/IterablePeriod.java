package ru.argustelecom.box.env.stl.period;

public interface IterablePeriod<P extends Period> {

	P next();

	P prev();

	default boolean isIterable() {
		return true;
	}

}
