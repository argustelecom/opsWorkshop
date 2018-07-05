package ru.argustelecom.box.env.type.model;

public interface TypeInstanceDerivative<T extends Type, S extends TypeInstanceSpec<T>> {

	S getPrototype();

	void setPrototype(S prototype);

}
