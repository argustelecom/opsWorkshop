package ru.argustelecom.ops.workshop.model;

import lombok.Getter;
import ru.argustelecom.system.inf.modelbase.Identifiable;
import ru.argustelecom.system.inf.testframework.ut.RuntimeTestUtils;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * @author k.koropovskiy
 *
 * Это частичная копипаста с ru.argustelecom.system.inf.modelbase.SuperClass
 * Без поддержки entity_id
 */
@MappedSuperclass
public abstract class OpsSuperClass implements Identifiable{

	@Id
	@GeneratedValue
	@Getter
	private Long id;

	@Override
	public final int hashCode() {
		if (getId() != null && getId() == 0 && RuntimeTestUtils.isMockitoProxy(this)){
			//Мотивы см в equals.
			return System.identityHashCode(this);
		}

		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public final boolean equals(java.lang.Object obj) {
		//Метод final для
		// - предотвращения инициализации hibernate proxy при equals (и hashCode).
		// - симметричности
		//		Симметричность нарушается когда кто-то делает new AncesorClass() и имеется ChildClass и его equals
		//		основан на getClass()==other.getClass или instanceof

		//Метод может выполняться на экземпляре прокси. Потому что final.
		//Это может быть прокси Hibernate, Mockito. CDI прокси для entity bean не ожидаем.

		//Метод может выполняться на экземпляре (абстрактного) предка. Например Client, а не Organization. Потому что
		// - Это прокси предка. Например getReference(Client.class...
		// - Это руками созданный неперсистентный экземпляр. Например new SuperClass(organizationId), new Client(organizationId)

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof OpsSuperClass)) {
			return false;
		}

		OpsSuperClass other = (OpsSuperClass) obj;

		if ((getId() == null) || (other.getId() == null)) {
			return false;
		}

		if (getId() == 0 && other.getId() == 0 && RuntimeTestUtils.isMockitoProxy(this) && RuntimeTestUtils.isMockitoProxy(obj)){
			//Mockito для моков и spy оверайдит equals как == и не даёт мокать equals (и hashCode) (см FAQ)
			//Поэтому мы в тестах и не обязаны задавать id каждому моку, что хорошо.
			//Надо сохранить это поведение.
			//По-умолчанию для мока getId() возвращает 0 и мы опираемся на это чтобы реже делать дорогостоящий(?) isMockitoProxy
			//К тому же, если замокали getId(), то наверно ожидают основанный на нём equals.
			return this == obj;
		}

		return getId().equals(other.getId());
	}
}
