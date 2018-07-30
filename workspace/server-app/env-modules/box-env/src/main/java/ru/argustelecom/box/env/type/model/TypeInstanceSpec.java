package ru.argustelecom.box.env.type.model;

/**
 * Маркерный интерфейс, призванный обозначить имплементирующий {@link TypeInstance} в качестве прототипа, способного
 * порождать {@link TypeInstanceDerivative производные}
 * 
 * @param <T>
 */
public interface TypeInstanceSpec<T extends Type> {

	T getType();

}
