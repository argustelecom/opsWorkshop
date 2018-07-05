package ru.argustelecom.box.env.numerationpattern.metamodel;

@FunctionalInterface
public interface NumerationPatternFormatCallback {
	String applyFormat(String format, Object obj);
}
