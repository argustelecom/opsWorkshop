package ru.argustelecom.box.inf.queue.api.context;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.argustelecom.box.inf.queue.api.model.Queue;
import ru.argustelecom.box.inf.queue.api.model.QueueEvent;

/**
 * Контекст выполнения события. Для каждого события может быть опционально определен контекст выполнения этого события.
 * В контекст разработчик может сложить всякие полезные с его точки зрения переменные и значения, такие как
 * сгенерированный идентификатор документа, ссылку на сущность, с которой будут выполняться операции при обработке
 * события и т.д.
 * <p>
 * С точки зрения кода контекст должен представлять собой простой POJO с геттерами и сеттерами. Маршаллер при
 * сериализации контектса будет обращать внимание только на поля, таким образом можно использовать логику в геттерах
 * (правда не понятно, зачем).
 * <p>
 * В качестве полей контекста могут выступать:
 * <ul>
 * <li>Примитивные типы данных</li>
 * <li>Стандартные сериализуемые объекты, такие как строки</li>
 * <li>POJO без хитрой логики в геттерах/сеттерах</li>
 * <li>Массивы</li>
 * <li>Перситстентные сущности, потомки Identifiable, обернутые в класс {@link EntityReference}</li>
 * </ul>
 */
@JsonAutoDetect(getterVisibility = NONE, isGetterVisibility = NONE, setterVisibility = NONE, fieldVisibility = ANY, creatorVisibility = ANY)
public abstract class Context implements Serializable {

	@JsonIgnore
	private QueueEvent event;

	/**
	 * Публичный конструктор для инстанцирования контекста из бизнес-кода.
	 */
	public Context() {
	}

	/**
	 * Защищенный конструктор, используемый платформой для инстанцирования контекста после его восстановления из
	 * персистентного состояния. Любой потомок контекста обязан предоставить защищенный конструктор с аналогичной
	 * сигнатурой
	 * 
	 * @param event
	 *            - событие, ассоциированное с этим контекстом
	 */
	protected Context(QueueEvent event) {
		this.event = checkNotNull(event);
	}

	/**
	 * Определяет, привязан ли контекст к событию. Если контекст привязан к событию, то происходит обработка события
	 * (был инстанцирован в процессе обработки платформенным кодом).
	 * 
	 * @return true, если контекст привязан к событию
	 */
	public final boolean isAttached() {
		return event != null;
	}

	/**
	 * Возвращает очередь, к которой привязан текущий контекст. Если контекст не ассоциирован с событием, то можно
	 * считать, что этот контекст не находится в среде обработки события/очереди
	 * 
	 * @return очередь, с которой ассоциирован текущий контекст или null, если контекст не находится в среде обработки
	 */
	public final Queue getQueue() {
		return event != null ? event.getQueue() : null;
	}

	/**
	 * Возвращает событие, к которому привязан текущий контекст. Если контекст не ассоциирован с событием, то можно
	 * считать, что этот контекст не находится в среде обработки события/очереди
	 * 
	 * @return событие, с которым ассоциирован текущий контекст или null, если контекст не находится в среде обработки
	 */
	public final QueueEvent getEvent() {
		return event;
	}

	private static final long serialVersionUID = 2295678308062360575L;
}
