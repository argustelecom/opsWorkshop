package ru.argustelecom.ops.inf.modelbase;

import java.io.Serializable;
import java.util.Objects;

public abstract class SingleValueObject<V extends SingleValueObject<V, T>, T extends Comparable<T>>
		implements Serializable, Comparable<V>, Cloneable {

	public abstract T value();

	@Override
	public int hashCode() {
		return this.value().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		SingleValueObject<?, ?> that = (SingleValueObject<?, ?>) obj;
		return Objects.equals(this.value(), that.value());
	}

	@Override
	public String toString() {
		return this.value().toString();
	}

	@Override
	public int compareTo(V that) {
		return this.value().compareTo(that.value());
	}

	private static final long serialVersionUID = -2292654753610551963L;
}
